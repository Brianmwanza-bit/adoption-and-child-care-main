require('dotenv').config();
const mysql = require('mysql2');

const connection = mysql.createConnection({
    host: '127.0.0.1',
    user: 'root',
    password: '',
    database: 'adoption_and_childcare_tracking_system_db'
});

connection.connect((err) => {
    if (err) {
        console.error('Connection failed:', err.message);
        process.exit(1);
    }

    connection.query('SHOW TABLES', (err, results) => {
        if (err) {
            console.error('Error fetching tables:', err.message);
        } else {
            console.log('Tables found:');
            results.forEach(row => {
                const tableName = row[`Tables_in_adoption_and_childcare_tracking_system_db`];
                connection.query(`SELECT COUNT(*) as count FROM ${tableName}`, (err, countRes) => {
                    if (err) {
                        console.log(` - ${tableName}: Error reading table`);
                    } else {
                        console.log(` - ${tableName}: ${countRes[0].count} records`);
                    }
                });
            });
        }

        // End connection after a delay to allow count queries to finish
        setTimeout(() => {
            connection.end();
        }, 2000);
    });
});
