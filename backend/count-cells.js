require('dotenv').config();
const mysql = require('mysql2/promise');

async function countCells() {
    const connection = await mysql.createConnection({
        host: process.env.DB_HOST || '127.0.0.1',
        user: process.env.DB_USER || 'root',
        password: process.env.DB_PASSWORD || '',
        database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
        port: process.env.DB_PORT || 3306
    });

    try {
        const [tablesRes] = await connection.query('SHOW TABLES');
        const dbName = process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db';
        const tableNames = tablesRes.map(r => r[`Tables_in_${dbName}`]);

        let grandTotalCells = 0;
        console.log('--- Table Cell Count Audit ---');

        for (const tableName of tableNames) {
            const [columnsRes] = await connection.query(`DESCRIBE \`${tableName}\``);
            const columnCount = columnsRes.length;

            const [countRes] = await connection.query(`SELECT COUNT(*) as cnt FROM \`${tableName}\``);
            const rowCount = countRes[0].cnt;

            const totalCells = rowCount * columnCount;
            grandTotalCells += totalCells;

            console.log(`- ${tableName}: ${rowCount} rows × ${columnCount} columns = ${totalCells} cells`);
        }

        console.log(`\n========================================`);
        console.log(`TOTAL TABLES: ${tableNames.length}`);
        console.log(`GRAND TOTAL CELLS SEEDED ACROSS ALL TABLES: ${grandTotalCells}`);
        console.log(`========================================`);
    } catch (error) {
        console.error('Error counting cells:', error.message);
    } finally {
        await connection.end();
    }
}

countCells();
