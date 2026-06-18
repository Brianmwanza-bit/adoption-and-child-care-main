package com.example.adoption_and_childcare.data.db

import android.content.Context
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.utils.Security

import androidx.room.withTransaction

/**
 * Utility object responsible for initializing the database with mock data.
 */
@Suppress("SpellCheckingInspection", "HardcodedStringLiteral", "LongMethod")
object DatabaseInitializer {

    /**
     * Initializes the database with sample data if it is empty.
     *
     * @param db The database instance to initialize.
     */
    suspend fun initializeDatabase(db: AppDatabase) {
        db.withTransaction {
                // Check if database is already initialized
                val userCount = db.userDao().count()
                if (userCount > 0) return@withTransaction // Already initialized
                
                // 1. Create Staff Users (10)
                val staffRoles = listOf("admin", "social_worker", "case_worker")
                val counties = listOf("Nairobi", "Mombasa", "Kisumu", "Nakuru", "Kiambu", "Uasin Gishu", "Machakos", "Kajiado", "Kilifi", "Nyeri")
                
                val userIds = mutableListOf<Int>()
                for (i in 1..10) {
                    val user = UserEntity(
                        username = "staff_$i",
                        passwordHash = Security.hashPassword("password$i"),
                        role = staffRoles[i % staffRoles.size],
                        email = "staff$i@adoptioncare.ke",
                        phone = "0711000${100+i}",
                        nationalIdNo = "1234567$i",
                        county = counties[i % counties.size]
                    )
                    userIds.add(db.userDao().insert(user).toInt())
                }
                
                // 2. Create Families (12)
                val familyNames = listOf("Kamau", "Onyango", "Mutua", "Ali", "Wanjala", "Kipchumba", "Muthoni", "Chebet", "Makena", "Mohamed", "Nekesa", "Githinji")
                val familyIds = mutableListOf<Int>()
                for (i in 0..11) {
                    val family = FamilyEntity(
                        primaryContactName = "${familyNames[i]} Family",
                        email = "${familyNames[i].lowercase()}@gmail.ke",
                        phone = "0722000${200+i}",
                        city = "City $i",
                        county = counties[i % counties.size],
                        country = "Kenya"
                    )
                    familyIds.add(db.familyDao().insert(family).toInt())
                }
                
                // 3. Create Children (15)
                val childNames = listOf("Kamau", "Achieng", "Moraa", "Wanjala", "Hassan", "Muthoni", "Kiprotich", "Atieno", "Mutua", "Fatuma", "Githinji", "Nekesa", "Kipchumba", "Halima", "Omondi")
                val childIds = mutableListOf<Int>()
                for (i in 0..14) {
                    val child = ChildEntity(
                        firstName = childNames[i],
                        lastName = "Surnam $i",
                        gender = if (i % 2 == 0) "Male" else "Female",
                        dateOfBirth = "201${i%9}-0${(i%9)+1}-15",
                        caseNumber = "CS-2024-${100+i}",
                        currentStatus = if (i < 5) "Placed" else "Active",
                        currentCounty = counties[i % counties.size],
                        assignedCaseWorker = userIds[i % userIds.size]
                    )
                    childIds.add(db.childDao().insert(child).toInt())
                }
                
                // 4. Create Adoption Applications (10)
                for (i in 0..9) {
                    val app = AdoptionApplicationEntity(
                        familyId = familyIds[i],
                        childId = childIds[i],
                        status = if (i % 3 == 0) "Approved" else if (i % 3 == 1) "Under Review" else "Pending",
                        notes = "Automatically generated mock application $i",
                        applicationNumber = "APP-2024-${500+i}"
                    )
                    db.adoptionApplicationDao().insert(app)
                }
                
                // 5. Create Home Studies (8)
                for (i in 0..7) {
                    val homeStudy = HomeStudyEntity(
                        familyId = familyIds[i],
                        result = if (i % 2 == 0) "Approved" else "Under Review",
                        notes = "Detailed home study report for family $i",
                        startedAt = "2024-01-10"
                    )
                    db.homeStudyDao().insert(homeStudy)
                }
                
                // 6. Create Placements (5)
                for (i in 0..4) {
                    val placement = PlacementEntity(
                        childId = childIds[i],
                        destinationFamilyId = familyIds[i],
                        startDate = "2024-02-15",
                        placementType = "Foster Home"
                    )
                    db.placementDao().insert(placement)
                }
                
                // 7. Create Case Reports (12)
                for (i in 0..11) {
                    val report = CaseReportEntity(
                        childId = childIds[i % childIds.size],
                        userId = userIds[i % userIds.size],
                        reportDate = "2024-03-20",
                        reportTitle = "Quarterly Progress Report $i",
                        content = "The child is adapting well to the current environment. No major concerns noted."
                    )
                    db.caseReportDao().insert(report)
                }
                
                // 8. Create Medical Records (10)
                val diagnoses = listOf("Common Cold", "Malaria", "Routine Checkup", "Flu", "Ear Infection")
                for (i in 0..9) {
                    val med = MedicalRecordEntity(
                        childId = childIds[i % childIds.size],
                        visitDate = "2024-04-10",
                        hospitalName = "Referral Hospital ${counties[i % counties.size]}",
                        diagnosis = diagnoses[i % diagnoses.size],
                        treatment = "Standard treatment protocol administered."
                    )
                    db.medicalRecordDao().insert(med)
                }
                
                // 9. Create Education Records (10)
                for (i in 0..9) {
                    val edu = EducationRecordEntity(
                        childId = childIds[i % childIds.size],
                        schoolName = "Primary School ${i+1}",
                        grade = "${(i % 8) + 1}",
                        enrollmentDate = "2024-01-05"
                    )
                    db.educationRecordDao().insert(edu)
                }
                
                // 10. Create Money Records (15)
                val types = listOf("Allowance", "Education", "Medical", "Clothing", "Other")
                for (i in 0..14) {
                    val money = MoneyRecordEntity(
                        childId = childIds[i % childIds.size],
                        amount = (500 * (i + 1)).toDouble(),
                        date = "2024-05-15",
                        transactionType = types[i % types.size],
                        description = "Monthly provision for ${types[i % types.size]}"
                    )
                    db.moneyRecordDao().insert(money)
                }

                // 11. Create Notifications (10)
                for (i in 0..9) {
                    val notif = NotificationEntity(
                        userId = userIds[0], // Admin/User 1
                        title = "System Update $i",
                        message = "Important update regarding case file CS-2024-${100+i}"
                    )
                    db.notificationDao().insertNotification(notif)
                }

                // 12. Create initial Foster Tasks
                for (i in 0..5) {
                    val task = FosterTaskEntity(
                        familyId = familyIds[i % familyIds.size],
                        caseWorkerId = userIds[i % userIds.size],
                        description = "Follow-up visit for ${familyNames[i % familyNames.size]} Family",
                        status = if (i % 2 == 0) "Urgent" else "Pending",
                        dueDate = "2024-06-${20 + i}",
                        createdAt = "2024-06-01"
                    )
                    db.fosterTaskDao().insert(task)
                }

                // 13. Create initial Audit Logs
                val actions = listOf("Created", "Updated", "Verified", "Deleted")
                val tables = listOf("children", "families", "documents", "placements")
                for (i in 0..10) {
                    val log = AuditLogEntity(
                        tableName = tables[i % tables.size],
                        recordId = i + 1,
                        action = actions[i % actions.size],
                        changedBy = userIds[i % userIds.size],
                        changedAt = "2024-06-${10 + (i/2)} 10:00:00"
                    )
                    db.auditLogDao().insert(log)
                }

                // 14. Seed New v15 Tables
                for (i in 0..4) {
                    // Investigation
                    db.investigationDao().insert(InvestigationEntity(
                        childId = childIds[i],
                        caseNumber = "INV-2024-00$i",
                        investigationType = "Safety Assessment",
                        openedDate = "2024-06-01",
                        status = "Open",
                        allegation = "Standard periodic review allegation mock."
                    ))
                    
                    // Service Plan
                    db.servicePlanDao().insert(ServicePlanEntity(
                        childId = childIds[i],
                        planName = "Permanency Plan 2024-Q3",
                        startDate = "2024-07-01",
                        status = "Active",
                        goalsSummary = "Reunification with biological family."
                    ))

                    // Vaccination
                    db.vaccinationRecordDao().insert(VaccinationRecordEntity(
                        childId = childIds[i],
                        vaccineName = "BCG",
                        administrationDate = "2024-05-10",
                        status = "Completed"
                    ))
                }
            }
    }
}
