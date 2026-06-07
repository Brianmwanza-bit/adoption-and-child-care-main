package com.example.adoption_and_childcare.data.db

import android.content.Context
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.data.security.Security
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {
    
    fun initializeDatabase(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(appContext)
            // Check if database is already initialized
            val userCount = db.userDao().count()
            if (userCount > 0) return@launch // Already initialized
            
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
                    country = "Kenya",
                    status = "Active"
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
                    placementType = "Foster Home",
                    isCurrent = true
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
                    message = "Important update regarding case file CS-2024-${100+i}",
                    isRead = false
                )
                db.notificationDao().insertNotification(notif)
            }
        }
    }
}
