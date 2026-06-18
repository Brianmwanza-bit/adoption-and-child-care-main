require('dotenv').config();
const mysql = require('mysql2');

const connection = mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
  port: process.env.DB_PORT || 3306
});

connection.connect((err) => {
  if (err) {
    console.error('Error connecting:', err.message);
    process.exit(1);
  }

  const tables = ['counties', 'users', 'documents'];

  let completed = 0;
  tables.forEach(table => {
    connection.query(`DESCRIBE \`${table}\``, (err, results) => {
      console.log(`\n--- Structure of ${table} ---`);
      if (err) {
        console.error(`Error describing ${table}:`, err.message);
      } else {
        console.table(results);
      }
      completed++;
      if (completed === tables.length) {
        connection.end();
      }
    });
  });
});
