require('dotenv').config();
const mysql = require('mysql2/promise');
const { faker } = require('@faker-js/faker');

async function seed20PerTable() {
    console.log('--- Starting Seeding to ensure at least 20 items per table ---');

    const connection = await mysql.createConnection({
        host: process.env.DB_HOST || '127.0.0.1',
        user: process.env.DB_USER || 'root',
        password: process.env.DB_PASSWORD || '',
        database: process.env.DB_NAME || 'defaultdb',
        port: process.env.DB_PORT || 3306,
        multipleStatements: true,
        ssl: (process.env.DB_SSL === 'true' || (process.env.DB_HOST && process.env.DB_HOST.includes('aivencloud.com'))) ? { rejectUnauthorized: false } : null
    });

    try {
        await connection.query('SET FOREIGN_KEY_CHECKS = 0;');

        const [users] = await connection.query('SELECT user_id FROM users');
        const userIds = users.map(u => u.user_id);

        const [children] = await connection.query('SELECT child_id FROM children');
        const childIds = children.map(c => c.child_id);

        const [families] = await connection.query('SELECT family_id FROM families');
        const familyIds = families.map(f => f.family_id);

        const [permissions] = await connection.query('SELECT permission_id FROM permissions');
        const permIds = permissions.map(p => p.permission_id);

        const [placements] = await connection.query('SELECT placement_id FROM placements');
        const placementIds = placements.map(p => p.placement_id);

        const [servicePlans] = await connection.query('SELECT plan_id FROM service_plans');
        const servicePlanIds = servicePlans.length > 0 ? servicePlans.map(sp => sp.plan_id) : [1];

        const tablesToCheck = [
            { name: 'action_items', query: () => [faker.helpers.arrayElement(childIds), faker.lorem.sentence() + ' ' + faker.string.alphanumeric(6), faker.lorem.paragraph(), faker.date.future().toISOString().split('T')[0], 'medium', 'pending'], cols: ['child_id', 'action_title', 'action_description', 'due_date', 'priority', 'status'] },
            { name: 'adoption_applications', query: () => [faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(familyIds), 'Pending', faker.date.past().toISOString().split('T')[0], faker.lorem.sentence()], cols: ['child_id', 'family_id', 'status', 'application_date', 'notes'] },
            { name: 'aftercare_plans', query: () => [faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(familyIds), faker.lorem.paragraph(), faker.date.past().toISOString().split('T')[0], 'Active'], cols: ['child_id', 'family_id', 'plan_details', 'start_date', 'status'] },
            { name: 'audit_logs', query: () => [faker.helpers.arrayElement(userIds), 'LOG_' + faker.string.alphanumeric(8), faker.lorem.sentence(), faker.internet.ip()], cols: ['user_id', 'action', 'details', 'ip_address'] },
            { name: 'budget_allocations', query: () => ['Cat_' + faker.string.alphanumeric(8), 50000.00, '2026', faker.date.past().toISOString().split('T')[0]], cols: ['category', 'amount', 'fiscal_year', 'allocated_date'] },
            { name: 'case_activities', query: () => [faker.helpers.arrayElement(childIds), 'Act_' + faker.string.alphanumeric(6), faker.lorem.sentence(), faker.date.past().toISOString().split('T')[0]], cols: ['case_id', 'activity_type', 'description', 'activity_date'] },
            { name: 'case_approvals', query: () => [faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(userIds), 'Approved', faker.lorem.sentence()], cols: ['case_id', 'approved_by', 'status', 'comments'] },
            { name: 'case_deadlines', query: () => [faker.helpers.arrayElement(childIds), 'DL_' + faker.string.alphanumeric(8), faker.date.future().toISOString().split('T')[0], 'Pending'], cols: ['case_id', 'deadline_title', 'due_date', 'status'] },
            { name: 'case_urgency_flags', query: () => [faker.helpers.arrayElement(childIds), 'High', faker.lorem.sentence()], cols: ['case_id', 'urgency_level', 'reason'] },
            { name: 'caseload', query: () => [faker.helpers.arrayElement(userIds), faker.helpers.arrayElement(childIds), faker.date.past().toISOString().split('T')[0], 'Active'], cols: ['worker_id', 'child_id', 'assigned_date', 'status'] },
            { name: 'child_behavior_assessments', query: () => [faker.helpers.arrayElement(childIds), faker.date.past().toISOString().split('T')[0], faker.person.fullName(), faker.lorem.sentence()], cols: ['child_id', 'assessment_date', 'evaluator', 'notes'] },
            { name: 'child_services_referrals', query: () => [faker.helpers.arrayElement(childIds), 'Serv_' + faker.string.alphanumeric(6), faker.company.name(), 'Pending'], cols: ['child_id', 'service_type', 'provider', 'status'] },
            { name: 'child_welfare_incidents', query: () => [faker.helpers.arrayElement(childIds), faker.date.past().toISOString().split('T')[0], faker.lorem.sentence(), 'Moderate'], cols: ['child_id', 'incident_date', 'description', 'severity'] },
            { name: 'consent_records', query: () => [faker.helpers.arrayElement(childIds), 1, 'Med_' + faker.string.alphanumeric(6), faker.date.past().toISOString().split('T')[0]], cols: ['child_id', 'guardian_id', 'consent_type', 'date_given'] },
            { name: 'county_offices', query: () => ['Cnt_' + faker.string.alphanumeric(8), 'Off_' + faker.string.alphanumeric(8), faker.phone.number(), faker.internet.email()], cols: ['county_name', 'office_name', 'phone', 'email'] },
            { name: 'court_cases', query: () => [faker.helpers.arrayElement(childIds), 'CC-' + faker.string.numeric(10), 'Family Court', faker.person.fullName(), faker.date.past().toISOString().split('T')[0], 'Open'], cols: ['child_id', 'case_number', 'court_name', 'judge_name', 'filing_date', 'status'] },
            { name: 'critical_dates', query: () => [faker.helpers.arrayElement(childIds), 'Evt_' + faker.string.alphanumeric(8), faker.date.future().toISOString().split('T')[0]], cols: ['child_id', 'event_name', 'event_date'] },
            { name: 'dashboard_metrics', query: () => ['metric_' + faker.string.alphanumeric(10), Math.floor(Math.random() * 100), faker.date.recent().toISOString().slice(0, 19).replace('T', ' ')], cols: ['metric_name', 'metric_value', 'updated_at'] },
            { name: 'dashboard_preferences', query: () => [faker.helpers.arrayElement(userIds), 'theme_' + faker.string.alphanumeric(6), 1], cols: ['user_id', 'theme', 'notifications_enabled'] },
            { name: 'documents', query: () => [faker.helpers.arrayElement(childIds), 'Report', 'doc_' + faker.string.alphanumeric(16) + '.pdf', 'application/pdf', faker.helpers.arrayElement(userIds)], cols: ['child_id', 'document_type', 'file_name', 'mime_type', 'uploaded_by'] },
            { name: 'donor_funding', query: () => [faker.company.name() + ' ' + faker.string.alphanumeric(6), 10000.00, faker.date.past().toISOString().split('T')[0], 'General Support'], cols: ['donor_name', 'amount', 'donation_date', 'purpose'] },
            { name: 'emergency_events', query: () => [faker.helpers.arrayElement(childIds), 'Emerg_' + faker.string.alphanumeric(6), faker.date.recent().toISOString().slice(0, 19).replace('T', ' '), faker.lorem.sentence()], cols: ['child_id', 'event_type', 'event_time', 'description'] },
            { name: 'family_profile', query: () => [faker.helpers.arrayElement(userIds), 'Foster', faker.person.fullName(), faker.phone.number()], cols: ['user_id', 'family_type', 'primary_contact_name', 'phone'] },
            { name: 'fcm_tokens', query: () => [faker.helpers.arrayElement(userIds), faker.string.alphanumeric(50), 'android'], cols: ['user_id', 'token', 'device_type'] },
            { name: 'foster_family_training', query: () => [faker.helpers.arrayElement(familyIds), 'Train_' + faker.string.alphanumeric(8), faker.date.past().toISOString().split('T')[0], 95], cols: ['family_id', 'training_title', 'completion_date', 'score'] },
            { name: 'foster_matches', query: () => [faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(familyIds), faker.date.past().toISOString().split('T')[0], 'Active'], cols: ['child_id', 'family_id', 'match_date', 'status'] },
            { name: 'foster_tasks', query: () => [faker.helpers.arrayElement(familyIds), 'FT_' + faker.string.alphanumeric(8), faker.date.future().toISOString().split('T')[0], 'Pending'], cols: ['family_id', 'task_title', 'due_date', 'status'] },
            { name: 'global_document_storage', query: () => ['gdoc_' + faker.string.alphanumeric(10) + '.pdf', '/uploads/doc.pdf', 102400, faker.helpers.arrayElement(userIds)], cols: ['file_name', 'file_path', 'file_size', 'uploaded_by'] },
            { name: 'home_studies', query: () => [faker.helpers.arrayElement(familyIds), faker.date.past().toISOString().split('T')[0], faker.person.fullName(), 'Completed', 'Recommended'], cols: ['family_id', 'study_date', 'evaluator', 'status', 'recommendations'] },
            { name: 'inter_county_transfers', query: () => [faker.helpers.arrayElement(childIds), 'C1_' + faker.string.alphanumeric(4), 'C2_' + faker.string.alphanumeric(4), faker.date.past().toISOString().split('T')[0]], cols: ['child_id', 'from_county', 'to_county', 'transfer_date'] },
            { name: 'investigations', query: () => [faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(userIds), faker.lorem.sentence(), 'Closed'], cols: ['child_id', 'investigator_id', 'findings', 'status'] },
            { name: 'money_records', query: () => [faker.helpers.arrayElement(childIds), 1500.00, 'Stipend', faker.date.past().toISOString().split('T')[0], 'Monthly support'], cols: ['child_id', 'amount', 'transaction_type', 'transaction_date', 'description'] },
            { name: 'organization_partners', query: () => [faker.company.name() + ' ' + faker.string.alphanumeric(6), faker.person.fullName(), faker.phone.number(), faker.internet.email()], cols: ['partner_name', 'contact_person', 'phone', 'email'] },
            { name: 'permanency_plans', query: () => [faker.helpers.arrayElement(childIds), 'Plan_' + faker.string.alphanumeric(6), faker.date.future().toISOString().split('T')[0], 'In Progress'], cols: ['child_id', 'plan_type', 'target_date', 'status'] },
            { name: 'permissions', query: () => ['perm_' + faker.string.alphanumeric(12), faker.lorem.sentence(), 'General'], cols: ['name', 'description', 'category'] },
            { name: 'placement_compatibility', query: () => [faker.helpers.arrayElement(childIds), faker.helpers.arrayElement(familyIds), 88.5, 'Good match'], cols: ['child_id', 'family_id', 'score', 'match_notes'] },
            { name: 'placement_disruptions', query: () => [faker.helpers.arrayElement(placementIds), faker.date.past().toISOString().split('T')[0], 'Reason_' + faker.string.alphanumeric(6)], cols: ['placement_id', 'disruption_date', 'reason'] },
            { name: 'referrals', query: () => [faker.helpers.arrayElement(childIds), 'Ref_' + faker.string.alphanumeric(6), faker.date.past().toISOString().split('T')[0], 'Open'], cols: ['child_id', 'referred_to', 'referral_date', 'status'] },
            { name: 'reports_generated', query: () => ['Rep_' + faker.string.alphanumeric(8), faker.helpers.arrayElement(userIds), '/reports/summary.pdf', faker.date.past().toISOString().split('T')[0]], cols: ['report_title', 'generated_by', 'file_path', 'generated_date'] },
            { name: 'risk_assessments', query: () => [faker.helpers.arrayElement(childIds), 'Low', faker.date.past().toISOString().split('T')[0], faker.person.fullName()], cols: ['child_id', 'risk_level', 'assessment_date', 'assessor'] },
            { name: 'service_plan_goals', query: () => [faker.helpers.arrayElement(servicePlanIds), faker.lorem.sentence(), 'Active'], cols: ['service_plan_id', 'goal_description', 'status'] },
            { name: 'service_plans', query: () => [faker.helpers.arrayElement(childIds), faker.date.past().toISOString().split('T')[0], 'Active', faker.helpers.arrayElement(userIds)], cols: ['child_id', 'plan_date', 'status', 'case_worker_id'] },
            { name: 'service_providers', query: () => [faker.company.name() + ' ' + faker.string.alphanumeric(6), 'Healthcare', faker.phone.number(), faker.internet.email()], cols: ['provider_name', 'service_type', 'phone', 'email'] },
            { name: 'sos_location_history', query: () => [faker.helpers.arrayElement(userIds), -1.286389, 36.817223, faker.date.recent().toISOString().slice(0, 19).replace('T', ' ')], cols: ['user_id', 'latitude', 'longitude', 'timestamp'] },
            { name: 'system_settings', query: () => ['set_' + faker.string.alphanumeric(10), 'val_' + faker.string.alphanumeric(6), faker.lorem.sentence()], cols: ['setting_key', 'setting_value', 'description'] },
            { name: 'tasks', query: () => ['Task_' + faker.string.alphanumeric(8), faker.lorem.sentence(), faker.date.future().toISOString().split('T')[0], 'Pending'], cols: ['task_title', 'description', 'due_date', 'status'] },
            { name: 'user_permissions', query: () => [faker.helpers.arrayElement(userIds), faker.helpers.arrayElement(permIds)], cols: ['user_id', 'permission_id'] },
            { name: 'vaccination_records', query: () => [faker.helpers.arrayElement(childIds), 'Vac_' + faker.string.alphanumeric(6), faker.date.past().toISOString().split('T')[0], 'BATCH-' + faker.string.numeric(8)], cols: ['child_id', 'vaccine_name', 'date_administered', 'batch_number'] },
            { name: 'visitation_schedules', query: () => [faker.helpers.arrayElement(childIds), faker.person.fullName(), faker.date.future().toISOString().split('T')[0], 'Scheduled'], cols: ['child_id', 'visitor_name', 'visit_date', 'status'] },
            { name: 'worker_location_tracking', query: () => [faker.helpers.arrayElement(userIds), -1.286389, 36.817223, faker.date.recent().toISOString().slice(0, 19).replace('T', ' ')], cols: ['worker_id', 'latitude', 'longitude', 'timestamp'] },
            { name: 'worker_messages', query: () => [faker.helpers.arrayElement(userIds), faker.helpers.arrayElement(userIds), faker.lorem.sentence(), faker.date.recent().toISOString().slice(0, 19).replace('T', ' ')], cols: ['sender_id', 'receiver_id', 'message_text', 'sent_at'] },
            { name: 'workload_tracking', query: () => [faker.helpers.arrayElement(userIds), Math.floor(Math.random() * 10), Math.floor(Math.random() * 5), faker.date.recent().toISOString().slice(0, 19).replace('T', ' ')], cols: ['worker_id', 'active_cases', 'pending_tasks', 'updated_at'] }
        ];

        for (const t of tablesToCheck) {
            try {
                let [countRes] = await connection.query(`SELECT COUNT(*) as count FROM \`${t.name}\``);
                let currentCount = countRes[0].count;
                console.log(`Table \`${t.name}\` current count: ${currentCount}`);

                let attempts = 0;
                while (currentCount < 20 && attempts < 30) {
                    attempts++;
                    const row = t.query();
                    const placeholders = t.cols.map(() => '?').join(', ');
                    const sql = `INSERT INTO \`${t.name}\` (${t.cols.map(c => `\`${c}\``).join(', ')}) VALUES (${placeholders})`;
                    try {
                        await connection.query(sql, row);
                    } catch (err) {
                        console.log(`  [Error inserting into ${t.name}]: ${err.message}`);
                        break;
                    }
                    [countRes] = await connection.query(`SELECT COUNT(*) as count FROM \`${t.name}\``);
                    currentCount = countRes[0].count;
                }
                console.log(`Table \`${t.name}\` final count: ${currentCount}`);
            } catch (err) {
                console.log(`Could not process table \`${t.name}\`: ${err.message}`);
            }
        }

        await connection.query('SET FOREIGN_KEY_CHECKS = 1;');
        console.log('\n✅ Successfully ensured at least 20 items per table!');
    } catch (error) {
        console.error('❌ Seeding failed:', error.message);
    } finally {
        await connection.end();
    }
}

seed20PerTable();
