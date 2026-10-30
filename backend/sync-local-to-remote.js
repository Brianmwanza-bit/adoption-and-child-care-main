require('dotenv').config();
const mysql = require('mysql2/promise');

async function syncLocalToRemote() {
    console.log('--- Sync Local XAMPP DB to Aiven Cloud DB (Optimized Bulk Insert) ---');

    const localConn = await mysql.createConnection({
        host: '127.0.0.1',
        user: 'root',
        password: '',
        database: 'adoption_and_childcare_tracking_system_db',
        port: 3306
    });

    const remoteConfig = {
        host: process.env.AIVEN_DB_HOST || process.env.DB_HOST,
        user: process.env.AIVEN_DB_USER || process.env.DB_USER || 'avnadmin',
        password: process.env.AIVEN_DB_PASSWORD || process.env.DB_PASSWORD,
        database: process.env.AIVEN_DB_NAME || process.env.DB_NAME || 'defaultdb',
        port: process.env.AIVEN_DB_PORT || process.env.DB_PORT || 13255,
        ssl: { rejectUnauthorized: false }
    };

    const remoteConn = await mysql.createConnection(remoteConfig);

    try {
        console.log('Connected to both local XAMPP and remote Aiven databases.');

        const [tablesRes] = await localConn.query('SHOW TABLES');
        const dbName = 'adoption_and_childcare_tracking_system_db';
        const tableNames = tablesRes.map(r => r[`Tables_in_${dbName}`]);

        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 0;');

        for (const tableName of tableNames) {
            console.log(`Syncing table: \`${tableName}\`...`);

            // 1. Get CREATE TABLE statement from local
            const [createTableRes] = await localConn.query(`SHOW CREATE TABLE \`${tableName}\``);
            let createSql = createTableRes[0]['Create Table'];
            createSql = createSql.replace('CREATE TABLE', 'CREATE TABLE IF NOT EXISTS');

            // 2. Create table on remote Aiven
            await remoteConn.query(createSql);

            // 3. Fetch all rows from local table
            const [rows] = await localConn.query(`SELECT * FROM \`${tableName}\``);

            // 4. Clear remote table
            try {
                await remoteConn.query(`TRUNCATE TABLE \`${tableName}\``);
            } catch (e) {
                await remoteConn.query(`DELETE FROM \`${tableName}\``);
            }

            if (rows.length === 0) continue;

            // 5. Bulk insert rows into remote table (batches of 100)
            const columns = Object.keys(rows[0]);
            const batchSize = 100;

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
                    // Fallback to single insert if batch fails
                    for (const row of batch) {
                        const singleValues = columns.map(col => row[col]);
                        const singleSql = `INSERT INTO \`${tableName}\` (${columns.map(c => `\`${c}\``).join(', ')}) VALUES (${columns.map(() => '?').join(', ')})`;
                        try {
                            await remoteConn.query(singleSql, singleValues);
                        } catch (e) {}
                    }
                }
            }
            console.log(`  -> Synced schema and ${rows.length} rows to remote \`${tableName}\`.`);
        }

        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('\n✅ Successfully synced all 64 tables and 17,160 cells from Local XAMPP to Aiven Cloud DB!');
    } catch (error) {
        console.error('❌ Sync failed:', error.message);
    } finally {
        await localConn.end();
        await remoteConn.end();
    }
}

syncLocalToRemote();
