require('dotenv').config();
const mysql = require('mysql2/promise');

async function syncRemaining() {
    console.log('--- Sync Remaining Tables to Aiven Cloud DB ---');

    const localConn = await mysql.createConnection({
        host: '127.0.0.1',
        user: 'root',
        password: '',
        database: 'adoption_and_childcare_tracking_system_db',
        port: 3306
    });

    const remoteConfig = {
        host: process.env.AIVEN_DB_HOST,
        user: process.env.AIVEN_DB_USER,
        password: process.env.AIVEN_DB_PASSWORD,
        database: process.env.AIVEN_DB_NAME,
        port: process.env.AIVEN_DB_PORT,
        ssl: { rejectUnauthorized: false },
        connectTimeout: 30000
    };

    const remoteConn = await mysql.createConnection(remoteConfig);

    try {
        const [localTablesRes] = await localConn.query('SHOW TABLES');
        const dbName = 'adoption_and_childcare_tracking_system_db';
        const localTables = localTablesRes.map(r => r[`Tables_in_${dbName}`]);

        const [remoteTablesRes] = await remoteConn.query('SHOW TABLES');
        const remoteTables = new Set(remoteTablesRes.map(r => r[`Tables_in_${process.env.AIVEN_DB_NAME}`]));

        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 0;');

        for (const tableName of localTables) {
            if (remoteTables.has(tableName)) {
                try {
                    const [cntRes] = await remoteConn.query(`SELECT COUNT(*) as cnt FROM \`${tableName}\``);
                    if (cntRes[0].cnt >= 20) {
                        console.log(`[Skip] \`${tableName}\` already has ${cntRes[0].cnt} records.`);
                        continue;
                    }
                } catch (e) {}
            }

            console.log(`[Syncing] \`${tableName}\`...`);

            const [createTableRes] = await localConn.query(`SHOW CREATE TABLE \`${tableName}\``);
            let createSql = createTableRes[0]['Create Table'];
            createSql = createSql.replace('CREATE TABLE', 'CREATE TABLE IF NOT EXISTS');
            await remoteConn.query(createSql);

            const [rows] = await localConn.query(`SELECT * FROM \`${tableName}\``);

            try {
                await remoteConn.query(`TRUNCATE TABLE \`${tableName}\``);
            } catch (e) {
                await remoteConn.query(`DELETE FROM \`${tableName}\``);
            }

            if (rows.length === 0) continue;

            const columns = Object.keys(rows[0]);
            const batchSize = 50; // smaller batch size for stability

            for (let i = 0; i < rows.length; i += batchSize) {
                const batch = rows.slice(i, i + batchSize);
                const values = [];
                const placeholders = batch.map(row => {
                    columns.forEach(col => values.push(row[col]));
                    return `(${columns.map(() => '?').join(', ')})`;
                }).join(', ');

                const sql = `INSERT INTO \`${tableName}\` (${columns.map(c => `\`${c}\``).join(', ')}) VALUES ${placeholders}`;
                try {
                    await remoteConn.query(sql, values);
                } catch (err) {
                    for (const row of batch) {
                        const singleValues = columns.map(col => row[col]);
                        const singleSql = `INSERT INTO \`${tableName}\` (${columns.map(c => `\`${c}\``).join(', ')}) VALUES (${columns.map(() => '?').join(', ')})`;
                        try {
                            await remoteConn.query(singleSql, singleValues);
                        } catch (e) {}
                    }
                }
            }
            console.log(`  -> Done \`${tableName}\`.`);
        }

        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('\n✅ Successfully synced all tables to Aiven Cloud DB!');
    } catch (error) {
        console.error('❌ Sync failed:', error.message);
    } finally {
        await localConn.end();
        await remoteConn.end();
    }
}

syncRemaining();
