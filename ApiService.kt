import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class AnalyticsSummaryResponse(
    val user_count: Int,
    val family_count: Int,
    val task_count: Int,
    val match_count: Int,
    val background_check_count: Int
)

data class RoleBreakdownResponse(
    val role: String,
    val count: Int
)

data class NotificationEntity(
    val id: Int,
    val user_id: Int?,
    val message: String,
    val sent_at: String,
    val is_read: Boolean
)

data class UnreadCountResponse(
    val unread: Int
)

interface ApiService {
    // USERS
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): List<UsersEntity>
    @POST("users")
    suspend fun createUser(@Header("Authorization") token: String, @Body user: UsersEntity): Response<Unit>
    @PUT("users/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body user: UsersEntity): Response<Unit>
    @DELETE("users/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // CHILDREN
    @GET("children")
    suspend fun getChildren(@Header("Authorization") token: String): List<ChildrenEntity>
    @POST("children")
    suspend fun createChild(@Header("Authorization") token: String, @Body child: ChildrenEntity): Response<Unit>
    @PUT("children/{id}")
    suspend fun updateChild(@Header("Authorization") token: String, @Path("id") id: Int, @Body child: ChildrenEntity): Response<Unit>
    @DELETE("children/{id}")
    suspend fun deleteChild(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // GUARDIANS
    @GET("guardians")
    suspend fun getGuardians(@Header("Authorization") token: String): List<GuardiansEntity>
    @POST("guardians")
    suspend fun createGuardian(@Header("Authorization") token: String, @Body guardian: GuardiansEntity): Response<Unit>
    @PUT("guardians/{id}")
    suspend fun updateGuardian(@Header("Authorization") token: String, @Path("id") id: Int, @Body guardian: GuardiansEntity): Response<Unit>
    @DELETE("guardians/{id}")
    suspend fun deleteGuardian(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // COURT CASES
    @GET("court_cases")
    suspend fun getCourtCases(@Header("Authorization") token: String): List<CourtCasesEntity>
    @POST("court_cases")
    suspend fun createCourtCase(@Header("Authorization") token: String, @Body case: CourtCasesEntity): Response<Unit>
    @PUT("court_cases/{id}")
    suspend fun updateCourtCase(@Header("Authorization") token: String, @Path("id") id: Int, @Body case: CourtCasesEntity): Response<Unit>
    @DELETE("court_cases/{id}")
    suspend fun deleteCourtCase(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // PLACEMENTS
    @GET("placements")
    suspend fun getPlacements(@Header("Authorization") token: String): List<PlacementsEntity>
    @POST("placements")
    suspend fun createPlacement(@Header("Authorization") token: String, @Body placement: PlacementsEntity): Response<Unit>
    @PUT("placements/{id}")
    suspend fun updatePlacement(@Header("Authorization") token: String, @Path("id") id: Int, @Body placement: PlacementsEntity): Response<Unit>
    @DELETE("placements/{id}")
    suspend fun deletePlacement(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // MEDICAL RECORDS
    @GET("medical_records")
    suspend fun getMedicalRecords(@Header("Authorization") token: String): List<MedicalRecordsEntity>
    @POST("medical_records")
    suspend fun createMedicalRecord(@Header("Authorization") token: String, @Body record: MedicalRecordsEntity): Response<Unit>
    @PUT("medical_records/{id}")
    suspend fun updateMedicalRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: MedicalRecordsEntity): Response<Unit>
    @DELETE("medical_records/{id}")
    suspend fun deleteMedicalRecord(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // CASE REPORTS
    @GET("case_reports")
    suspend fun getCaseReports(@Header("Authorization") token: String): List<CaseReportsEntity>
    @POST("case_reports")
    suspend fun createCaseReport(@Header("Authorization") token: String, @Body report: CaseReportsEntity): Response<Unit>
    @PUT("case_reports/{id}")
    suspend fun updateCaseReport(@Header("Authorization") token: String, @Path("id") id: Int, @Body report: CaseReportsEntity): Response<Unit>
    @DELETE("case_reports/{id}")
    suspend fun deleteCaseReport(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // MONEY RECORDS
    @GET("money_records")
    suspend fun getMoneyRecords(@Header("Authorization") token: String): List<MoneyRecordsEntity>
    @POST("money_records")
    suspend fun createMoneyRecord(@Header("Authorization") token: String, @Body record: MoneyRecordsEntity): Response<Unit>
    @PUT("money_records/{id}")
    suspend fun updateMoneyRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: MoneyRecordsEntity): Response<Unit>
    @DELETE("money_records/{id}")
    suspend fun deleteMoneyRecord(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // EDUCATION RECORDS
    @GET("education_records")
    suspend fun getEducationRecords(@Header("Authorization") token: String): List<EducationRecordsEntity>
    @POST("education_records")
    suspend fun createEducationRecord(@Header("Authorization") token: String, @Body record: EducationRecordsEntity): Response<Unit>
    @PUT("education_records/{id}")
    suspend fun updateEducationRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: EducationRecordsEntity): Response<Unit>
    @DELETE("education_records/{id}")
    suspend fun deleteEducationRecord(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // DOCUMENTS
    @GET("documents")
    suspend fun getDocuments(@Header("Authorization") token: String): List<DocumentsEntity>
    @POST("documents")
    suspend fun createDocument(@Header("Authorization") token: String, @Body document: DocumentsEntity): Response<Unit>
    @PUT("documents/{id}")
    suspend fun updateDocument(@Header("Authorization") token: String, @Path("id") id: Int, @Body document: DocumentsEntity): Response<Unit>
    @DELETE("documents/{id}")
    suspend fun deleteDocument(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // AUDIT LOGS
    @GET("audit_logs")
    suspend fun getAuditLogs(@Header("Authorization") token: String): List<AuditLogsEntity>
    @POST("audit_logs")
    suspend fun createAuditLog(@Header("Authorization") token: String, @Body log: AuditLogsEntity): Response<Unit>
    @PUT("audit_logs/{id}")
    suspend fun updateAuditLog(@Header("Authorization") token: String, @Path("id") id: Int, @Body log: AuditLogsEntity): Response<Unit>
    @DELETE("audit_logs/{id}")
    suspend fun deleteAuditLog(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // PERMISSIONS
    @GET("permissions")
    suspend fun getPermissions(@Header("Authorization") token: String): List<PermissionsEntity>
    @POST("permissions")
    suspend fun createPermission(@Header("Authorization") token: String, @Body permission: PermissionsEntity): Response<Unit>
    @PUT("permissions/{id}")
    suspend fun updatePermission(@Header("Authorization") token: String, @Path("id") id: Int, @Body permission: PermissionsEntity): Response<Unit>
    @DELETE("permissions/{id}")
    suspend fun deletePermission(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // USER PERMISSIONS
    @GET("user_permissions")
    suspend fun getUserPermissions(@Header("Authorization") token: String): List<UserPermissionsEntity>
    @POST("user_permissions")
    suspend fun createUserPermission(@Header("Authorization") token: String, @Body userPermission: UserPermissionsEntity): Response<Unit>
    @PUT("user_permissions/{user_id}/{permission_id}")
    suspend fun updateUserPermission(@Header("Authorization") token: String, @Path("user_id") userId: Int, @Path("permission_id") permissionId: Int, @Body userPermission: UserPermissionsEntity): Response<Unit>
    @DELETE("user_permissions/{user_id}/{permission_id}")
    suspend fun deleteUserPermission(@Header("Authorization") token: String, @Path("user_id") userId: Int, @Path("permission_id") permissionId: Int): Response<Unit>

    // FAMILY PROFILE
    @GET("family_profile")
    suspend fun getFamilyProfiles(@Header("Authorization") token: String): List<FamilyProfileEntity>
    @POST("family_profile")
    suspend fun createFamilyProfile(@Header("Authorization") token: String, @Body profile: FamilyProfileEntity): Response<Unit>
    @PUT("family_profile/{id}")
    suspend fun updateFamilyProfile(@Header("Authorization") token: String, @Path("id") id: Int, @Body profile: FamilyProfileEntity): Response<Unit>
    @DELETE("family_profile/{id}")
    suspend fun deleteFamilyProfile(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // FOSTER TASKS
    @GET("foster_tasks")
    suspend fun getFosterTasks(@Header("Authorization") token: String): List<FosterTasksEntity>
    @POST("foster_tasks")
    suspend fun createFosterTask(@Header("Authorization") token: String, @Body task: FosterTasksEntity): Response<Unit>
    @PUT("foster_tasks/{id}")
    suspend fun updateFosterTask(@Header("Authorization") token: String, @Path("id") id: Int, @Body task: FosterTasksEntity): Response<Unit>
    @DELETE("foster_tasks/{id}")
    suspend fun deleteFosterTask(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // FOSTER MATCHES
    @GET("foster_matches")
    suspend fun getFosterMatches(@Header("Authorization") token: String): List<FosterMatchesEntity>
    @POST("foster_matches")
    suspend fun createFosterMatch(@Header("Authorization") token: String, @Body match: FosterMatchesEntity): Response<Unit>
    @PUT("foster_matches/{id}")
    suspend fun updateFosterMatch(@Header("Authorization") token: String, @Path("id") id: Int, @Body match: FosterMatchesEntity): Response<Unit>
    @DELETE("foster_matches/{id}")
    suspend fun deleteFosterMatch(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // BACKGROUND CHECKS
    @GET("background_checks")
    suspend fun getBackgroundChecks(@Header("Authorization") token: String): List<BackgroundChecksEntity>
    @POST("background_checks")
    suspend fun createBackgroundCheck(@Header("Authorization") token: String, @Body check: BackgroundChecksEntity): Response<Unit>
    @PUT("background_checks/{id}")
    suspend fun updateBackgroundCheck(@Header("Authorization") token: String, @Path("id") id: Int, @Body check: BackgroundChecksEntity): Response<Unit>
    @DELETE("background_checks/{id}")
    suspend fun deleteBackgroundCheck(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    // --- ANALYTICS ---
    @GET("analytics/summary")
    suspend fun getAnalyticsSummary(@Header("Authorization") token: String): AnalyticsSummaryResponse

    @GET("analytics/roles")
    suspend fun getRoleBreakdown(@Header("Authorization") token: String): List<RoleBreakdownResponse>

    @GET("analytics/recent-activity")
    suspend fun getRecentActivity(@Header("Authorization") token: String): List<AuditLogsEntity>

    @GET("analytics/pending-background-checks")
    suspend fun getPendingBackgroundChecks(@Header("Authorization") token: String): List<BackgroundChecksEntity>

    // --- NOTIFICATIONS ---
    @GET("notifications/history")
    suspend fun getNotificationHistory(@Header("Authorization") token: String): List<NotificationEntity>

    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationCount(@Header("Authorization") token: String): UnreadCountResponse

    // --- FILE/PHOTO UPLOADS ---
    @Multipart
    @POST("children/{id}/photo")
    suspend fun uploadChildPhoto(
        @Header("Authorization") token: String,
        @Path("id") childId: Int,
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @POST("documents/{id}/file")
    suspend fun uploadDocumentFile(
        @Header("Authorization") token: String,
        @Path("id") documentId: Int,
        @Part file: MultipartBody.Part
    ): Response<Unit>
} 