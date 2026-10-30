require('dotenv').config();
const mysql = require('mysql2/promise');

async function purgeUsers() {
    console.log('--- Purging Users Table on Local XAMPP and Remote Aiven ---');

    // 1. Local XAMPP connection
    const localConn = await mysql.createConnection({
        host: '127.0.0.1',
        user: 'root',
        password: '',
        database: 'adoption_and_childcare_tracking_system_db',
        port: 3306
    });

    // 2. Remote Aiven connection
    const remoteConn = await mysql.createConnection({
        host: process.env.AIVEN_DB_HOST,
        user: process.env.AIVEN_DB_USER,
        password: process.env.AIVEN_DB_PASSWORD,
        database: process.env.AIVEN_DB_NAME,
        port: process.env.AIVEN_DB_PORT,
        ssl: { rejectUnauthorized: false }
    });

    try {
        // Purge Local XAMPP users
        await localConn.query('SET FOREIGN_KEY_CHECKS = 0;');
        await localConn.query('TRUNCATE TABLE users;');
        await localConn.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('✅ Successfully purged `users` table on Local XAMPP.');

        // Purge Remote Aiven users
        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 0;');
        await remoteConn.query('TRUNCATE TABLE users;');
        await remoteConn.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('✅ Successfully purged `users` table on Remote Aiven.');

    } catch (error) {
        console.error('❌ Purge failed:', error.message);
    } finally {
        await localConn.end();
        await remoteConn.end();
    }
}

purgeUsers();
