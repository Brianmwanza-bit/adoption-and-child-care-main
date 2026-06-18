require('dotenv').config();
const mysql = require('mysql2');

async function testConnection(config) {
    return new Promise((resolve) => {
        // mysql2 should handle auth switch automatically if configured,
        // but let's try a standard connection first.
        const connection = mysql.createConnection({
            ...config,
            // Try to force the client to support various plugins
            authPlugins: {
                // This is a common workaround for auth issues in node-mysql2
                mysql_clear_password: () => Buffer.from(config.password + '\0')
            }
        });

        console.log(`Testing ${config.host}:${config.port}...`);

        const timeout = setTimeout(() => {
            connection.destroy();
            resolve({ success: false, error: 'TIMEOUT' });
        }, 5000);

        connection.connect((err) => {
            clearTimeout(timeout);
            if (err) {
                connection.destroy();
                resolve({ success: false, error: err.code, message: err.message });
            } else {
                connection.end();
                resolve({ success: true });
            }
        });
    });
}

async function diagnose() {
    console.log('=== Database Diagnostic Tool (V3) ===');

    const ports = [3306];
    const hosts = ['127.0.0.1', 'localhost'];

    let workingConfig = null;

    for (const host of hosts) {
        for (const port of ports) {
            const res = await testConnection({
                host: host,
                user: process.env.DB_USER || 'root',
                password: process.env.DB_PASSWORD || '',
                port: port
            });

            if (res.success) {
                console.log(`✅ SUCCESS: Connected to ${host}:${port}`);
                workingConfig = { host, port };
            } else {
                console.log(`❌ FAILED: ${host}:${port} (${res.error}: ${res.message})`);
            }
        }
    }

    if (workingConfig) {
        console.log('\n--- Checking for database in ' + workingConfig.host + ':' + workingConfig.port + ' ---');
        const connection = mysql.createConnection({
            ...workingConfig,
            user: process.env.DB_USER || 'root',
            password: process.env.DB_PASSWORD || ''
        });

        connection.query('SHOW DATABASES', (err, results) => {
            if (err) {
                console.error('Error listing databases:', err.message);
            } else {
                console.log('Available Databases:');
                results.forEach(db => console.log(` - ${db.Database}`));

                const targetDb = process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db';
                const found = results.some(db => db.Database === targetDb);

                if (found) {
                    console.log(`\n✅ Database "${targetDb}" FOUND!`);
                    console.log('Updating .env suggested.');
                } else {
                    console.log(`\n⚠️  Database "${targetDb}" NOT FOUND in the list above.`);
                }
            }
            connection.end();
        });
    }
}

diagnose();
