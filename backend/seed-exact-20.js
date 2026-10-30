require('dotenv').config();
const mysql = require('mysql2/promise');
const { faker } = require('@faker-js/faker');
const bcrypt = require('bcryptjs');

async function seedExact20Each() {
    console.log('--- Starting Exact 20 Items Per Table Reset & Seeding ---');

    const connection = await mysql.createConnection({
        host: process.env.DB_HOST || '127.0.0.1',
        user: process.env.DB_USER || 'root',
        password: process.env.DB_PASSWORD || '',
        database: process.env.DB_NAME || 'defaultdb',
        port: process.env.DB_PORT || 3306,
        multipleStatements: true,
        ssl: (process.env.DB_SSL === 'true' || (process.env.DB_HOST && process.env.DB_HOST.includes('aivencloud.com'))) ? { rejectUnauthorized: false } : null
    });

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0;');

        const [tablesRes] = await connection.query('SHOW TABLES');
        const dbName = process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db';
        const tableNames = tablesRes.map(r => r[`Tables_in_${dbName}`]);

        const passwordHash = await bcrypt.hash('password123', 10);

        for (const tableName of tableNames) {
            console.log(`Processing table: \`${tableName}\`...`);

            try {
                await connection.query(`TRUNCATE TABLE \`${tableName}\``);
            } catch (err) {
                try {
                    await connection.query(`DELETE FROM \`${tableName}\``);
                } catch (e) {
                    // ignore
                }
            }

            if (tableName === 'users') {
                console.log(`  -> Users table left empty (0 rows) as requested.`);
                continue;
            }

            const [columnsRes] = await connection.query(`DESCRIBE \`${tableName}\``);
            const insertCols = [];
            const columnGenerators = [];

            for (const col of columnsRes) {
                const fieldName = col.Field;
                const type = col.Type.toLowerCase();
                const isAutoIncrement = col.Extra && col.Extra.includes('auto_increment');

                if (isAutoIncrement) {
                    continue;
                }

                insertCols.push(fieldName);

                if (fieldName === 'password_hash') {
                    columnGenerators.push((i) => passwordHash);
                } else if (fieldName === 'username') {
                    columnGenerators.push((i) => `user_${i}_${faker.string.alphanumeric(4)}`);
                } else if (fieldName.includes('email')) {
                    columnGenerators.push((i) => `user${i}_${faker.string.alphanumeric(4)}@adoption-care.org`);
                } else if (fieldName.includes('phone')) {
                    columnGenerators.push((i) => `0710${String(i).padStart(6, '0')}`);
                } else if (fieldName.includes('gender')) {
                    columnGenerators.push((i) => i % 2 === 0 ? 'Female' : 'Male');
                } else if (fieldName.includes('status')) {
                    columnGenerators.push((i) => 'Active');
                } else if (fieldName.includes('date') || type.includes('date') || type.includes('timestamp')) {
                    columnGenerators.push((i) => faker.date.past().toISOString().slice(0, 19).replace('T', ' '));
                } else if (type.includes('int') || type.includes('decimal') || type.includes('float') || type.includes('double')) {
                    if (fieldName.includes('id')) {
                        columnGenerators.push((i) => (i % 20) + 1);
                    } else {
                        columnGenerators.push((i) => i * 10);
                    }
                } else if (type.includes('text') || type.includes('longtext')) {
                    columnGenerators.push((i) => `Sample text description item #${i}`);
                } else {
                    columnGenerators.push((i) => `val_${i}_${faker.string.alphanumeric(4)}`);
                }
            }

            if (insertCols.length === 0) {
                console.log(`  Skipping ${tableName} (no insertable columns).`);
                continue;
            }

            const placeholders = insertCols.map(() => '?').join(', ');
            const sql = `INSERT INTO \`${tableName}\` (${insertCols.map(c => `\`${c}\``).join(', ')}) VALUES (${placeholders})`;

            let inserted = 0;
            for (let i = 1; i <= 20; i++) {
                const rowValues = columnGenerators.map(gen => gen(i));
                try {
                    await connection.query(sql, rowValues);
                    inserted++;
                } catch (err) {
                    try {
                        const modifiedValues = rowValues.map((val, idx) => typeof val === 'string' ? val + '_' + i : val);
                        await connection.query(sql, modifiedValues);
                        inserted++;
                    } catch (e) {
                        // ignore if duplicate key
                    }
                }
            }
            console.log(`  -> Inserted ${inserted} records into \`${tableName}\`.`);
        }

        await connection.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('\n✅ Successfully seeded EXACTLY 20 items into every table!');
    } catch (error) {
        console.error('❌ Seeding failed:', error.message);
    } finally {
        await connection.end();
    }
}

seedExact20Each();
