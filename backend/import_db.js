const fs = require('fs');
const mysql = require('mysql2');
require('dotenv').config();

const dbConfig = {
    host: '127.0.0.1',
    user: 'root',
    password: '',
    database: 'adoption_and_childcare_tracking_system_db',
    multipleStatements: true
};

const sqlPath = "C:/Users/Lydia mwanza/OneDrive/Desktop/school PROJECT FILE/adoption-and-child-care-main/databases/adoption_and_childcare_tracking_system_db_modified.sql";

async function importSql() {
    console.log('Reading SQL file...');
    let sql = fs.readFileSync(sqlPath, 'utf8');

    // Disable foreign key checks for the import
    sql = "SET FOREIGN_KEY_CHECKS = 0;\n" + sql + "\nSET FOREIGN_KEY_CHECKS = 1;";

    const connection = mysql.createConnection(dbConfig);

    console.log('Connecting to database...');
    connection.connect(err => {
        if (err) {
            console.error('Connection failed:', err.message);
            process.exit(1);
        }

        console.log('Running import script (this may take a few seconds)...');
        connection.query(sql, (err, results) => {
            if (err) {
                console.error('Import Error:', err.message);
                // Try to find where it failed
                if (err.offset) {
                    console.log('Error at offset:', err.offset);
                }
            } else {
                console.log('✅ Import Successful!');
            }
            connection.end();
        });
    });
}

importSql();
