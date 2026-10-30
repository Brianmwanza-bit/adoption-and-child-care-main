require('dotenv').config();
const mysql = require('mysql2');
const fs = require('fs');
const path = require('path');

const caPath = path.join(__dirname, 'config-aiven-ca.pem');
const sslConfig = fs.existsSync(caPath)
  ? { minVersion: 'TLSv1.2', ca: fs.readFileSync(caPath, 'utf8'), rejectUnauthorized: false }
  : { minVersion: 'TLSv1.2', rejectUnauthorized: false };

const connection = mysql.createConnection({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'defaultdb',
    port: process.env.DB_PORT || 3306,
    ssl: sslConfig
});

connection.connect((err) => {
    if (err) {
        console.error('Connection failed:', err.message);
        process.exit(1);
    }

    const dbName = process.env.DB_NAME || 'defaultdb';
    connection.query('SHOW TABLES', (err, results) => {
        if (err) {
            console.error('Error fetching tables:', err.message);
        } else {
            console.log(`\n--- Tables in Aiven Database (${dbName}) ---`);
            let completed = 0;
            results.forEach((row, index) => {
                const tableName = row[`Tables_in_${dbName}`] || Object.values(row)[0];
                connection.query(`SELECT COUNT(*) as count FROM \`${tableName}\``, (err, countRes) => {
                    if (err) {
                        console.log(`[${index + 1}] ${tableName}: (error counting)`);
                    } else {
                        console.log(`[${index + 1}] ${tableName} — ${countRes[0].count} records`);
                    }
                    completed++;
                    if (completed === results.length) {
                        connection.end();
                        process.exit(0);
                    }
                });
            });
        }
    });
});
