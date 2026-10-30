require('dotenv').config();
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');

async function seedDefaultUsers() {
    console.log('--- Seeding Default Auth Users into Users Table ---');

    const passwordHash = await bcrypt.hash('password123', 10);

    const defaultUsers = [
        ['admin', passwordHash, 'Admin', 'admin@adoption-care.org', '0710000000', '12345678', 'Nairobi', 1],
        ['worker1', passwordHash, 'Social Worker', 'worker1@adoption-care.org', '0710000001', '23456789', 'Mombasa', 1],
        ['worker2', passwordHash, 'Case Manager', 'worker2@adoption-care.org', '0710000002', '34567890', 'Kisumu', 1],
        ['supervisor', passwordHash, 'Supervisor', 'supervisor@adoption-care.org', '0710000003', '45678901', 'Nakuru', 1]
    ];

    const sql = 'INSERT INTO users (username, password_hash, role, email, phone, national_id_no, county, is_active) VALUES ?';

    // 1. Local XAMPP
    const localConn = await mysql.createConnection({
        host: '127.0.0.1',
        user: 'root',
        password: '',
        database: 'adoption_and_childcare_tracking_system_db',
        port: 3306
    });

    await localConn.query(sql, [defaultUsers]);
    console.log('✅ Seeded default users into Local XAMPP `users` table.');
    await localConn.end();

    // 2. Remote Aiven
    const remoteConn = await mysql.createConnection({
        host: process.env.AIVEN_DB_HOST,
        user: process.env.AIVEN_DB_USER,
        password: process.env.AIVEN_DB_PASSWORD,
        database: process.env.AIVEN_DB_NAME,
        port: process.env.AIVEN_DB_PORT,
        ssl: { rejectUnauthorized: false }
    });

    await remoteConn.query(sql, [defaultUsers]);
    console.log('✅ Seeded default users into Remote Aiven `users` table.');
    await remoteConn.end();

    console.log('\n✅ Auth system is now fully linked and populated with login credentials!');
    console.log('Login credentials:');
    console.log(' - Username / Email: admin / admin@adoption-care.org (Password: password123)');
    console.log(' - Username / Email: worker1 / worker1@adoption-care.org (Password: password123)');
}

seedDefaultUsers();
