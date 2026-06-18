package com.example.adoption_and_childcare.network

import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.network.ApiConstants.ACTION_ITEMS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.ADOPTION_APPLICATIONS
import com.example.adoption_and_childcare.network.ApiConstants.ADOPTION_APPLICATIONS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.ADOPTION_APPLICATION_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.ANALYTICS_SUMMARY
import com.example.adoption_and_childcare.network.ApiConstants.AUDIT_LOGS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.AUTHORIZATION
import com.example.adoption_and_childcare.network.ApiConstants.BACKGROUND_CHECKS
import com.example.adoption_and_childcare.network.ApiConstants.BACKGROUND_CHECKS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.BACKGROUND_CHECK_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.CASE_ACTIVITIES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.CASE_APPROVALS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.CASE_DEADLINES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.CASE_REPORTS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.CASE_URGENCY_FLAGS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.CHILDREN
import com.example.adoption_and_childcare.network.ApiConstants.CHILD_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.COURT_CASES
import com.example.adoption_and_childcare.network.ApiConstants.COURT_CASES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.COURT_CASE_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.CRITICAL_DATES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.DASHBOARD_METRICS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.DASHBOARD_PREFERENCES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.DOCUMENTS
import com.example.adoption_and_childcare.network.ApiConstants.DOCUMENT_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.EDUCATION_RECORDS
import com.example.adoption_and_childcare.network.ApiConstants.EDUCATION_RECORDS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.EDUCATION_RECORD_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.FAMILIES
import com.example.adoption_and_childcare.network.ApiConstants.FAMILY_PROFILE
import com.example.adoption_and_childcare.network.ApiConstants.FAMILY_PROFILE_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.FOSTER_MATCHES
import com.example.adoption_and_childcare.network.ApiConstants.FOSTER_MATCHES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.FOSTER_MATCH_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.FOSTER_TASKS
import com.example.adoption_and_childcare.network.ApiConstants.FOSTER_TASKS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.FOSTER_TASK_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.GUARDIANS
import com.example.adoption_and_childcare.network.ApiConstants.GUARDIAN_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.HOME_STUDIES
import com.example.adoption_and_childcare.network.ApiConstants.HOME_STUDIES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.HOME_STUDY_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.ID
import com.example.adoption_and_childcare.network.ApiConstants.ID_NUMBER
import com.example.adoption_and_childcare.network.ApiConstants.LOGIN
import com.example.adoption_and_childcare.network.ApiConstants.MARK_NOTIFICATION_READ
import com.example.adoption_and_childcare.network.ApiConstants.MEDICAL_RECORDS
import com.example.adoption_and_childcare.network.ApiConstants.MEDICAL_RECORDS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.MEDICAL_RECORD_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.MONEY_RECORDS
import com.example.adoption_and_childcare.network.ApiConstants.MONEY_RECORDS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.MONEY_RECORD_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.NATIONAL_ID_NO
import com.example.adoption_and_childcare.network.ApiConstants.NOTIFICATIONS
import com.example.adoption_and_childcare.network.ApiConstants.OPERATION
import com.example.adoption_and_childcare.network.ApiConstants.PAYLOAD
import com.example.adoption_and_childcare.network.ApiConstants.PERMANENCY_PLANS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.PERMISSIONS
import com.example.adoption_and_childcare.network.ApiConstants.PERMISSION_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.PLACEMENTS
import com.example.adoption_and_childcare.network.ApiConstants.PLACEMENT_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.PLACEMENT_COMPATIBILITY_JSON
import com.example.adoption_and_childcare.network.ApiConstants.PLATFORM_ANDROID
import com.example.adoption_and_childcare.network.ApiConstants.RECORD_ID
import com.example.adoption_and_childcare.network.ApiConstants.REGISTER
import com.example.adoption_and_childcare.network.ApiConstants.RISK_ASSESSMENTS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.SINCE
import com.example.adoption_and_childcare.network.ApiConstants.SOS_LOCATIONS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.SUB_COUNTY
import com.example.adoption_and_childcare.network.ApiConstants.SYNC_PULL
import com.example.adoption_and_childcare.network.ApiConstants.SYNC_PUSH
import com.example.adoption_and_childcare.network.ApiConstants.SYSTEM_SETTINGS
import com.example.adoption_and_childcare.network.ApiConstants.SYSTEM_SETTINGS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.SYSTEM_SETTING_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.TABLE_NAME
import com.example.adoption_and_childcare.network.ApiConstants.UNREAD_NOTIFICATION_COUNT
import com.example.adoption_and_childcare.network.ApiConstants.UPDATE_FCM_TOKEN
import com.example.adoption_and_childcare.network.ApiConstants.USERS
import com.example.adoption_and_childcare.network.ApiConstants.USER_PERMISSIONS
import com.example.adoption_and_childcare.network.ApiConstants.USER_PERMISSIONS_JSON
import com.example.adoption_and_childcare.network.ApiConstants.USER_PERMISSION_BY_ID
import com.example.adoption_and_childcare.network.ApiConstants.WORKER_MESSAGES_JSON
import com.example.adoption_and_childcare.network.ApiConstants.WORKLOAD_TRACKING_JSON
import com.google.gson.annotations.SerializedName

import retrofit2.Response
import retrofit2.http.*

/**
 * Interface defining the API endpoints for the Adoption and Childcare application.
 */
interface ApiService {
    // Children Endpoints
    /**
     * Retrieves a list of all children.
     * @param token The authorization token.
     */
    @GET(CHILDREN)
    suspend fun getChildren(@Header(AUTHORIZATION) token: String): Response<List<ChildEntity>>

    /**
     * Creates a new child record.
     * @param token The authorization token.
     * @param child The child entity to create.
     */
    @POST(CHILDREN)
    suspend fun createChild(@Header(AUTHORIZATION) token: String, @Body child: ChildEntity): Response<ChildEntity>

    /**
     * Updates an existing child record.
     * @param token The authorization token.
     * @param id The ID of the child to update.
     * @param child The updated child entity.
     */
    @PUT(CHILD_BY_ID)
    suspend fun updateChild(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body child: ChildEntity): Response<ChildEntity>

    /**
     * Deletes a child record.
     * @param token The authorization token.
     * @param id The ID of the child to delete.
     */
    @DELETE(CHILD_BY_ID)
    suspend fun deleteChild(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Families Endpoints
    /**
     * Retrieves a list of all families.
     * @param token The authorization token.
     */
    @GET(FAMILIES)
    suspend fun getFamilies(@Header(AUTHORIZATION) token: String): Response<List<FamilyEntity>>

    /**
     * Creates a new family profile.
     * @param token The authorization token.
     * @param family The family entity to create.
     */
    @POST(FAMILY_PROFILE)
    suspend fun createFamilyProfile(@Header(AUTHORIZATION) token: String, @Body family: FamilyEntity): Response<FamilyEntity>

    /**
     * Updates an existing family profile.
     * @param token The authorization token.
     * @param id The ID of the family profile to update.
     * @param family The updated family entity.
     */
    @PUT(FAMILY_PROFILE_BY_ID)
    suspend fun updateFamilyProfile(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body family: FamilyEntity): Response<FamilyEntity>

    /**
     * Deletes a family profile.
     * @param token The authorization token.
     * @param id The ID of the family profile to delete.
     */
    @DELETE(FAMILY_PROFILE_BY_ID)
    suspend fun deleteFamilyProfile(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Adoption Applications Endpoints
    /**
     * Retrieves a list of all adoption applications.
     * @param token The authorization token.
     */
    @GET(ADOPTION_APPLICATIONS)
    suspend fun getAdoptionApplications(@Header(AUTHORIZATION) token: String): Response<List<AdoptionApplicationEntity>>

    /**
     * Creates a new adoption application.
     * @param token The authorization token.
     * @param application The application entity to create.
     */
    @POST(ADOPTION_APPLICATIONS)
    suspend fun createAdoptionApplication(@Header(AUTHORIZATION) token: String, @Body application: AdoptionApplicationEntity): Response<AdoptionApplicationEntity>

    /**
     * Updates an existing adoption application.
     * @param token The authorization token.
     * @param id The ID of the application to update.
     * @param application The updated application entity.
     */
    @PUT(ADOPTION_APPLICATION_BY_ID)
    suspend fun updateAdoptionApplication(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body application: AdoptionApplicationEntity): Response<AdoptionApplicationEntity>

    /**
     * Deletes an adoption application.
     * @param token The authorization token.
     * @param id The ID of the application to delete.
     */
    @DELETE(ADOPTION_APPLICATION_BY_ID)
    suspend fun deleteAdoptionApplication(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Users Endpoints
    /**
     * Retrieves a list of all users.
     * @param token The authorization token.
     */
    @GET(USERS)
    suspend fun getUsers(@Header(AUTHORIZATION) token: String): Response<List<UserEntity>>

    // Placements Endpoints
    /**
     * Retrieves a list of all placements.
     * @param token The authorization token.
     */
    @GET(PLACEMENTS)
    suspend fun getPlacements(@Header(AUTHORIZATION) token: String): Response<List<PlacementEntity>>

    /**
     * Creates a new placement.
     * @param token The authorization token.
     * @param placement The placement entity to create.
     */
    @POST(PLACEMENTS)
    suspend fun createPlacement(@Header(AUTHORIZATION) token: String, @Body placement: PlacementEntity): Response<PlacementEntity>

    /**
     * Updates an existing placement.
     * @param token The authorization token.
     * @param id The ID of the placement to update.
     * @param placement The updated placement entity.
     */
    @PUT(PLACEMENT_BY_ID)
    suspend fun updatePlacement(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body placement: PlacementEntity): Response<PlacementEntity>

    /**
     * Deletes a placement.
     * @param token The authorization token.
     * @param id The ID of the placement to delete.
     */
    @DELETE(PLACEMENT_BY_ID)
    suspend fun deletePlacement(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Guardians Endpoints
    /**
     * Retrieves a list of all guardians.
     * @param token The authorization token.
     */
    @GET(GUARDIANS)
    suspend fun getGuardians(@Header(AUTHORIZATION) token: String): Response<List<GuardianEntity>>

    /**
     * Creates a new guardian.
     * @param token The authorization token.
     * @param guardian The guardian entity to create.
     */
    @POST(GUARDIANS)
    suspend fun createGuardian(@Header(AUTHORIZATION) token: String, @Body guardian: GuardianEntity): Response<GuardianEntity>

    /**
     * Updates an existing guardian.
     * @param token The authorization token.
     * @param id The ID of the guardian to update.
     * @param guardian The updated guardian entity.
     */
    @PUT(GUARDIAN_BY_ID)
    suspend fun updateGuardian(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body guardian: GuardianEntity): Response<GuardianEntity>

    /**
     * Deletes a guardian.
     * @param token The authorization token.
     * @param id The ID of the guardian to delete.
     */
    @DELETE(GUARDIAN_BY_ID)
    suspend fun deleteGuardian(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Court Cases Endpoints
    /**
     * Creates a new court case.
     * @param token The authorization token.
     * @param courtCase The court case entity to create.
     */
    @POST(COURT_CASES)
    suspend fun createCourtCase(@Header(AUTHORIZATION) token: String, @Body courtCase: CourtCaseEntity): Response<CourtCaseEntity>

    /**
     * Updates an existing court case.
     * @param token The authorization token.
     * @param id The ID of the court case to update.
     * @param courtCase The updated court case entity.
     */
    @PUT(COURT_CASE_BY_ID)
    suspend fun updateCourtCase(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body courtCase: CourtCaseEntity): Response<CourtCaseEntity>

    /**
     * Deletes a court case.
     * @param token The authorization token.
     * @param id The ID of the court case to delete.
     */
    @DELETE(COURT_CASE_BY_ID)
    suspend fun deleteCourtCase(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Background Checks Endpoints
    /**
     * Creates a new background check.
     * @param token The authorization token.
     * @param backgroundCheck The background check entity to create.
     */
    @POST(BACKGROUND_CHECKS)
    suspend fun createBackgroundCheck(@Header(AUTHORIZATION) token: String, @Body backgroundCheck: BackgroundCheckEntity): Response<BackgroundCheckEntity>

    /**
     * Updates an existing background check.
     * @param token The authorization token.
     * @param id The ID of the background check to update.
     * @param backgroundCheck The updated background check entity.
     */
    @PUT(BACKGROUND_CHECK_BY_ID)
    suspend fun updateBackgroundCheck(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body backgroundCheck: BackgroundCheckEntity): Response<BackgroundCheckEntity>

    /**
     * Deletes a background check.
     * @param token The authorization token.
     * @param id The ID of the background check to delete.
     */
    @DELETE(BACKGROUND_CHECK_BY_ID)
    suspend fun deleteBackgroundCheck(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Foster Tasks Endpoints
    /**
     * Retrieves a list of all foster tasks.
     * @param token The authorization token.
     */
    @GET(FOSTER_TASKS)
    suspend fun getFosterTasks(@Header(AUTHORIZATION) token: String): Response<List<FosterTaskEntity>>

    /**
     * Creates a new foster task.
     * @param token The authorization token.
     * @param fosterTask The foster task entity to create.
     */
    @POST(FOSTER_TASKS)
    suspend fun createFosterTask(@Header(AUTHORIZATION) token: String, @Body fosterTask: FosterTaskEntity): Response<FosterTaskEntity>

    /**
     * Updates an existing foster task.
     * @param token The authorization token.
     * @param id The ID of the foster task to update.
     * @param fosterTask The updated foster task entity.
     */
    @PUT(FOSTER_TASK_BY_ID)
    suspend fun updateFosterTask(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body fosterTask: FosterTaskEntity): Response<FosterTaskEntity>

    /**
     * Deletes a foster task.
     * @param token The authorization token.
     * @param id The ID of the foster task to delete.
     */
    @DELETE(FOSTER_TASK_BY_ID)
    suspend fun deleteFosterTask(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Foster Matches Endpoints
    /**
     * Retrieves a list of all foster matches.
     * @param token The authorization token.
     */
    @GET(FOSTER_MATCHES)
    suspend fun getFosterMatches(@Header(AUTHORIZATION) token: String): Response<List<FosterMatchEntity>>

    /**
     * Creates a new foster match.
     * @param token The authorization token.
     * @param fosterMatch The foster match entity to create.
     */
    @POST(FOSTER_MATCHES)
    suspend fun createFosterMatch(@Header(AUTHORIZATION) token: String, @Body fosterMatch: FosterMatchEntity): Response<FosterMatchEntity>

    /**
     * Updates an existing foster match.
     * @param token The authorization token.
     * @param id The ID of the foster match to update.
     * @param fosterMatch The updated foster match entity.
     */
    @PUT(FOSTER_MATCH_BY_ID)
    suspend fun updateFosterMatch(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body fosterMatch: FosterMatchEntity): Response<FosterMatchEntity>

    /**
     * Deletes a foster match.
     * @param token The authorization token.
     * @param id The ID of the foster match to delete.
     */
    @DELETE(FOSTER_MATCH_BY_ID)
    suspend fun deleteFosterMatch(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Permissions Endpoints
    /**
     * Retrieves a list of all permissions.
     * @param token The authorization token.
     */
    @GET(PERMISSIONS)
    suspend fun getPermissions(@Header(AUTHORIZATION) token: String): Response<List<PermissionEntity>>

    /**
     * Creates a new permission.
     * @param token The authorization token.
     * @param permission The permission entity to create.
     */
    @POST(PERMISSIONS)
    suspend fun createPermission(@Header(AUTHORIZATION) token: String, @Body permission: PermissionEntity): Response<PermissionEntity>

    /**
     * Updates an existing permission.
     * @param token The authorization token.
     * @param id The ID of the permission to update.
     * @param permission The updated permission entity.
     */
    @PUT(PERMISSION_BY_ID)
    suspend fun updatePermission(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body permission: PermissionEntity): Response<PermissionEntity>

    /**
     * Deletes a permission.
     * @param token The authorization token.
     * @param id The ID of the permission to delete.
     */
    @DELETE(PERMISSION_BY_ID)
    suspend fun deletePermission(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // User Permissions Endpoints
    /**
     * Retrieves a list of all user permissions.
     * @param token The authorization token.
     */
    @GET(USER_PERMISSIONS)
    suspend fun getUserPermissions(@Header(AUTHORIZATION) token: String): Response<List<UserPermissionEntity>>

    /**
     * Creates a new user permission mapping.
     * @param token The authorization token.
     * @param userPermission The user permission entity to create.
     */
    @POST(USER_PERMISSIONS)
    suspend fun createUserPermission(@Header(AUTHORIZATION) token: String, @Body userPermission: UserPermissionEntity): Response<UserPermissionEntity>

    /**
     * Deletes a user permission mapping.
     * @param token The authorization token.
     * @param id The ID of the user permission mapping to delete.
     */
    @DELETE(USER_PERMISSION_BY_ID)
    suspend fun deleteUserPermission(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // System Settings Endpoints
    /**
     * Retrieves all system settings.
     * @param token The authorization token.
     */
    @GET(SYSTEM_SETTINGS)
    suspend fun getSystemSettings(@Header(AUTHORIZATION) token: String): Response<List<SystemSettingEntity>>

    /**
     * Creates a new system setting.
     * @param token The authorization token.
     * @param setting The system setting entity to create.
     */
    @POST(SYSTEM_SETTINGS)
    suspend fun createSystemSetting(@Header(AUTHORIZATION) token: String, @Body setting: SystemSettingEntity): Response<SystemSettingEntity>

    /**
     * Updates an existing system setting.
     * @param token The authorization token.
     * @param id The ID of the system setting to update.
     * @param setting The updated system setting entity.
     */
    @PUT(SYSTEM_SETTING_BY_ID)
    suspend fun updateSystemSetting(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body setting: SystemSettingEntity): Response<SystemSettingEntity>

    /**
     * Deletes a system setting.
     * @param token The authorization token.
     * @param id The ID of the system setting to delete.
     */
    @DELETE(SYSTEM_SETTING_BY_ID)
    suspend fun deleteSystemSetting(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Documents Endpoints
    /**
     * Retrieves all documents.
     * @param token The authorization token.
     */
    @GET(DOCUMENTS)
    suspend fun getAllDocuments(@Header(AUTHORIZATION) token: String): Response<List<DocumentEntity>>

    /**
     * Creates a new document record.
     * @param token The authorization token.
     * @param document The document entity to create.
     */
    @POST(DOCUMENTS)
    suspend fun createDocument(@Header(AUTHORIZATION) token: String, @Body document: DocumentEntity): Response<DocumentEntity>

    /**
     * Updates an existing document record.
     * @param token The authorization token.
     * @param id The ID of the document to update.
     * @param document The updated document entity.
     */
    @PUT(DOCUMENT_BY_ID)
    suspend fun updateDocument(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body document: DocumentEntity): Response<DocumentEntity>

    /**
     * Deletes a document record.
     * @param token The authorization token.
     * @param id The ID of the document to delete.
     */
    @DELETE(DOCUMENT_BY_ID)
    suspend fun deleteDocument(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Education Records Endpoints
    /**
     * Retrieves all education records.
     * @param token The authorization token.
     */
    @GET(EDUCATION_RECORDS)
    suspend fun getAllEducationRecords(@Header(AUTHORIZATION) token: String): Response<List<EducationRecordEntity>>

    /**
     * Creates a new education record.
     * @param token The authorization token.
     * @param record The education record entity to create.
     */
    @POST(EDUCATION_RECORDS)
    suspend fun createEducationRecord(@Header(AUTHORIZATION) token: String, @Body record: EducationRecordEntity): Response<EducationRecordEntity>

    /**
     * Updates an existing education record.
     * @param token The authorization token.
     * @param id The ID of the education record to update.
     * @param record The updated education record entity.
     */
    @PUT(EDUCATION_RECORD_BY_ID)
    suspend fun updateEducationRecord(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body record: EducationRecordEntity): Response<EducationRecordEntity>

    /**
     * Deletes an education record.
     * @param token The authorization token.
     * @param id The ID of the education record to delete.
     */
    @DELETE(EDUCATION_RECORD_BY_ID)
    suspend fun deleteEducationRecord(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Medical Records Endpoints
    /**
     * Retrieves all medical records.
     * @param token The authorization token.
     */
    @GET(MEDICAL_RECORDS)
    suspend fun getAllMedicalRecords(@Header(AUTHORIZATION) token: String): Response<List<MedicalRecordEntity>>

    /**
     * Creates a new medical record.
     * @param token The authorization token.
     * @param record The medical record entity to create.
     */
    @POST(MEDICAL_RECORDS)
    suspend fun createMedicalRecord(@Header(AUTHORIZATION) token: String, @Body record: MedicalRecordEntity): Response<MedicalRecordEntity>

    /**
     * Updates an existing medical record.
     * @param token The authorization token.
     * @param id The ID of the medical record to update.
     * @param record The updated medical record entity.
     */
    @PUT(MEDICAL_RECORD_BY_ID)
    suspend fun updateMedicalRecord(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body record: MedicalRecordEntity): Response<MedicalRecordEntity>

    /**
     * Deletes a medical record.
     * @param token The authorization token.
     * @param id The ID of the medical record to delete.
     */
    @DELETE(MEDICAL_RECORD_BY_ID)
    suspend fun deleteMedicalRecord(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Money Records Endpoints
    /**
     * Retrieves all money records.
     * @param token The authorization token.
     */
    @GET(MONEY_RECORDS)
    suspend fun getAllMoneyRecords(@Header(AUTHORIZATION) token: String): Response<List<MoneyRecordEntity>>

    /**
     * Creates a new money record.
     * @param token The authorization token.
     * @param record The money record entity to create.
     */
    @POST(MONEY_RECORDS)
    suspend fun createMoneyRecord(@Header(AUTHORIZATION) token: String, @Body record: MoneyRecordEntity): Response<MoneyRecordEntity>

    /**
     * Updates an existing money record.
     * @param token The authorization token.
     * @param id The ID of the money record to update.
     * @param record The updated money record entity.
     */
    @PUT(MONEY_RECORD_BY_ID)
    suspend fun updateMoneyRecord(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body record: MoneyRecordEntity): Response<MoneyRecordEntity>

    /**
     * Deletes a money record.
     * @param token The authorization token.
     * @param id The ID of the money record to delete.
     */
    @DELETE(MONEY_RECORD_BY_ID)
    suspend fun deleteMoneyRecord(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Home Studies Endpoints
    /**
     * Retrieves all home studies.
     * @param token The authorization token.
     */
    @GET(HOME_STUDIES)
    suspend fun getAllHomeStudies(@Header(AUTHORIZATION) token: String): Response<List<HomeStudyEntity>>

    /**
     * Creates a new home study.
     * @param token The authorization token.
     * @param study The home study entity to create.
     */
    @POST(HOME_STUDIES)
    suspend fun createHomeStudy(@Header(AUTHORIZATION) token: String, @Body study: HomeStudyEntity): Response<HomeStudyEntity>

    /**
     * Updates an existing home study.
     * @param token The authorization token.
     * @param id The ID of the home study to update.
     * @param study The updated home study entity.
     */
    @PUT(HOME_STUDY_BY_ID)
    suspend fun updateHomeStudy(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int, @Body study: HomeStudyEntity): Response<HomeStudyEntity>

    /**
     * Deletes a home study.
     * @param token The authorization token.
     * @param id The ID of the home study to delete.
     */
    @DELETE(HOME_STUDY_BY_ID)
    suspend fun deleteHomeStudy(@Header(AUTHORIZATION) token: String, @Path(ID) id: Int): Response<Unit>

    // Analytics Endpoints
    /**
     * Retrieves the analytics summary.
     * @param token The authorization token.
     */
    @GET(ANALYTICS_SUMMARY)
    suspend fun getAnalyticsSummary(@Header(AUTHORIZATION) token: String): Response<AnalyticsSummary>

    /**
     * Authenticates a user.
     * @param request The login request.
     */
    @POST(LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /**
     * Registers a new user.
     * @param request The registration request.
     */
    @POST(REGISTER)
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    /**
     * Retrieves all notifications for the user.
     * @param token The authorization token.
     */
    @GET(NOTIFICATIONS)
    suspend fun getNotifications(@Header(AUTHORIZATION) token: String): Response<List<NotificationEntity>>
    
    /**
     * Retrieves the count of unread notifications.
     * @param token The authorization token.
     */
    @GET(UNREAD_NOTIFICATION_COUNT)
    suspend fun getUnreadNotificationCount(@Header(AUTHORIZATION) token: String): Response<UnreadCountResponse>
    
    /**
     * Marks a notification as read.
     * @param token The authorization token.
     * @param notificationId The ID of the notification.
     */
    @POST(MARK_NOTIFICATION_READ)
    suspend fun markNotificationAsRead(@Header(AUTHORIZATION) token: String, @Path(ID) notificationId: Int): Response<Unit>

    /**
     * Updates the FCM token for push notifications.
     * @param authToken The authorization token.
     * @param request The FCM token request.
     */
    @POST(UPDATE_FCM_TOKEN)
    suspend fun updateFcmToken(
        @Header(AUTHORIZATION) authToken: String,
        @Body request: FcmTokenRequest
    ): Response<Unit>

    // Sync Endpoints
    /**
     * Pushes local changes to the server for synchronization.
     * @param token The authorization token.
     * @param syncItems The list of items to push.
     */
    @POST(SYNC_PUSH)
    suspend fun pushSync(
        @Header(AUTHORIZATION) token: String,
        @Body syncItems: List<SyncPushRequestItem>
    ): Response<SyncPushResponse>

    /**
     * Pulls changes from the server for synchronization.
     * @param token The authorization token.
     * @param since The timestamp from which to pull changes.
     */
    @GET(SYNC_PULL)
    suspend fun pullSync(
        @Header(AUTHORIZATION) token: String,
        @Query(SINCE) since: Long
    ): Response<SyncPullResponse>
}

/**
 * Request item for pushing sync data.
 * @property tableName The name of the table being synced.
 * @property operation The operation being performed.
 * @property recordId The ID of the record being synced.
 * @property payload The JSON payload of the record.
 */
data class SyncPushRequestItem(
    @SerializedName(TABLE_NAME) val tableName: String,
    @SerializedName(OPERATION) val operation: String,
    @SerializedName(RECORD_ID) val recordId: String,
    @SerializedName(PAYLOAD) val payload: String
)

/**
 * Response from a sync push operation.
 * @property success Whether the operation was successful.
 * @property applied The number of items applied on the server.
 * @property errors A list of error messages, if any.
 */
data class SyncPushResponse(
    val success: Boolean,
    val applied: Int,
    val errors: List<String>
)

/**
 * Response from a sync pull operation containing all updated entities.
 * @property children List of child entities.
 * @property families List of family entities.
 * @property placements List of placement entities.
 * @property medicalRecords List of medical record entities.
 * @property educationRecords List of education record entities.
 * @property moneyRecords List of money record entities.
 * @property documents List of document entities.
 * @property caseReports List of case report entities.
 * @property courtCases List of court case entities.
 * @property guardians List of guardian entities.
 * @property adoptionApplications List of adoption application entities.
 * @property homeStudies List of home study entities.
 * @property auditLogs List of audit log entities.
 * @property notifications List of notification entities.
 * @property backgroundChecks List of background check entities.
 * @property fosterTasks List of foster task entities.
 * @property fosterMatches List of foster match entities.
 * @property permissions List of permission entities.
 * @property userPermissions List of user permission entities.
 * @property systemSettings List of system setting entities.
 * @property users List of user entities.
 * @property sosLocations List of SOS location entities.
 * @property tasks List of task entities.
 * @property actionItems List of action item entities.
 * @property dashboardMetrics List of dashboard metric entities.
 * @property dashboardPreferences List of dashboard preference entities.
 * @property criticalDates List of critical date entities.
 * @property workerMessages List of worker message entities.
 * @property riskAssessments List of risk assessment entities.
 * @property permanencyPlans List of permanency plan entities.
 * @property caseload List of caseload entities.
 * @property caseUrgencyFlags List of case urgency flag entities.
 * @property caseActivities List of case activity entities.
 * @property caseDeadlines List of case deadline entities.
 * @property caseApprovals List of case approval entities.
 * @property placementCompatibility List of placement compatibility entities.
 * @property workloadTracking List of workload tracking entities.
 */
data class SyncPullResponse(
    val children: List<ChildEntity>,
    val families: List<FamilyEntity>,
    val placements: List<PlacementEntity>,
    @SerializedName(MEDICAL_RECORDS_JSON) val medicalRecords: List<MedicalRecordEntity>,
    @SerializedName(EDUCATION_RECORDS_JSON) val educationRecords: List<EducationRecordEntity>,
    @SerializedName(MONEY_RECORDS_JSON) val moneyRecords: List<MoneyRecordEntity>,
    val documents: List<DocumentEntity>,
    @SerializedName(CASE_REPORTS_JSON) val caseReports: List<CaseReportEntity>,
    @SerializedName(COURT_CASES_JSON) val courtCases: List<CourtCaseEntity>,
    val guardians: List<GuardianEntity>,
    @SerializedName(ADOPTION_APPLICATIONS_JSON) val adoptionApplications: List<AdoptionApplicationEntity>,
    @SerializedName(HOME_STUDIES_JSON) val homeStudies: List<HomeStudyEntity>,
    @SerializedName(AUDIT_LOGS_JSON) val auditLogs: List<AuditLogEntity>,
    val notifications: List<NotificationEntity>,
    @SerializedName(BACKGROUND_CHECKS_JSON) val backgroundChecks: List<BackgroundCheckEntity>,
    @SerializedName(FOSTER_TASKS_JSON) val fosterTasks: List<FosterTaskEntity>,
    @SerializedName(FOSTER_MATCHES_JSON) val fosterMatches: List<FosterMatchEntity>,
    val permissions: List<PermissionEntity>,
    @SerializedName(USER_PERMISSIONS_JSON) val userPermissions: List<UserPermissionEntity>,
    @SerializedName(SYSTEM_SETTINGS_JSON) val systemSettings: List<SystemSettingEntity>,
    val users: List<UserEntity>,
    @SerializedName(SOS_LOCATIONS_JSON) val sosLocations: List<SOSLocationEntity>,
    val tasks: List<TaskEntity>,
    @SerializedName(ACTION_ITEMS_JSON) val actionItems: List<ActionItemEntity>,
    @SerializedName(DASHBOARD_METRICS_JSON) val dashboardMetrics: List<DashboardMetricEntity>,
    @SerializedName(DASHBOARD_PREFERENCES_JSON) val dashboardPreferences: List<DashboardPreferenceEntity>,
    @SerializedName(CRITICAL_DATES_JSON) val criticalDates: List<CriticalDateEntity>,
    @SerializedName(WORKER_MESSAGES_JSON) val workerMessages: List<WorkerMessageEntity>,
    @SerializedName(RISK_ASSESSMENTS_JSON) val riskAssessments: List<RiskAssessmentEntity>,
    @SerializedName(PERMANENCY_PLANS_JSON) val permanencyPlans: List<PermanencyPlanEntity>,
    val caseload: List<CaseloadEntity>,
    @SerializedName(CASE_URGENCY_FLAGS_JSON) val caseUrgencyFlags: List<CaseUrgencyFlagEntity>,
    @SerializedName(CASE_ACTIVITIES_JSON) val caseActivities: List<CaseActivityEntity>,
    @SerializedName(CASE_DEADLINES_JSON) val caseDeadlines: List<CaseDeadlineEntity>,
    @SerializedName(CASE_APPROVALS_JSON) val caseApprovals: List<CaseApprovalEntity>,
    @SerializedName(PLACEMENT_COMPATIBILITY_JSON) val placementCompatibility: List<PlacementCompatibilityEntity>,
    @SerializedName(WORKLOAD_TRACKING_JSON) val workloadTracking: List<WorkloadTrackingEntity>
)

/**
 * Response for the unread notification count.
 * @property unread The number of unread notifications.
 */
data class UnreadCountResponse(val unread: Int)

/**
 * Request to update the FCM token.
 * @property fcmToken The new FCM token.
 * @property deviceId The unique ID of the device.
 * @property platform The platform of the device (default is "android").
 */
data class FcmTokenRequest(
    val fcmToken: String,
    val deviceId: String,
    val platform: String = PLATFORM_ANDROID
)

/**
 * Request for user login.
 * @property email The user's email address.
 * @property password The user's password.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Request for user registration.
 * @property username The desired username.
 * @property email The user's email address.
 * @property password The user's password.
 * @property phone The user's phone number.
 * @property idNumber The user's ID number.
 * @property nationalIdNo The user's national ID number (optional).
 * @property role The user's role in the system.
 * @property county The user's county (optional).
 * @property subCounty The user's sub-county (optional).
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    @SerializedName(ID_NUMBER) val idNumber: String,
    @SerializedName(NATIONAL_ID_NO) val nationalIdNo: String? = null,
    val role: String,
    val county: String? = null,
    @SerializedName(SUB_COUNTY) val subCounty: String? = null
)

/**
 * Response for authentication requests.
 * @property success Whether the authentication was successful.
 * @property token The JWT token, if successful.
 * @property user The user entity, if successful.
 * @property error Error details, if unsuccessful.
 */
data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val user: UserEntity?,
    val error: ErrorDetail?
)

/**
 * Error detail information.
 * @property code The error code.
 * @property message The error message.
 */
data class ErrorDetail(
    val code: String,
    val message: String
)

/**
 * Summary of analytics data.
 * @property totalChildren Total number of children in the system.
 * @property totalFamilies Total number of families in the system.
 * @property totalUsers Total number of users in the system.
 * @property activePlacements Number of active placements.
 * @property pendingApplications Number of pending adoption applications.
 * @property pendingBackgroundChecks Number of pending background checks.
 */
data class AnalyticsSummary(
    val totalChildren: Int,
    val totalFamilies: Int,
    val totalUsers: Int,
    val activePlacements: Int,
    val pendingApplications: Int,
    val pendingBackgroundChecks: Int
)
