require('dotenv').config();
const mysql = require('mysql2/promise');

async function readTables() {
  const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
    port: process.env.DB_PORT || 3306
  };

  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log(`--- LIVE DATABASE: ${dbConfig.database} ---\n`);

    const [tables] = await connection.query('SHOW TABLES');
    const tableKey = `Tables_in_${dbConfig.database}`;

    for (const tableRow of tables) {
      const tableName = tableRow[tableKey];
      const [rows] = await connection.query(`SELECT * FROM \`${tableName}\` LIMIT 5`);
      const [countResult] = await connection.query(`SELECT COUNT(*) as total FROM \`${tableName}\``);

      console.log(`TABLE: ${tableName} (${countResult[0].total} rows)`);
      if (rows.length > 0) {
        console.table(rows);
      } else {
        console.log(' (Empty table)');
      }
      console.log('\n' + '='.repeat(50) + '\n');
    }

    await connection.end();
  } catch (err) {
    console.error('Error reading database:', err.message);
  }
}

readTables();
