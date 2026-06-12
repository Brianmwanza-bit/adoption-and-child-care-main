package com.example.adoption_and_childcare.network

import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Children Endpoints
    @GET("children")
    suspend fun getChildren(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.ChildEntity>>

    @GET("children/{id}")
    suspend fun getChildById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.ChildEntity>

    @POST("children")
    suspend fun createChild(@Header("Authorization") token: String, @Body child: com.example.adoption_and_childcare.data.db.entities.ChildEntity): Response<com.example.adoption_and_childcare.data.db.entities.ChildEntity>

    @PUT("children/{id}")
    suspend fun updateChild(@Header("Authorization") token: String, @Path("id") id: Int, @Body child: com.example.adoption_and_childcare.data.db.entities.ChildEntity): Response<com.example.adoption_and_childcare.data.db.entities.ChildEntity>

    @DELETE("children/{id}")
    suspend fun deleteChild(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Families Endpoints
    @GET("families")
    suspend fun getFamilies(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.FamilyEntity>>

    @GET("family_profile")
    suspend fun getFamilyProfiles(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.FamilyEntity>>

    @POST("family_profile")
    suspend fun createFamilyProfile(@Header("Authorization") token: String, @Body family: com.example.adoption_and_childcare.data.db.entities.FamilyEntity): Response<com.example.adoption_and_childcare.data.db.entities.FamilyEntity>

    @PUT("family_profile/{id}")
    suspend fun updateFamilyProfile(@Header("Authorization") token: String, @Path("id") id: Int, @Body family: com.example.adoption_and_childcare.data.db.entities.FamilyEntity): Response<com.example.adoption_and_childcare.data.db.entities.FamilyEntity>

    @DELETE("family_profile/{id}")
    suspend fun deleteFamilyProfile(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Users Endpoints
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.UserEntity>>

    @GET("users/{id}")
    suspend fun getUserById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.UserEntity>

    @PUT("users/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body user: com.example.adoption_and_childcare.data.db.entities.UserEntity): Response<com.example.adoption_and_childcare.data.db.entities.UserEntity>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Placements Endpoints
    @GET("placements")
    suspend fun getPlacements(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.PlacementEntity>>

    @POST("placements")
    suspend fun createPlacement(@Header("Authorization") token: String, @Body placement: com.example.adoption_and_childcare.data.db.entities.PlacementEntity): Response<com.example.adoption_and_childcare.data.db.entities.PlacementEntity>

    @PUT("placements/{id}")
    suspend fun updatePlacement(@Header("Authorization") token: String, @Path("id") id: Int, @Body placement: com.example.adoption_and_childcare.data.db.entities.PlacementEntity): Response<com.example.adoption_and_childcare.data.db.entities.PlacementEntity>

    @DELETE("placements/{id}")
    suspend fun deletePlacement(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Guardians Endpoints
    @GET("guardians")
    suspend fun getGuardians(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.GuardianEntity>>

    @GET("guardians/{id}")
    suspend fun getGuardianById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.GuardianEntity>

    @POST("guardians")
    suspend fun createGuardian(@Header("Authorization") token: String, @Body guardian: com.example.adoption_and_childcare.data.db.entities.GuardianEntity): Response<com.example.adoption_and_childcare.data.db.entities.GuardianEntity>

    @PUT("guardians/{id}")
    suspend fun updateGuardian(@Header("Authorization") token: String, @Path("id") id: Int, @Body guardian: com.example.adoption_and_childcare.data.db.entities.GuardianEntity): Response<com.example.adoption_and_childcare.data.db.entities.GuardianEntity>

    @DELETE("guardians/{id}")
    suspend fun deleteGuardian(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Court Cases Endpoints
    @GET("court-cases")
    suspend fun getCourtCases(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity>>

    @GET("court-cases/{id}")
    suspend fun getCourtCaseById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity>

    @POST("court-cases")
    suspend fun createCourtCase(@Header("Authorization") token: String, @Body courtCase: com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity): Response<com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity>

    @PUT("court-cases/{id}")
    suspend fun updateCourtCase(@Header("Authorization") token: String, @Path("id") id: Int, @Body courtCase: com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity): Response<com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity>

    @DELETE("court-cases/{id}")
    suspend fun deleteCourtCase(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Background Checks Endpoints
    @GET("background-checks")
    suspend fun getBackgroundChecks(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity>>

    @GET("background-checks/{id}")
    suspend fun getBackgroundCheckById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity>

    @POST("background-checks")
    suspend fun createBackgroundCheck(@Header("Authorization") token: String, @Body backgroundCheck: com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity): Response<com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity>

    @PUT("background-checks/{id}")
    suspend fun updateBackgroundCheck(@Header("Authorization") token: String, @Path("id") id: Int, @Body backgroundCheck: com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity): Response<com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity>

    @DELETE("background-checks/{id}")
    suspend fun deleteBackgroundCheck(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Foster Tasks Endpoints
    @GET("foster-tasks")
    suspend fun getFosterTasks(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity>>

    @GET("foster-tasks/{id}")
    suspend fun getFosterTaskById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity>

    @POST("foster-tasks")
    suspend fun createFosterTask(@Header("Authorization") token: String, @Body fosterTask: com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity): Response<com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity>

    @PUT("foster-tasks/{id}")
    suspend fun updateFosterTask(@Header("Authorization") token: String, @Path("id") id: Int, @Body fosterTask: com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity): Response<com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity>

    @DELETE("foster-tasks/{id}")
    suspend fun deleteFosterTask(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Foster Matches Endpoints
    @GET("foster-matches")
    suspend fun getFosterMatches(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity>>

    @GET("foster-matches/{id}")
    suspend fun getFosterMatchById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity>

    @POST("foster-matches")
    suspend fun createFosterMatch(@Header("Authorization") token: String, @Body fosterMatch: com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity): Response<com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity>

    @PUT("foster-matches/{id}")
    suspend fun updateFosterMatch(@Header("Authorization") token: String, @Path("id") id: Int, @Body fosterMatch: com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity): Response<com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity>

    @DELETE("foster-matches/{id}")
    suspend fun deleteFosterMatch(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Permissions Endpoints
    @GET("permissions")
    suspend fun getPermissions(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.PermissionEntity>>

    @GET("permissions/{id}")
    suspend fun getPermissionById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.PermissionEntity>

    @POST("permissions")
    suspend fun createPermission(@Header("Authorization") token: String, @Body permission: com.example.adoption_and_childcare.data.db.entities.PermissionEntity): Response<com.example.adoption_and_childcare.data.db.entities.PermissionEntity>

    @PUT("permissions/{id}")
    suspend fun updatePermission(@Header("Authorization") token: String, @Path("id") id: Int, @Body permission: com.example.adoption_and_childcare.data.db.entities.PermissionEntity): Response<com.example.adoption_and_childcare.data.db.entities.PermissionEntity>

    @DELETE("permissions/{id}")
    suspend fun deletePermission(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // User Permissions Endpoints
    @GET("user-permissions")
    suspend fun getUserPermissions(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity>>

    @GET("user-permissions/{id}")
    suspend fun getUserPermissionById(@Header("Authorization") token: String, @Path("id") id: Int): Response<com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity>

    @POST("user-permissions")
    suspend fun createUserPermission(@Header("Authorization") token: String, @Body userPermission: com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity): Response<com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity>

    @DELETE("user-permissions/{id}")
    suspend fun deleteUserPermission(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // System Settings Endpoints
    @GET("system-settings")
    suspend fun getSystemSettings(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>>

    @GET("system-settings/{key}")
    suspend fun getSystemSettingByKey(@Header("Authorization") token: String, @Path("key") key: String): Response<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>

    @POST("system-settings")
    suspend fun createSystemSetting(@Header("Authorization") token: String, @Body setting: com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity): Response<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>

    @PUT("system-settings/{id}")
    suspend fun updateSystemSetting(@Header("Authorization") token: String, @Path("id") id: Int, @Body setting: com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity): Response<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>

    @DELETE("system-settings/{id}")
    suspend fun deleteSystemSetting(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Documents Endpoints
    @GET("documents")
    suspend fun getAllDocuments(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.DocumentEntity>>

    @POST("documents")
    suspend fun createDocument(@Header("Authorization") token: String, @Body document: com.example.adoption_and_childcare.data.db.entities.DocumentEntity): Response<com.example.adoption_and_childcare.data.db.entities.DocumentEntity>

    @PUT("documents/{id}")
    suspend fun updateDocument(@Header("Authorization") token: String, @Path("id") id: Int, @Body document: com.example.adoption_and_childcare.data.db.entities.DocumentEntity): Response<com.example.adoption_and_childcare.data.db.entities.DocumentEntity>

    @DELETE("documents/{id}")
    suspend fun deleteDocument(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Education Records Endpoints
    @GET("education-records")
    suspend fun getAllEducationRecords(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity>>

    @POST("education-records")
    suspend fun createEducationRecord(@Header("Authorization") token: String, @Body record: com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity): Response<com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity>

    @PUT("education-records/{id}")
    suspend fun updateEducationRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity): Response<com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity>

    @DELETE("education-records/{id}")
    suspend fun deleteEducationRecord(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Medical Records Endpoints
    @GET("medical-records")
    suspend fun getAllMedicalRecords(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity>>

    @POST("medical-records")
    suspend fun createMedicalRecord(@Header("Authorization") token: String, @Body record: com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity): Response<com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity>

    @PUT("medical-records/{id}")
    suspend fun updateMedicalRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity): Response<com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity>

    @DELETE("medical-records/{id}")
    suspend fun deleteMedicalRecord(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Money Records Endpoints
    @GET("money-records")
    suspend fun getAllMoneyRecords(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity>>

    @POST("money-records")
    suspend fun createMoneyRecord(@Header("Authorization") token: String, @Body record: com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity): Response<com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity>

    @PUT("money-records/{id}")
    suspend fun updateMoneyRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity): Response<com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity>

    @DELETE("money-records/{id}")
    suspend fun deleteMoneyRecord(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Home Studies Endpoints
    @GET("home-studies")
    suspend fun getAllHomeStudies(@Header("Authorization") token: String): Response<List<com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity>>

    @POST("home-studies")
    suspend fun createHomeStudy(@Header("Authorization") token: String, @Body study: com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity): Response<com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity>

    @PUT("home-studies/{id}")
    suspend fun updateHomeStudy(@Header("Authorization") token: String, @Path("id") id: Int, @Body study: com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity): Response<com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity>

    @DELETE("home-studies/{id}")
    suspend fun deleteHomeStudy(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // Analytics Endpoints
    @GET("analytics/summary")
    suspend fun getAnalyticsSummary(@Header("Authorization") token: String): Response<AnalyticsSummary>

    @GET("analytics/recent-activity")
    suspend fun getRecentActivity(@Header("Authorization") token: String): Response<List<ActivityItem>>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<List<NotificationEntity>>
    
    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationCount(@Header("Authorization") token: String): Response<UnreadCountResponse>
    
    @POST("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Header("Authorization") token: String, @Path("id") notificationId: Int): Response<Unit>

    @POST("auth/fcm-token")
    suspend fun updateFcmToken(
        @Header("Authorization") authToken: String,
        @Body request: FcmTokenRequest
    ): Response<Unit>

    // Sync Endpoints
    @POST("api/v2/sync/push")
    suspend fun pushSync(
        @Header("Authorization") token: String,
        @Body syncItems: List<SyncPushRequestItem>
    ): Response<SyncPushResponse>

    @GET("api/v2/sync/pull")
    suspend fun pullSync(
        @Header("Authorization") token: String,
        @Query("since") since: Long
    ): Response<SyncPullResponse>
}

data class SyncPushRequestItem(
    val table_name: String,
    val operation: String,
    val record_id: String,
    val payload: String
)

data class SyncPushResponse(val success: Boolean, val applied: Int, val errors: List<String>)

data class SyncPullResponse(
    val children: List<com.example.adoption_and_childcare.data.db.entities.ChildEntity>,
    val families: List<com.example.adoption_and_childcare.data.db.entities.FamilyEntity>,
    val placements: List<com.example.adoption_and_childcare.data.db.entities.PlacementEntity>,
    val medical_records: List<com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity>,
    val education_records: List<com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity>,
    val money_records: List<com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity>,
    val documents: List<com.example.adoption_and_childcare.data.db.entities.DocumentEntity>,
    val case_reports: List<com.example.adoption_and_childcare.data.db.entities.CaseReportEntity>,
    val court_cases: List<com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity>,
    val guardians: List<com.example.adoption_and_childcare.data.db.entities.GuardianEntity>,
    val adoption_applications: List<com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity>,
    val home_studies: List<com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity>,
    val audit_logs: List<com.example.adoption_and_childcare.data.db.entities.AuditLogEntity>,
    val notifications: List<NotificationEntity>,
    val background_checks: List<com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity>,
    val foster_tasks: List<com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity>,
    val foster_matches: List<com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity>,
    val permissions: List<com.example.adoption_and_childcare.data.db.entities.PermissionEntity>,
    val user_permissions: List<com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity>,
    val system_settings: List<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>,
    val users: List<com.example.adoption_and_childcare.data.db.entities.UserEntity>,
    val sos_locations: List<com.example.adoption_and_childcare.data.db.entities.SOSLocationEntity>,
    val tasks: List<com.example.adoption_and_childcare.data.db.entities.TaskEntity>,
    val action_items: List<com.example.adoption_and_childcare.data.db.entities.ActionItemEntity>,
    val dashboard_metrics: List<com.example.adoption_and_childcare.data.db.entities.DashboardMetricEntity>,
    val dashboard_preferences: List<com.example.adoption_and_childcare.data.db.entities.DashboardPreferenceEntity>,
    val critical_dates: List<com.example.adoption_and_childcare.data.db.entities.CriticalDateEntity>,
    val worker_messages: List<com.example.adoption_and_childcare.data.db.entities.WorkerMessageEntity>,
    val risk_assessments: List<com.example.adoption_and_childcare.data.db.entities.RiskAssessmentEntity>,
    val permanency_plans: List<com.example.adoption_and_childcare.data.db.entities.PermanencyPlanEntity>,
    val caseload: List<com.example.adoption_and_childcare.data.db.entities.CaseloadEntity>,
    val case_urgency_flags: List<com.example.adoption_and_childcare.data.db.entities.CaseUrgencyFlagEntity>,
    val case_activities: List<com.example.adoption_and_childcare.data.db.entities.CaseActivityEntity>,
    val case_deadlines: List<com.example.adoption_and_childcare.data.db.entities.CaseDeadlineEntity>,
    val case_approvals: List<com.example.adoption_and_childcare.data.db.entities.CaseApprovalEntity>,
    val placement_compatibility: List<com.example.adoption_and_childcare.data.db.entities.PlacementCompatibilityEntity>,
    val workload_tracking: List<com.example.adoption_and_childcare.data.db.entities.WorkloadTrackingEntity>
)

data class UnreadCountResponse(val unread: Int)

data class FcmTokenRequest(
    val fcmToken: String,
    val deviceId: String,
    val platform: String = "android"
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val id_number: String,
    val role: String,
    val county: String? = null,
    val sub_county: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val user: com.example.adoption_and_childcare.data.db.entities.UserEntity?,
    val error: ErrorDetail?
)

data class ErrorDetail(
    val code: String,
    val message: String
)

data class AnalyticsSummary(
    val totalChildren: Int,
    val totalFamilies: Int,
    val totalUsers: Int,
    val activePlacements: Int,
    val pendingApplications: Int,
    val pendingBackgroundChecks: Int
)

data class ActivityItem(
    val id: Int,
    val action: String,
    val tableName: String,
    val recordId: Int,
    val changedAt: String,
    val changedBy: String?
)