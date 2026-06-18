require('dotenv').config();
const mysql = require('mysql2/promise');
const { faker } = require('@faker-js/faker');
const bcrypt = require('bcryptjs');

async function seed() {
    console.log('--- FINAL Comprehensive Database Seeder Started ---');

    const connection = await mysql.createConnection({
        host: process.env.DB_HOST || '127.0.0.1',
        user: process.env.DB_USER || 'root',
        password: process.env.DB_PASSWORD || '',
        database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
        port: process.env.DB_PORT || 3306,
        multipleStatements: true
    });

    try {
        const passwordHash = await bcrypt.hash('password123', 10);
        const roles = ['Social Worker', 'Admin', 'Case Manager', 'Field Officer', 'Supervisor'];
        const counties = ['Nairobi', 'Mombasa', 'Kisumu', 'Nakuru', 'Eldoret', 'Kiambu', 'Machakos', 'Nyeri', 'Meru'];
        const genders = ['Male', 'Female'];
        const statuses = ['Active', 'Pending', 'Closed', 'Archived'];
        const placementTypes = ['Foster Home', 'Group Home', 'Kinship Care', 'Institution', 'Adoption'];

        // 1. Users
        console.log('Seeding Users...');
        const users = [];
        for (let i = 0; i < 25; i++) {
            const firstName = faker.person.firstName();
            const lastName = faker.person.lastName();
            users.push([
                faker.internet.username({ firstName, lastName }), passwordHash,
                faker.helpers.arrayElement(roles), faker.internet.email({ firstName, lastName }),
                faker.phone.number(), faker.string.numeric(8),
                faker.helpers.arrayElement(counties), faker.location.city(), 1
            ]);
        }
        await connection.query('INSERT IGNORE INTO users (username, password_hash, role, email, phone, national_id_no, county, sub_county, is_active) VALUES ?', [users]);
        const [userRows] = await connection.query('SELECT user_id FROM users');
        const userIds = userRows.map(r => r.user_id);

        // 2. Families
        console.log('Seeding Families...');
        const families = [];
        for (let i = 0; i < 40; i++) {
            families.push([
                faker.person.fullName(), faker.person.fullName(), faker.internet.email(),
                faker.phone.number(), faker.location.streetAddress(), faker.helpers.arrayElement(counties),
                faker.location.city(), faker.location.latitude(), faker.location.longitude(),
                faker.string.numeric(8), `FL-${faker.string.numeric(4)}-${faker.string.numeric(4)}`,
                faker.date.past({ years: 2 }).toISOString().split('T')[0],
                faker.date.future({ years: 1 }).toISOString().split('T')[0],
                faker.helpers.arrayElement(['Licensed', 'Pending Renewal', 'Active']), faker.location.county()
            ]);
        }
        await connection.query('INSERT IGNORE INTO families (primary_contact_name, secondary_contact_name, email, phone, address, county, city, latitude, longitude, national_id_no, license_number, license_issue_date, license_expiration_date, license_status, sub_county) VALUES ?', [families]);
        const [familyRows] = await connection.query('SELECT family_id FROM families');
        const familyIds = familyRows.map(r => r.family_id);

        // 3. Children
        console.log('Seeding Children...');
        const children = [];
        for (let i = 0; i < 60; i++) {
            const isEmancipated = faker.datatype.boolean(0.05) ? 1 : 0;
            children.push([
                `CASE-${faker.string.alphanumeric(6).toUpperCase()}`, faker.person.firstName(),
                faker.person.middleName(), faker.person.lastName(),
                faker.date.birthdate({ min: 1, max: 17, mode: 'age' }).toISOString().split('T')[0],
                faker.helpers.arrayElement(genders), faker.helpers.arrayElement(['Active', 'Placed', 'Pending Match']),
                faker.helpers.arrayElement(counties), isEmancipated, faker.location.city(),
                isEmancipated ? null : faker.lorem.sentence(), faker.helpers.arrayElement(['Peanuts', 'None', 'Dairy', 'Dust']),
                faker.helpers.arrayElement(['A+', 'B+', 'O+', 'O-']), 'Dr. ' + faker.person.lastName(),
                faker.helpers.arrayElement(['None', 'IEP Required', 'Speech Therapy']), faker.string.numeric(10),
                isEmancipated ? faker.date.recent().toISOString().split('T')[0] : null,
                isEmancipated ? 'Legal Age reached' : null
            ]);
        }
        await connection.query('INSERT IGNORE INTO children (case_number, first_name, middle_name, last_name, date_of_birth, gender, status, current_county, is_emancipated, place_of_birth, trauma_notes, allergies, blood_type, primary_physician, special_needs, birth_certificate_no, emancipation_date, emancipation_reason) VALUES ?', [children]);
        const [childRows] = await connection.query('SELECT child_id FROM children');
        const childIds = childRows.map(r => r.child_id);

        // 4. Placements
        console.log('Seeding Placements...');
        const placements = [];
        for (let i = 0; i < 45; i++) {
            placements.push([
                faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(familyIds),
                faker.helpers.arrayElement(placementTypes), faker.date.past({ years: 1 }).toISOString().split('T')[0],
                'Active', faker.helpers.arrayElement(userIds), faker.company.name(), faker.lorem.sentence()
            ]);
        }
        await connection.query('INSERT IGNORE INTO placements (child_id, destination_family_id, placement_type, start_date, status, case_worker_id, organization, notes) VALUES ?', [placements]);

        // 5. Medical
        console.log('Seeding Medical...');
        const medical = [];
        for (let i = 0; i < 100; i++) {
            medical.push([
                faker.helpers.arrayElement(childIds), faker.date.past({ years: 1 }).toISOString().split('T')[0],
                faker.company.name() + ' Clinic', faker.lorem.word() + ' Examination',
                faker.lorem.sentence(), 'Dr. ' + faker.person.lastName(),
                faker.lorem.words(3), faker.helpers.arrayElement([0, 1]), faker.helpers.arrayElement(['BCG', 'Polio', 'Measles', 'None'])
            ]);
        }
        await connection.query('INSERT IGNORE INTO medical_records (child_id, visit_date, hospital_name, diagnosis, treatment, doctor_name, medications, is_immunization, immunization_type) VALUES ?', [medical]);

        // 6. Education
        console.log('Seeding Education...');
        const education = [];
        for (let i = 0; i < 80; i++) {
            education.push([
                faker.helpers.arrayElement(childIds), faker.company.name() + ' Academy',
                faker.helpers.arrayElement(['Grade 1', 'Grade 4', 'Form 2', 'Class 8']),
                faker.date.past({ years: 2 }).toISOString().split('T')[0],
                faker.helpers.arrayElement(['Excellent', 'Improving', 'Average', 'Needs Support'])
            ]);
        }
        await connection.query('INSERT IGNORE INTO education_records (child_id, school_name, grade, enrollment_date, performance) VALUES ?', [education]);

        // 7. Case Reports
        console.log('Seeding Case Reports...');
        const reports = [];
        for (let i = 0; i < 90; i++) {
            reports.push([
                faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(userIds),
                faker.date.past().toISOString().split('T')[0], faker.lorem.words(3).toUpperCase() + ' Report',
                faker.helpers.arrayElement(['Monthly Visit', 'Incident', 'Review']),
                faker.lorem.sentence(), faker.lorem.paragraph(), faker.datatype.boolean(0.1) ? 1 : 0
            ]);
        }
        await connection.query('INSERT IGNORE INTO case_reports (child_id, worker_id, report_date, report_title, report_type, findings, recommendations, is_confidential) VALUES ?', [reports]);

        // 8. Guardians
        console.log('Seeding Guardians...');
        const guardians = [];
        for (let i = 0; i < 50; i++) {
            guardians.push([
                faker.helpers.arrayElement(childIds), faker.person.firstName(), faker.person.lastName(),
                faker.helpers.arrayElement(['Aunt', 'Uncle', 'Grandparent']), faker.phone.number(),
                faker.internet.email(), faker.location.streetAddress(), faker.datatype.boolean(0.3) ? 1 : 0, 'Verified'
            ]);
        }
        await connection.query('INSERT IGNORE INTO guardians (child_id, first_name, last_name, relationship, phone, email, address, is_primary, verification_status) VALUES ?', [guardians]);

        // 9. BG Checks
        console.log('Seeding BG Checks...');
        const bgChecks = [];
        for (let i = 0; i < 40; i++) {
            bgChecks.push([
                faker.helpers.arrayElement(userIds), faker.helpers.arrayElement(['Completed', 'Processing', 'Pending']),
                'No criminal record found.', faker.date.past().toISOString().slice(0, 19).replace('T', ' '),
                faker.date.recent().toISOString().slice(0, 19).replace('T', ' ')
            ]);
        }
        await connection.query('INSERT IGNORE INTO background_checks (user_id, status, result, requested_at, completed_at) VALUES ?', [bgChecks]);

        // 10. Siblings
        console.log('Seeding Siblings...');
        const siblingLinks = [];
        const used = new Set();
        for (let i = 0; i < 30; i++) {
            const c1 = faker.helpers.arrayElement(childIds);
            const c2 = faker.helpers.arrayElement(childIds);
            const key = c1 < c2 ? `${c1}-${c2}` : `${c2}-${c1}`;
            if (c1 !== c2 && !used.has(key)) {
                siblingLinks.push([c1, c2, faker.helpers.arrayElement(['Biological', 'Step']), 1, 1, 'Verified via registration']);
                used.add(key);
            }
        }
        if (siblingLinks.length > 0) {
            await connection.query('INSERT IGNORE INTO siblings (child_id, sibling_child_id, relationship_type, same_placement, contact_allowed, notes) VALUES ?', [siblingLinks]);
        }

        console.log('\n✅ FINAL Database Seeding Successful!');

    } catch (error) {
        console.error('\n❌ Seeding Failed:', error.message);
    } finally {
        await connection.end();
        process.exit();
    }
}

seed();
