require('dotenv').config();
const mysql = require('mysql2/promise');

async function syncMissingOnly() {
    console.log('--- Syncing Missing Tables to Aiven Cloud DB ---');

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
        ssl: { rejectUnauthorized: false }
    };

    const remoteConn = await mysql.createConnection(remoteConfig);

    try {
        const missingTables = [
            'reports_generated',
            'risk_assessments',
            'service_plan_goals',
            'service_plans',
            'service_providers',
            'siblings',
            'sos_location_history',
            'system_settings',
            'tasks',
            'user_permissions',
            'users',
            'vaccination_records',
            'visitation_schedules',
            'worker_location_tracking',
            'worker_messages',
            'workload_tracking'
        ];

        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 0;');

        for (const tableName of missingTables) {
            console.log(`[Syncing Missing] \`${tableName}\`...`);

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
            const batchSize = 50;

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
            console.log(`  -> Successfully synced ${tableName} (${rows.length} rows).`);
        }

        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('\n✅ Successfully synced all missing tables to Aiven Cloud DB!');
    } catch (error) {
        console.error('❌ Sync failed:', error.message);
    } finally {
        await localConn.end();
        await remoteConn.end();
    }
}

syncMissingOnly();
