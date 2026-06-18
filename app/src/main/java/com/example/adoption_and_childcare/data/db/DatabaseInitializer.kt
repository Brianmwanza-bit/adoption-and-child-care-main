package com.example.adoption_and_childcare.data.db

import android.util.Log
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.utils.Security
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * Utility object responsible for initializing the database with mock data for all 66 tables.
 */
object DatabaseInitializer {

    private const val TAG = "DatabaseInitializer"
    private var isInitialized = false
    
    @Suppress("HardcodedStringLiteral", "SpellCheckingInspection")
    private val firstNames = listOf("Samuel", "Amani", "David", "Faith", "Lucas", "Sarah", "Michael", "Jane", "John", "Mary", "Peter", "Esther", "Paul", "Ruth", "James", "Grace", "Joseph", "Alice", "Simon", "Mercy", "Andrew", "Lydia", "Thomas", "Rose", "Philip", "Joy", "Matthew", "Catherine", "Bartholomew", "Dorcas", "Stephen", "Margaret", "Daniel", "Elizabeth", "Isaac", "Rachael", "Jacob", "Rebecca", "Noah", "Hannah", "Joshua", "Miriam", "Caleb", "Deborah", "Gideon", "Priscilla", "Silas", "Tabitha", "Timothy", "Eunice")
    
    @Suppress("HardcodedStringLiteral", "SpellCheckingInspection")
    private val lastNames = listOf("Karanja", "Otieno", "Mutua", "Wambui", "Njoroge", "Kamau", "Ochieng", "Maina", "Muli", "Mwangi", "Odhiambo", "Nyambura", "Kiplagat", "Chepngetich", "Wanjiru", "Muthoni", "Kariuki", "Onyango", "Mulei", "Waweru", "Njeri", "Githinji", "Mugo", "Kimani", "Nduta", "Gitau", "Wanyoike", "Kibet", "Cherop", "Kiptoo", "Jepchirchir", "Lagat", "Tanui", "Kosgei", "Barsosio", "Biwott", "Korir", "Rotich", "Sang", "Yego", "Bett", "Koech", "Keter", "Cheruiyot", "Kipsang", "Kirui", "Kitur", "Limo", "Maiyo", "Rono")

    @Suppress("HardcodedStringLiteral", "SpellCheckingInspection")
    private val kenyanCounties = listOf(
        "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta", "Garissa", "Wajir", "Mandera", 
        "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", 
        "Nyeri", "Kirinyaga", "Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia", 
        "Uasin Gishu", "Elgeyo-Marakwet", "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", 
        "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma", "Busia", "Siaya", "Kisumu", "Homa Bay", 
        "Migori", "Kisii", "Nyamira", "Nairobi"
    )

    @Suppress("HardcodedStringLiteral", "SpellCheckingInspection")
    private val courtNames = listOf("Milimani Law Courts", "Mombasa Law Courts", "Nakuru Law Courts", "Kisumu Law Courts", "Eldoret Law Courts", "Nyeri Law Courts", "Malindi Law Courts", "Kericho Law Courts")

    /**
     * Initializes the main database with seed data if it's empty.
     * 
     * @param db The main [AppDatabase] instance.
     */
    @Suppress("HardcodedStringLiteral")
    suspend fun initializeDatabase(db: AppDatabase): Unit = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext
        Log.d(TAG, "Initializing AppDatabase...")
        db.withTransaction {
            if (db.userDao().count() > 0) {
                isInitialized = true
                return@withTransaction
            }
            seedAll(db)
            isInitialized = true
        }
    }

    /**
     * Initializes the minimal database with seed data if it's empty.
     * 
     * @param db The [AppDatabaseMinimal] instance.
     */
    @Suppress("HardcodedStringLiteral")
    suspend fun initializeMinimalDatabase(db: AppDatabaseMinimal): Unit = withContext(Dispatchers.IO) {
        Log.d(TAG, "Initializing AppDatabaseMinimal...")
        db.withTransaction {
            if (db.userDao().count() > 0) return@withTransaction
            seedAll(db)
        }
    }

    /**
     * Checks if Google Play Services is available on the device.
     * 
     * @param context The application context.
     * @return True if Google Play Services is available.
     */
    fun isGooglePlayServicesAvailable(context: android.content.Context): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val result = availability.isGooglePlayServicesAvailable(context)
        return result == ConnectionResult.SUCCESS
    }

    /**
     * Seeds all tables with sample data.
     * 
     * @param db The database instance implementing [BaseAppDatabase].
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedAll(db: BaseAppDatabase) {
        seedSystemData(db)
        val userIds = seedCoreUsers(db)
        val familyIds = seedCoreFamilies(db)
        val childIds = seedCoreChildren(db, userIds)

        (1..50).forEach { i ->
            val uId = userIds[(i - 1) % userIds.size]
            val cId = childIds[i - 1]
            val fId = familyIds[(i - 1) % familyIds.size]

            seedChildRelatedData(db, i, uId, cId, fId)
            seedCaseManagementData(db, i, uId, cId, fId)
            seedWelfareData(db, i, cId, childIds)
            seedAdministrativeData(db, i, uId, cId, fId, userIds)
        }
        Log.d(TAG, "Seeding complete for 66 tables with 50 entries.")
    }

    /**
     * Seeds system-wide data like counties, settings, and metrics.
     * 
     * @param db The database instance implementing [BaseAppDatabase].
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedSystemData(db: BaseAppDatabase) {
        // Seed Counties
        kenyanCounties.forEach { countyName ->
            db.countyDao().insert(CountyEntity(countyName = countyName))
        }

        // Seed Dashboard Metrics (matching ModernLandingPage.kt)
        db.dashboardMetricDao().insert(DashboardMetricEntity(metricName = "children_placed", metricLabel = "Children Placed", metricValue = 1200.0))
        db.dashboardMetricDao().insert(DashboardMetricEntity(metricName = "active_families", metricLabel = "Active Families", metricValue = 450.0))
        db.dashboardMetricDao().insert(DashboardMetricEntity(metricName = "success_rate", metricLabel = "Success Rate", metricValue = 98.0))
        db.dashboardMetricDao().insert(DashboardMetricEntity(metricName = "active_placements", metricLabel = "Active Placements", metricValue = 45.0))

        // Seed System Settings (Features & Testimonials)
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "feature_real_time", settingValue = "Monitor case progress and milestones instantly.", category = "Features"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "feature_secure_docs", settingValue = "Military-grade encryption for all sensitive records.", category = "Features"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "feature_family_match", settingValue = "Smart algorithms to find the perfect home for every child.", category = "Features"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "testimonial_quote", settingValue = "This platform transformed how we manage our case load. We've seen a 30% increase in placement speed.", category = "Testimonials"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "testimonial_author", settingValue = "Sarah Jenkins, Senior Case Manager", category = "Testimonials"))
        
        // Seed Lookup Lists (for UI dropdowns)
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "case_activity_types", settingValue = "Visit,Court,Medical,Education,Other", category = "Lookups"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "document_types", settingValue = "Birth Certificate,Medical Report,Court Order,School Report,Psychological Eval", category = "Lookups"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "placement_types", settingValue = "Foster,Adoption,Kinship,Emergency", category = "Lookups"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "user_roles", settingValue = "admin,worker,supervisor,manager", category = "Lookups"))

        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "Max_Cases_Per_Worker", settingValue = "15", category = "Policy"))
        db.systemSettingDao().insert(SystemSettingEntity(settingKey = "Default_County", settingValue = "Nairobi"))
    }

    /**
     * Seeds the core user table.
     * 
     * @param db The database instance.
     * @return A list of generated user IDs.
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedCoreUsers(db: BaseAppDatabase): List<Int> {
        return (1..10).map { i ->
            db.userDao().insert(UserEntity(
                username = "user_${i.toString().padStart(2, '0')}",
                passwordHash = Security.hashPassword("password$i"),
                role = if (i == 1) "admin" else "worker",
                email = "worker$i@adoption-care.org",
                phone = "07" + (10000000 + i).toString(),
                nationalIdNo = "ID-${10000 + i}",
                county = "Nairobi",
                photoData = generateSamplePhoto(),
                photoMimeType = "image/jpeg"
            )).toInt()
        }
    }

    /**
     * Seeds the core families table.
     * 
     * @param db The database instance.
     * @return A list of generated family IDs.
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedCoreFamilies(db: BaseAppDatabase): List<Int> {
        return (1..20).map { i ->
            db.familyDao().insert(FamilyEntity(
                primaryContactName = "${firstNames[i % 50]} ${lastNames[i % 50]}",
                email = "family$i@gmail.com",
                phone = "07" + (20000000 + i).toString(),
                city = "City $i",
                county = "County $i"
            )).toInt()
        }
    }

    /**
     * Seeds the core children table.
     * 
     * @param db The database instance.
     * @param userIds The list of existing user IDs to assign as caseworkers.
     * @return A list of generated child IDs.
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedCoreChildren(db: BaseAppDatabase, userIds: List<Int>): List<Int> {
        return (1..50).map { i ->
            db.childDao().insert(ChildEntity(
                firstName = firstNames[i - 1],
                lastName = lastNames[i - 1],
                gender = if (i % 2 == 0) "Male" else "Female",
                dateOfBirth = "201${i % 10}-0${(i % 9) + 1}-15",
                caseNumber = "CASE-KE-${1000 + i}",
                assignedCaseWorker = userIds[i % userIds.size]
            )).toInt()
        }
    }

    /**
     * Seeds data related to a specific child.
     * 
     * @param db The database instance.
     * @param i The current iteration index.
     * @param uId The user ID of the caseworker.
     * @param cId The child ID.
     * @param fId The family ID.
     */
    @Suppress("HardcodedStringLiteral", "SpellCheckingInspection")
    private suspend fun seedChildRelatedData(db: BaseAppDatabase, i: Int, uId: Int, cId: Int, fId: Int) {
        val childName = "${firstNames[i-1]} ${lastNames[i-1]}"
        
        // Birth Certificate
        db.documentDao().insert(DocumentEntity(
            childId = cId, 
            documentType = "Birth Certificate", 
            fileName = "birth_cert_${childName.replace(" ", "_").lowercase()}.pdf", 
            mimeType = "application/pdf", 
            fileData = generateSamplePdf("OFFICIAL BIRTH CERTIFICATE\nName: $childName\nDate: 2024-01-01\nVerified by System."),
            uploadedBy = uId
        ))
        
        // Medical Report
        db.documentDao().insert(DocumentEntity(
            childId = cId, 
            documentType = "Medical Report", 
            fileName = "medical_report_${childName.replace(" ", "_").lowercase()}.pdf", 
            mimeType = "application/pdf", 
            fileData = generateSamplePdf("MEDICAL CLEARANCE REPORT\nPatient: $childName\nStatus: Healthy\nVaccinations: Up to date."),
            uploadedBy = uId
        ))

        // Court Order
        if (i % 3 == 0) {
            db.documentDao().insert(DocumentEntity(
                childId = cId, 
                documentType = "Court Order", 
                fileName = "court_order_${childName.replace(" ", "_").lowercase()}.pdf", 
                mimeType = "application/pdf", 
                fileData = generateSamplePdf("IN THE HIGH COURT OF KENYA\nRE: $childName\nORDER: Placement approved for immediate action."),
                uploadedBy = uId
            ))
        }
        
        db.placementDao().insert(PlacementEntity(childId = cId, destinationFamilyId = fId, startDate = "2024-01-01", placementType = "Foster"))
        db.caseReportDao().insert(CaseReportEntity(childId = cId, userId = uId, reportDate = "2024-01-01", reportTitle = "Progress Report - $childName", content = "The child is adapting well to the new environment. Social interactions are positive."))
        db.courtCaseDao().insert(CourtCaseEntity(childId = cId, caseNumber = "CRT-${100+i}", courtName = courtNames[i % courtNames.size]))
        db.educationRecordDao().insert(EducationRecordEntity(childId = cId, schoolName = "Sunshine Academy $i", grade = "Grade ${(i % 8) + 1}"))
        db.guardianDao().insert(GuardianEntity(childId = cId, firstName = "Guardian", lastName = lastNames[i % 50], relationship = "Relative", phone = "07" + (30000000 + i).toString()))
        db.medicalRecordDao().insert(MedicalRecordEntity(childId = cId, visitDate = "2024-01-01", hospitalName = "General Hospital $i", diagnosis = "General Checkup - Normal"))
        db.moneyRecordDao().insert(MoneyRecordEntity(childId = cId, amount = 1000.0 + (i * 10), transactionType = "Welfare Support", date = "2024-01-01"))
    }

    /**
     * Generates a minimal valid PDF byte array.
     * 
     * @param content The text content to include in the PDF.
     * @return The PDF file data.
     */
    @Suppress("SpellCheckingInspection")
    private fun generateSamplePdf(content: String): ByteArray {
        @Suppress("HardcodedStringLiteral", "SpellCheckingInspection")
        val pdfHeader = "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << >> >>\nendobj\n4 0 obj\n<< /Length ${content.length + 44} >>\nstream\nBT /F1 12 Tf 70 700 Td ($content) Tj ET\nendstream\nendobj\nxref\n0 5\n0000000000 65535 f \ntrailer\n<< /Size 5 /Root 1 0 R >>\nstartxref\n315\n%%EOF"
        return pdfHeader.toByteArray(Charsets.UTF_8)
    }

    /**
     * Generates a sample photo byte array (placeholder JPEG header).
     * 
     * @return The sample photo data.
     */
    private fun generateSamplePhoto(): ByteArray {
        // Minimal JPEG-like binary data
        return byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00.toByte(), 0x10.toByte(),
            'J'.code.toByte(), 'F'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 0x00.toByte(),
            0x01.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte()
        )
    }

    /**
     * Seeds case management related data.
     * 
     * @param db The database instance.
     * @param i The current iteration index.
     * @param uId The user ID of the caseworker.
     * @param cId The child ID.
     * @param fId The family ID.
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedCaseManagementData(db: BaseAppDatabase, i: Int, uId: Int, cId: Int, fId: Int) {
        db.caseloadDao().insert(CaseloadEntity(workerId = uId, date = "2024-01-01", activeCases = 5, pendingReviews = 2, overdueTasks = 1, capacityPercentage = 50.0))
        db.caseUrgencyFlagDao().insert(CaseUrgencyFlagEntity(caseId = i, flagType = if (i % 5 == 0) "Critical" else "Standard"))
        db.caseActivityDao().insert(CaseActivityEntity(caseId = i, activityType = "Home Visit", title = "Quarterly Assessment"))
        db.caseDeadlineDao().insert(CaseDeadlineEntity(caseId = i, deadlineType = "Court Hearing", dueDate = "2024-12-${(i % 28) + 1}", title = "Finalize Placement"))
        db.caseApprovalDao().insert(CaseApprovalEntity(caseId = i, approvalType = "Permanent Adoption"))
        db.placementCompatibilityDao().insert(PlacementCompatibilityEntity(childId = cId, familyId = fId, compatibilityScore = 75.0 + (i % 25)))
        db.workloadTrackingDao().insert(WorkloadTrackingEntity(caseworkerId = uId, trackingDate = "2024-01-01", totalActiveCases = 10, casesWithUrgentFlags = 2, timeLoggedHours = 40.0))
    }

    /**
     * Seeds child welfare and assessment data.
     * 
     * @param db The database instance.
     * @param i The current iteration index.
     * @param cId The child ID.
     * @param childIds All available child IDs for sibling links.
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedWelfareData(db: BaseAppDatabase, i: Int, cId: Int, childIds: List<Int>) {
        db.childBehaviorAssessmentDao().insert(ChildBehaviorAssessmentEntity(childId = cId, assessmentDate = "2024-01-01", overallScore = 80 + (i % 20)))
        db.childWelfareIncidentDao().insert(ChildWelfareIncidentEntity(childId = cId, incidentDate = "2024-01-01", incidentType = "Health Concern"))
        db.vaccinationRecordDao().insert(VaccinationRecordEntity(childId = cId, vaccineName = "BCG/Polio", administrationDate = "2024-01-01"))
        db.siblingDao().insert(SiblingEntity(childId = cId, siblingChildId = if (i < 50) childIds[i] else childIds[0]))
        db.consentRecordDao().insert(ConsentRecordEntity(childId = cId, consentType = "Educational Trip", consentDate = "2024-01-01"))
        db.childServicesReferralDao().insert(ChildServicesReferralEntity(childId = cId, serviceType = "Counseling", referralDate = "2024-01-01"))
        db.investigationDao().insert(InvestigationEntity(childId = cId, openedDate = "2024-01-01", status = "Closed"))
        db.servicePlanDao().insert(ServicePlanEntity(childId = cId, planName = "Stability Plan", startDate = "2024-01-01"))
        db.servicePlanGoalDao().insert(ServicePlanGoalEntity(planId = i, goalDescription = "Achieve emotional stability"))
        db.visitationScheduleDao().insert(VisitationScheduleEntity(childId = cId, visitorName = "Aunt ${lastNames[i % 50]}", visitationDate = "2024-01-01"))
        db.referralDao().insert(ReferralEntity(childId = cId, referralType = "Specialist", referralDate = "2024-01-01"))
        db.aftercarePlanDao().insert(AftercarePlanEntity(childId = cId, planName = "Graduation Support", startDate = "2025-01-01"))
    }

    /**
     * Seeds administrative, logs, and system preference data.
     * 
     * @param db The database instance implementing [BaseAppDatabase].
     * @param i The current iteration index.
     * @param uId The user ID.
     * @param cId The child ID.
     * @param fId The family ID.
     * @param userIds All available user IDs for messaging links.
     */
    @Suppress("HardcodedStringLiteral")
    private suspend fun seedAdministrativeData(db: BaseAppDatabase, i: Int, uId: Int, cId: Int, fId: Int, userIds: List<Int>) {
        val pId = db.permissionDao().insert(PermissionEntity(name = "Access_Child_Records_$i")).toInt()
        db.userPermissionDao().insert(UserPermissionEntity(userId = uId, permissionId = pId))
        
        db.auditLogDao().insert(AuditLogEntity(tableName = "documents", recordId = i, action = "Upload", changedBy = uId))
        db.adoptionApplicationDao().insert(AdoptionApplicationEntity(familyId = fId, submittedAt = "2024-01-01"))
        db.homeStudyDao().insert(HomeStudyEntity(familyId = fId, result = "Recommended", completedAt = "2024-01-01"))
        db.notificationDao().insertNotification(NotificationEntity(userId = uId, title = "New Task Assigned", message = "Please review case CRT-${100+i}"))
        db.syncQueueDao().insert(SyncQueueEntity(tableName = "children", operation = "UPDATE", recordId = cId.toString(), payload = "{}", createdAt = System.currentTimeMillis()))
        db.sosLocationDao().insert(SOSLocationEntity(sosEventId = "SOS-EVENT-${1000+i}", latitude = -1.28 + (i * 0.001), longitude = 36.82 + (i * 0.001), accuracy = 5.0f, timestamp = System.currentTimeMillis()))
        db.backgroundCheckDao().insert(BackgroundCheckEntity(userId = uId, status = "Completed", requestedAt = "2024-01-01"))
        db.fosterTaskDao().insert(FosterTaskEntity(familyId = fId, description = "Home safety check"))
        db.fosterMatchDao().insert(FosterMatchEntity(familyId = fId, childId = cId, status = "Active"))
        
        db.taskDao().insert(TaskEntity(title = "Document Verification", priority = "High", status = "Pending"))
        db.actionItemDao().insert(ActionItemEntity(title = "Follow up with Court", priority = "Normal"))
        db.dashboardMetricDao().insert(DashboardMetricEntity(metricName = "Active_Placements", metricValue = 45.0))
        db.dashboardPreferenceDao().insert(DashboardPreferenceEntity(userId = uId))
        db.criticalDateDao().insert(CriticalDateEntity(childId = cId, dateType = "Annual Review", eventDate = "2024-12-15"))
        db.workerMessageDao().insert(WorkerMessageEntity(senderId = userIds[0], recipientId = uId, title = "Team Sync", content = "Meeting at 10 AM tomorrow."))
        db.riskAssessmentDao().insert(RiskAssessmentEntity(childId = cId, assessmentDate = "2024-01-01"))
        db.permanencyPlanDao().insert(PermanencyPlanEntity(childId = cId, primaryGoal = "Long-term Foster Care"))
        
        db.organizationPartnerDao().insert(OrganizationPartnerEntity(partnerName = "Global Child Welfare NGO"))
        db.serviceProviderDao().insert(ServiceProviderEntity(providerName = "SafeHaven Counseling Services"))
        db.donorFundingDao().insert(DonorFundingEntity(donorName = "Main Street Foundation", amount = 50000.0, donationDate = "2024-01-01"))
        db.budgetAllocationDao().insert(BudgetAllocationEntity(category = "Education Support", allocatedAmount = 15000.0))
        db.countyOfficeDao().insert(CountyOfficeEntity(officeName = "${kenyanCounties[i % kenyanCounties.size]} County Office", county = kenyanCounties[i % kenyanCounties.size]))
        db.placementDisruptionDao().insert(PlacementDisruptionEntity(placementId = i, childId = cId, disruptionDate = "2024-01-01"))
        db.fosterFamilyTrainingDao().insert(FosterFamilyTrainingEntity(familyId = fId, trainingName = "Trauma Informed Care", trainingDate = "2024-01-01"))
        db.reportGeneratedDao().insert(ReportGeneratedEntity(reportName = "Quarterly_Summary_Q4.pdf"))
        db.emergencyEventDao().insert(EmergencyEventEntity(childId = cId, eventType = "Sudden Illness"))
        db.globalDocumentStorageDao().insert(GlobalDocumentStorageEntity(documentName = "Policy_Manual_2024.pdf"))
        db.interCountyTransferDao().insert(InterCountyTransferEntity(childId = cId, transferDate = "2024-01-01"))
        db.workerLocationTrackingDao().insert(WorkerLocationTrackingEntity(userId = uId, latitude = -1.28, longitude = 36.82, trackingTime = "2024-01-01 09:00:00"))
        db.familyMeetingDao().insert(FamilyMeetingEntity(familyId = fId, meetingDate = "2024-01-01", topic = "Placement Stability Review"))
        db.childDevelopmentMetricDao().insert(ChildDevelopmentMetricEntity(childId = cId, measurementDate = "2024-01-01", weightKg = 15.0 + (i % 10), heightCm = 100.0 + (i % 20)))
        db.staffResourceDao().insert(StaffResourceEntity(userId = uId, resourceName = "Company Vehicle #$i", resourceType = "Transport"))
    }

    /**
     * Checks the status of the cloud database connection.
     */
    @Suppress("HardcodedStringLiteral")
    fun checkCloudDatabaseStatus() {
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            Log.i(TAG, "Firestore linked: ${firestore.app.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Firestore error: ${e.message}")
        }
    }
}
