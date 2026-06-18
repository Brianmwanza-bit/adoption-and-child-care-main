const mysql = require('mysql2/promise');
require('dotenv').config();

const dbConfig = {
    host: '127.0.0.1',
    user: 'root',
    password: '',
    database: 'adoption_and_childcare_tracking_system_db',
    multipleStatements: true
};

async function populate() {
    const connection = await mysql.createConnection(dbConfig);
    console.log('--- Database Populator ---');

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0');

        // 1. Ensure 10 Users
        const [users] = await connection.query('SELECT count(*) as count FROM users');
        if (users[0].count < 10) {
            console.log('Populating users...');
            for (let i = users[0].count + 1; i <= 10; i++) {
                await connection.query(`INSERT IGNORE INTO users (username, password_hash, role, email, phone, national_id_no)
                    VALUES (?, ?, ?, ?, ?, ?)`,
                    [`user${i}`, '$2a$10$8K1p/a0daM/98G88R86R8.h8R8R8R8R8R8R8R8R8R8R8R8R8R8R8R8', 'Social Worker', `user${i}@example.com`, `071234567${i}`, `ID${1000 + i}`]);
            }
        }

        const [allUsers] = await connection.query('SELECT user_id FROM users LIMIT 10');
        const userIds = allUsers.map(u => u.user_id);

        // 2. Ensure 10 Children
        const [children] = await connection.query('SELECT count(*) as count FROM children');
        if (children[0].count < 10) {
            console.log('Populating children...');
            for (let i = children[0].count + 1; i <= 10; i++) {
                await connection.query(`INSERT IGNORE INTO children (case_number, first_name, last_name, date_of_birth, gender, status, case_worker_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)`,
                    [`CASE-00${i}`, `ChildFirstName${i}`, `LastName${i}`, '2015-01-01', i % 2 === 0 ? 'Male' : 'Female', 'Active', userIds[i % userIds.length]]);
            }
        }
        const [allChildren] = await connection.query('SELECT child_id FROM children LIMIT 10');
        const childIds = allChildren.map(c => c.child_id);

        // 3. Ensure 10 Family Profiles
        const [families] = await connection.query('SELECT count(*) as count FROM family_profile');
        if (families[0].count < 10) {
            console.log('Populating families...');
            for (let i = families[0].count + 1; i <= 10; i++) {
                await connection.query(`INSERT IGNORE INTO family_profile (family_registration_no, user_id, primary_contact_name, phone, status)
                    VALUES (?, ?, ?, ?, ?)`,
                    [`REG-00${i}`, userIds[i % userIds.length], `ParentName${i}`, `072233445${i}`, 'Active']);
            }
        }
        const [allFamilies] = await connection.query('SELECT family_id FROM family_profile LIMIT 10');
        const familyIds = allFamilies.map(f => f.family_id);

        // 4. Populate other dependent tables
        const dependentTables = [
            { name: 'guardians', cols: '(child_id, first_name, last_name, relationship, phone)', vals: (i) => [childIds[i % childIds.length], 'Guardian', 'Name'+i, 'Aunt', '070011223'+i] },
            { name: 'placements', cols: '(child_id, destination_family_id, start_date, status)', vals: (i) => [childIds[i % childIds.length], familyIds[i % familyIds.length], '2023-01-01', 'Active'] },
            { name: 'medical_records', cols: '(child_id, visit_date, diagnosis, treatment)', vals: (i) => [childIds[i % childIds.length], '2023-05-10', 'Checkup', 'General'] },
            { name: 'education_records', cols: '(child_id, school_name, grade)', vals: (i) => [childIds[i % childIds.length], 'Primary School '+i, 'Grade '+i] },
            { name: 'court_cases', cols: '(child_id, case_number, court_name, case_type, filing_date)', vals: (i) => [childIds[i % childIds.length], 'CRT-'+i, 'High Court', 'Adoption', '2023-02-01'] },
            { name: 'adoption_applications', cols: '(child_id, family_id, application_date, status)', vals: (i) => [childIds[i % childIds.length], familyIds[i % familyIds.length], '2023-03-01', 'Pending'] },
            { name: 'foster_matches', cols: '(child_id, family_id, match_date, status)', vals: (i) => [childIds[i % childIds.length], familyIds[i % familyIds.length], '2023-04-01', 'Pending'] },
            { name: 'notifications', cols: '(user_id, notification_type, title, message)', vals: (i) => [userIds[i % userIds.length], 'System', 'Welcome', 'Welcome to the system'] }
        ];

        for (const table of dependentTables) {
            const [count] = await connection.query(`SELECT count(*) as count FROM ${table.name}`);
            if (count[0].count < 10) {
                console.log(`Populating ${table.name}...`);
                for (let i = count[0].count + 1; i <= 10; i++) {
                    const placeholders = table.vals(i).map(() => '?').join(', ');
                    await connection.query(`INSERT INTO ${table.name} ${table.cols} VALUES (${placeholders})`, table.vals(i));
                }
            }
        }

        console.log('✅ Success: All core tables now have at least 10 records.');
    } catch (err) {
        console.error('❌ Error during population:', err.message);
    } finally {
        await connection.query('SET FOREIGN_KEY_CHECKS = 1');
        await connection.end();
    }
}

populate();
