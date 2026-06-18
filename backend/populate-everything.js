const mysql = require('mysql2/promise');
require('dotenv').config();

const dbConfig = {
    host: '127.0.0.1',
    user: 'root',
    password: '',
    database: 'adoption_and_childcare_tracking_system_db',
    multipleStatements: true
};

// Dummy Binary Data
const DUMMY_PHOTO = Buffer.from([
    0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01, 0x01, 0x01, 0x00, 0x60,
    0x00, 0x60, 0x00, 0x00, 0xFF, 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08,
    0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12,
    0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20,
    0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27,
    0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32, 0xFF, 0xD9
]); // Smallest possible valid JPEG

const DUMMY_PDF = Buffer.from('%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n3 0 obj<</Type/Page/MediaBox[0 0 612 792]/Parent 2 0 R/Resources<<>>>>endobj\nxref\n0 4\n0000000000 65535 f\n0000000009 00000 n\n0000000052 00000 n\n0000000101 00000 n\ntrailer<</Size 4/Root 1 0 R>>\nstartxref\n178\n%%EOF');

const idMap = {
    'child_id': 'children',
    'user_id': 'users',
    'worker_id': 'users',
    'case_worker_id': 'users',
    'family_id': 'family_profile',
    'placement_id': 'placements',
    'permission_id': 'permissions',
    'application_id': 'adoption_applications',
    'home_study_id': 'home_studies',
    'court_case_id': 'court_cases',
    'destination_family_id': 'families',
    'source_family_id': 'families',
    'conducted_by': 'users',
    'performed_by': 'users',
    'assigned_to': 'users',
    'uploaded_by': 'users',
    'granted_by': 'users',
    'event_id': 'emergency_events',
    'task_id': 'tasks'
};

const getDummyValue = (col, index, table, existingIds) => {
    const name = col.Field.toLowerCase();
    const type = col.Type.toLowerCase();

    // Binary data
    if (type.includes('blob')) {
        if (name.includes('photo')) return DUMMY_PHOTO;
        return DUMMY_PDF;
    }

    // ID Links
    if (idMap[name] && existingIds[idMap[name]]) {
        const ids = existingIds[idMap[name]];
        return ids[index % ids.length];
    }

    // Special Fields
    if (name.includes('username')) return `staff_user_${index}_${Math.floor(Math.random()*1000)}`;
    if (name.includes('email')) return `${table}_${index}_${Math.floor(Math.random()*1000)}@adoption.org`;
    if (name.includes('phone')) return `+2547${Math.floor(10000000 + Math.random() * 90000000)}`;
    if (name.includes('password')) return '$2a$10$8K1p/a0daM/98G88R86R8.h8R8R8R8R8R8R8R8R8R8R8R8R8R8R8R8';
    if (name.includes('status')) return 'Active';
    if (name.includes('date') || name.includes('_at')) return new Date().toISOString().slice(0, 10);
    if (name.includes('amount') || name.includes('cost') || name.includes('fee')) return (1000 + index * 50).toFixed(2);
    if (name.includes('url')) return `https://api.adoption.org/assets/${table}_${index}.png`;
    if (name.includes('mime_type')) return name.includes('photo') ? 'image/jpeg' : 'application/pdf';
    if (name.includes('latitude')) return -1.2921 + (Math.random() * 0.1);
    if (name.includes('longitude')) return 36.8219 + (Math.random() * 0.1);
    if (name.includes('id_no') || name.includes('national_id')) return `ID-${index}-${Math.floor(100000 + Math.random()*900000)}`;
    if (name.includes('case_number') || name.includes('application_number')) return `ACC-${table.toUpperCase()}-${2024000 + index}`;

    // Defaults by Type
    if (type.includes('int')) return index + 1;
    if (type.includes('varchar') || type.includes('text')) return `Sample ${name.replace(/_/g, ' ')} for record ${index}`;
    if (type.includes('tinyint')) return 1;

    return null;
};

async function populateEverything() {
    const connection = await mysql.createConnection(dbConfig);
    console.log('=== Rich Data Populator (Photos & PDFs) ===');

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0');
        const [tables] = await connection.query('SHOW TABLES');
        const tableNames = tables.map(t => Object.values(t)[0]);

        const existingIds = {};
        const refreshIds = async (tableName) => {
            const pkResult = await connection.query(`SHOW KEYS FROM ${tableName} WHERE Key_name = 'PRIMARY'`);
            if (pkResult[0].length > 0) {
                const pk = pkResult[0][0].Column_name;
                const [rows] = await connection.query(`SELECT ${pk} FROM ${tableName} LIMIT 100`);
                existingIds[tableName] = rows.map(r => r[pk]);
            }
        };

        // Priority order for foreign keys
        const coreOrder = ['users', 'children', 'families', 'family_profile', 'permissions', 'tasks', 'emergency_events'];
        const remaining = tableNames.filter(t => !coreOrder.includes(t));
        const finalOrder = [...coreOrder, ...remaining];

        for (const tableName of finalOrder) {
            console.log(`Checking table: ${tableName}`);
            const [count] = await connection.query(`SELECT count(*) as count FROM ${tableName}`);
            const [cols] = await connection.query(`DESCRIBE ${tableName}`);
            const insertCols = cols.filter(c => !c.Extra.includes('auto_increment'));

            // Even if it has 10, we'll make sure it has at least 10
            const needed = 10 - count[0].count;
            if (needed > 0) {
                console.log(`   Populating ${needed} records into ${tableName}...`);
                for (let i = 1; i <= needed; i++) {
                    const names = [];
                    const values = [];
                    const placeholders = [];

                    for (const col of insertCols) {
                        const val = getDummyValue(col, count[0].count + i, tableName, existingIds);
                        if (val !== null || col.Null === 'NO') {
                            names.push(`\`${col.Field}\``);
                            values.push(val);
                            placeholders.push('?');
                        }
                    }

                    try {
                        await connection.query(`INSERT INTO ${tableName} (${names.join(', ')}) VALUES (${placeholders.join(', ')})`, values);
                    } catch (err) {
                        if (!err.message.includes('Duplicate entry')) {
                            console.log(`      Error in ${tableName} row ${i}: ${err.message}`);
                        }
                    }
                }
            } else {
                console.log(`   Table already has ${count[0].count} records.`);
            }
            await refreshIds(tableName);
        }

        console.log('\n=== Success: All tables seeded with data, photos, and PDFs. ===');
    } catch (err) {
        console.error('CRITICAL ERROR:', err);
    } finally {
        await connection.query('SET FOREIGN_KEY_CHECKS = 1');
        await connection.end();
    }
}

populateEverything();
