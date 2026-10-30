require('dotenv').config();
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

async function seedEmptyTables() {
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
        ssl: sslConfig
    });

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0;');
        const dbName = process.env.DB_NAME || 'defaultdb';
        const [tablesRes] = await connection.query('SHOW TABLES');
        const tableNames = tablesRes.map(r => r[`Tables_in_${dbName}`] || Object.values(r)[0]);

        for (const tableName of tableNames) {
            const [countRes] = await connection.query(`SELECT COUNT(*) as count FROM \`${tableName}\``);
            const count = countRes[0].count;

            if (count === 0) {
                console.log(`Seeding table: ${tableName} (was 0 records)...`);
                // Get columns
                const [columnsRes] = await connection.query(`SHOW COLUMNS FROM \`${tableName}\``);
                const insertCols = [];
                const insertVals = [];

                columnsRes.forEach(col => {
                    const colName = col.Field;
                    const type = col.Type.toLowerCase();
                    const key = col.Key;
                    const nullAllowed = col.Null === 'YES';
                    const defaultVal = col.Default;

                    // Skip auto-increment primary keys
                    if (key === 'pri' && col.Extra.includes('auto_increment')) {
                        return;
                    }

                    insertCols.push(`\`${colName}\``);

                    if (type.includes('int') || type.includes('decimal') || type.includes('float') || type.includes('double')) {
                        insertVals.push(1);
                    } else if (type.includes('date') || type.includes('time')) {
                        insertVals.push(new Date().toISOString().slice(0, 19).replace('T', ' '));
                    } else {
                        insertVals.push('Sample ' + tableName);
                    }
                });

                if (insertCols.length > 0) {
                    const sql = `INSERT INTO \`${tableName}\` (${insertCols.join(', ')}) VALUES (${insertVals.map(() => '?').join(', ')})`;
                    try {
                        await connection.query(sql, insertVals);
                        console.log(`✅ Seeded ${tableName}`);
                    } catch (e) {
                        console.log(`⚠️ Skipped ${tableName}: ${e.message}`);
                    }
                }
            }
        }

        console.log('\n✅ Successfully populated all empty tables!');
    } catch (e) {
        console.error('Error seeding empty tables:', e.message);
    } finally {
        await connection.end();
    }
}

seedEmptyTables();
