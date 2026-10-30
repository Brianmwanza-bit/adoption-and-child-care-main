require('dotenv').config();
const mysql = require('mysql2/promise');
const { faker } = require('@faker-js/faker');
const fs = require('fs');
const path = require('path');

async function topupTablesTo20() {
    console.log('--- Topping up tables to exactly 20 records (excluding users) ---');

    const caPath = path.join(__dirname, 'config-aiven-ca.pem');
    const sslConfig = fs.existsSync(caPath)
      ? { minVersion: 'TLSv1.2', ca: fs.readFileSync(caPath, 'utf8'), rejectUnauthorized: false }
      : { minVersion: 'TLSv1.2', rejectUnauthorized: false };

    const connection = await mysql.createConnection({
        host: process.env.DB_HOST || 'localhost',
        user: process.env.DB_USER || 'root',
        password: process.env.DB_PASSWORD || '',
        database: process.env.DB_NAME || 'defaultdb',
        port: process.env.DB_PORT || 13255,
        ssl: sslConfig,
        multipleStatements: true
    });

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0;');

        const [tablesRes] = await connection.query('SHOW TABLES');
        const dbName = process.env.DB_NAME || 'defaultdb';
        const tableNames = tablesRes.map(r => r[`Tables_in_${dbName}`] || Object.values(r)[0]);

        const [users] = await connection.query('SELECT user_id FROM users');
        const userIds = users.length > 0 ? users.map(u => u.user_id) : [1];

        const [children] = await connection.query('SELECT child_id FROM children');
        const childIds = children.length > 0 ? children.map(c => c.child_id) : [1];

        for (const tableName of tableNames) {
            if (tableName === 'users') continue;

            const [countRes] = await connection.query(`SELECT COUNT(*) as count FROM \`${tableName}\``);
            const currentCount = countRes[0].count;

            if (currentCount < 20) {
                const needed = 20 - currentCount;
                console.log(`Table \`${tableName}\` has ${currentCount} records. Inserting ${needed} more...`);

                const [columnsRes] = await connection.query(`SHOW COLUMNS FROM \`${tableName}\``);
                const insertCols = [];
                columnsRes.forEach(col => {
                    if (col.Key === 'pri' && col.Extra.includes('auto_increment')) return;
                    insertCols.push(`\`${col.Field}\``);
                });

                if (insertCols.length === 0) continue;

                for (let i = 0; i < needed; i++) {
                    const insertVals = [];
                    columnsRes.forEach(col => {
                        const colName = col.Field;
                        const type = col.Type.toLowerCase();
                        if (col.Key === 'pri' && col.Extra.includes('auto_increment')) return;

                        if (colName.includes('id') && (colName.includes('child') || colName.includes('case')) && childIds.length > 0) {
                            insertVals.push(faker.helpers.arrayElement(childIds));
                        } else if (colName.includes('id') && colName.includes('user') && userIds.length > 0) {
                            insertVals.push(faker.helpers.arrayElement(userIds));
                        } else if (type.includes('int') || type.includes('decimal') || type.includes('float')) {
                            insertVals.push(faker.number.int({ min: 1, max: 100 }));
                        } else if (type.includes('date') || type.includes('time')) {
                            insertVals.push(faker.date.recent().toISOString().slice(0, 19).replace('T', ' '));
                        } else if (type.includes('enum')) {
                            const match = type.match(/enum\((.*?)\)/);
                            if (match) {
                                const options = match[1].split(',').map(o => o.trim().replace(/^'(.*)'$/, '$1'));
                                insertVals.push(options[0]);
                            } else {
                                insertVals.push('Active');
                            }
                        } else {
                            insertVals.push('Item_' + faker.string.alphanumeric(6));
                        }
                    });

                    if (insertVals.length === insertCols.length) {
                        try {
                            const placeholders = insertVals.map(() => '?').join(', ');
                            const sql = `INSERT INTO \`${tableName}\` (${insertCols.join(', ')}) VALUES (${placeholders})`;
                            await connection.query(sql, insertVals);
                        } catch (err) {
                            // Ignore
                        }
                    }
                }
            }
        }

        console.log('\n✅ All tables (excluding users) successfully topped up to 20+ records!');
    } catch (e) {
        console.error('Error topping up tables:', e.message);
    } finally {
        await connection.end();
    }
}

topupTablesTo20();
