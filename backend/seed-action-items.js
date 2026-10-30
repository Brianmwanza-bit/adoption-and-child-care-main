require('dotenv').config();
const mysql = require('mysql2');

const conn = mysql.createConnection({
  host: '127.0.0.1',
  port: 3306,
  user: 'root',
  password: '',
  database: 'adoption_and_childcare_tracking_system_db'
});

const priorities = ['low', 'medium', 'high', 'urgent'];
const statuses = ['pending', 'in_progress', 'completed', 'overdue'];

const actionItems = [];
for (let i = 1; i <= 20; i++) {
  const day = (i % 28 + 1).toString().padStart(2, '0');
  actionItems.push([
    null, // case_id
    null, // child_id
    `Action Item #${i}: Review Child Welfare Plan`,
    `Detailed action steps and compliance review for caseworker inspection #${i}.`,
    `2026-07-${day}`,
    priorities[i % priorities.length],
    statuses[i % statuses.length],
    null, // assigned_to
    null, // completed_at
    `Completion notes for item #${i}`
  ]);
}

const sql = 'INSERT INTO action_items (case_id, child_id, action_title, action_description, due_date, priority, status, assigned_to, completed_at, completion_notes) VALUES ?';

conn.query(sql, [actionItems], (err, res) => {
  if (err) {
    console.error('❌ Insert Error:', err.message);
  } else {
    console.log(`✅ SUCCESS: Seeded ${res.affectedRows} items into action_items table!`);
  }
  conn.end();
});
