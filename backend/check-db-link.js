require('dotenv').config();
const mysql = require('mysql2');

console.log('--- Database Link Checker ---');
console.log('Host:', process.env.DB_HOST);
console.log('User:', process.env.DB_USER);
console.log('Database:', process.env.DB_NAME);
console.log('Port:', process.env.DB_PORT);

const connection = mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
  port: process.env.DB_PORT || 3306
});

connection.connect((err) => {
  if (err) {
    console.error('\n❌ FAILED to connect to MySQL:', err.message);
    console.log('\nTroubleshooting:');
    console.log('1. Is XAMPP/MySQL running?');
    console.log('2. Check if the database name is exactly: ' + (process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db'));
    console.log('3. Ensure credentials in backend/.env match phpMyAdmin.');
    process.exit(1);
  }

  console.log('\n✅ SUCCESS: Connected to MySQL!');

  connection.query('SELECT COUNT(*) as count FROM users', (err, results) => {
    if (err) {
      console.error('❌ Error querying users table:', err.message);
      console.log('Did you import the .sql schema yet?');
    } else {
      console.log('✅ SUCCESS: Found ' + results[0].count + ' users in database.');
    }

    connection.end();
    process.exit(0);
  });
});
