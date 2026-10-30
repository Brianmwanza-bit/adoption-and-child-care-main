require('dotenv').config();
const mysql = require('mysql2/promise');
const { faker } = require('@faker-js/faker');

async function seedDynamic20() {
    console.log('--- Starting Dynamic Seeding (At least 20 items per table) ---');

    const connection = await mysql.createConnection({
        host: process.env.DB_HOST || '127.0.0.1',
        user: process.env.DB_USER || 'root',
        password: process.env.DB_PASSWORD || '',
        database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
        port: process.env.DB_PORT || 3306,
        multipleStatements: true
    });

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0;');

        // Get all tables
        const [tablesRes] = await connection.query('SHOW TABLES');
        const dbName = process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db';
        const tableNames = tablesRes.map(r => r[`Tables_in_${dbName}`]);

        for (const tableName of tableNames) {
            const [countRes] = await connection.query(`SELECT COUNT(*) as count FROM \`${tableName}\``);
            let currentCount = countRes[0].count;
            console.log(`Table \`${tableName}\` current count: ${currentCount}`);

            if (currentCount >= 20) {
                continue;
            }

            // Get columns description
            const [columnsRes] = await connection.query(`DESCRIBE \`${tableName}\``);

            // Filter out auto-increment columns if they aren't required, or keep them if needed
            const insertCols = [];
            const columnGenerators = [];

            for (const col of columnsRes) {
                const fieldName = col.Field;
                const type = col.Type.toLowerCase();
                const isAutoIncrement = col.Extra && col.Extra.includes('auto_increment');
                const isNullable = col.Null === 'yes';
                const hasDefault = col.Default !== null && col.Default !== undefined;

                if (isAutoIncrement) {
                    continue; // Skip auto_increment
                }

                insertCols.push(fieldName);

                // Determine generator based on type and field name
                if (type.includes('int') || type.includes('decimal') || type.includes('float') || type.includes('double')) {
                    if (fieldName.includes('id')) {
                        columnGenerators.push(() => 1);
                    } else {
                        columnGenerators.push(() => Math.floor(Math.random() * 100));
                    }
                } else if (type.includes('date') || type.includes('timestamp')) {
                    columnGenerators.push(() => faker.date.past().toISOString().slice(0, 19).replace('T', ' '));
                } else if (type.includes('text') || type.includes('longtext')) {
                    columnGenerators.push(() => faker.lorem.sentence());
                } else {
                    // varchar, char, etc.
                    if (fieldName.includes('email')) {
                        columnGenerators.push(() => faker.internet.email());
                    } else if (fieldName.includes('phone')) {
                        columnGenerators.push(() => faker.phone.number());
                    } else if (fieldName.includes('name') || fieldName.includes('title')) {
                        columnGenerators.push(() => faker.lorem.words(2) + '_' + faker.string.alphanumeric(4));
                    } else if (fieldName.includes('status')) {
                        columnGenerators.push(() => 'Active');
                    } else {
                        columnGenerators.push(() => faker.string.alphanumeric(6));
                    }
                }
            }

            if (insertCols.length === 0) {
                console.log(` -> Skipping \`${tableName}\` (no insertable columns without auto_increment).`);
                continue;
            }

            const needed = 20 - currentCount;
            console.log(` -> Inserting ${needed} records into \`${tableName}\`...`);

            const placeholders = insertCols.map(() => '?').join(', ');
            const sql = `INSERT INTO \`${tableName}\` (${insertCols.map(c => `\`${c}\``).join(', ')}) VALUES (${placeholders})`;

            let attempts = 0;
            while (currentCount < 20 && attempts < 30) {
                attempts++;
                const rowValues = columnGenerators.map(gen => gen());
                try {
                    await connection.query(sql, rowValues);
                } catch (err) {
                    // ignore individual insertion errors (e.g. unique constraint)
                }
                const [newCountRes] = await connection.query(`SELECT COUNT(*) as count FROM \`${tableName}\``);
                const newCount = newCountRes[0].count;
                if (newCount === currentCount) {
                    break;
                }
                currentCount = newCount;
            }
            console.log(` -> \`${tableName}\` final count: ${currentCount}`);
        }

        await connection.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('\n✅ Successfully ensured at least 20 items per table!');
    } catch (error) {
        console.error('❌ Dynamic seeding failed:', error.message);
    } finally {
        await connection.end();
    }
}

seedDynamic20();
