import retrofit2.http.*

interface ApiService {
    @GET("/children")
    suspend fun getChildren(@Header("Authorization") token: String): List<ChildrenEntity>

    @GET("/children/{id}")
    suspend fun getChildById(@Header("Authorization") token: String, @Path("id") id: Int): ChildrenEntity

    @POST("/children")
    suspend fun createChild(@Header("Authorization") token: String, @Body child: ChildrenEntity): ChildrenEntity

    @PUT("/children/{id}")
    suspend fun updateChild(@Header("Authorization") token: String, @Path("id") id: Int, @Body child: ChildrenEntity): ChildrenEntity

    @DELETE("/children/{id}")
    suspend fun deleteChild(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/users")
    suspend fun getUsers(@Header("Authorization") token: String): List<UsersEntity>

    @GET("/users/{id}")
    suspend fun getUserById(@Header("Authorization") token: String, @Path("id") id: Int): UsersEntity

    @POST("/users")
    suspend fun createUser(@Header("Authorization") token: String, @Body user: UsersEntity): UsersEntity

    @PUT("/users/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body user: UsersEntity): UsersEntity

    @DELETE("/users/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/guardians")
    suspend fun getGuardians(@Header("Authorization") token: String): List<GuardiansEntity>

    @GET("/guardians/{id}")
    suspend fun getGuardianById(@Header("Authorization") token: String, @Path("id") id: Int): GuardiansEntity

    @POST("/guardians")
    suspend fun createGuardian(@Header("Authorization") token: String, @Body guardian: GuardiansEntity): GuardiansEntity

    @PUT("/guardians/{id}")
    suspend fun updateGuardian(@Header("Authorization") token: String, @Path("id") id: Int, @Body guardian: GuardiansEntity): GuardiansEntity

    @DELETE("/guardians/{id}")
    suspend fun deleteGuardian(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/court_cases")
    suspend fun getCourtCases(@Header("Authorization") token: String): List<CourtCasesEntity>

    @GET("/court_cases/{id}")
    suspend fun getCourtCaseById(@Header("Authorization") token: String, @Path("id") id: Int): CourtCasesEntity

    @POST("/court_cases")
    suspend fun createCourtCase(@Header("Authorization") token: String, @Body case: CourtCasesEntity): CourtCasesEntity

    @PUT("/court_cases/{id}")
    suspend fun updateCourtCase(@Header("Authorization") token: String, @Path("id") id: Int, @Body case: CourtCasesEntity): CourtCasesEntity

    @DELETE("/court_cases/{id}")
    suspend fun deleteCourtCase(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/placements")
    suspend fun getPlacements(@Header("Authorization") token: String): List<PlacementsEntity>

    @GET("/placements/{id}")
    suspend fun getPlacementById(@Header("Authorization") token: String, @Path("id") id: Int): PlacementsEntity

    @POST("/placements")
    suspend fun createPlacement(@Header("Authorization") token: String, @Body placement: PlacementsEntity): PlacementsEntity

    @PUT("/placements/{id}")
    suspend fun updatePlacement(@Header("Authorization") token: String, @Path("id") id: Int, @Body placement: PlacementsEntity): PlacementsEntity

    @DELETE("/placements/{id}")
    suspend fun deletePlacement(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/medical_records")
    suspend fun getMedicalRecords(@Header("Authorization") token: String): List<MedicalRecordsEntity>

    @GET("/medical_records/{id}")
    suspend fun getMedicalRecordById(@Header("Authorization") token: String, @Path("id") id: Int): MedicalRecordsEntity

    @POST("/medical_records")
    suspend fun createMedicalRecord(@Header("Authorization") token: String, @Body record: MedicalRecordsEntity): MedicalRecordsEntity

    @PUT("/medical_records/{id}")
    suspend fun updateMedicalRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: MedicalRecordsEntity): MedicalRecordsEntity

    @DELETE("/medical_records/{id}")
    suspend fun deleteMedicalRecord(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/case_reports")
    suspend fun getCaseReports(@Header("Authorization") token: String): List<CaseReportsEntity>

    @GET("/case_reports/{id}")
    suspend fun getCaseReportById(@Header("Authorization") token: String, @Path("id") id: Int): CaseReportsEntity

    @POST("/case_reports")
    suspend fun createCaseReport(@Header("Authorization") token: String, @Body report: CaseReportsEntity): CaseReportsEntity

    @PUT("/case_reports/{id}")
    suspend fun updateCaseReport(@Header("Authorization") token: String, @Path("id") id: Int, @Body report: CaseReportsEntity): CaseReportsEntity

    @DELETE("/case_reports/{id}")
    suspend fun deleteCaseReport(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/money_records")
    suspend fun getMoneyRecords(@Header("Authorization") token: String): List<MoneyRecordsEntity>

    @GET("/money_records/{id}")
    suspend fun getMoneyRecordById(@Header("Authorization") token: String, @Path("id") id: Int): MoneyRecordsEntity

    @POST("/money_records")
    suspend fun createMoneyRecord(@Header("Authorization") token: String, @Body record: MoneyRecordsEntity): MoneyRecordsEntity

    @PUT("/money_records/{id}")
    suspend fun updateMoneyRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: MoneyRecordsEntity): MoneyRecordsEntity

    @DELETE("/money_records/{id}")
    suspend fun deleteMoneyRecord(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/education_records")
    suspend fun getEducationRecords(@Header("Authorization") token: String): List<EducationRecordsEntity>

    @GET("/education_records/{id}")
    suspend fun getEducationRecordById(@Header("Authorization") token: String, @Path("id") id: Int): EducationRecordsEntity

    @POST("/education_records")
    suspend fun createEducationRecord(@Header("Authorization") token: String, @Body record: EducationRecordsEntity): EducationRecordsEntity

    @PUT("/education_records/{id}")
    suspend fun updateEducationRecord(@Header("Authorization") token: String, @Path("id") id: Int, @Body record: EducationRecordsEntity): EducationRecordsEntity

    @DELETE("/education_records/{id}")
    suspend fun deleteEducationRecord(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/documents")
    suspend fun getDocuments(@Header("Authorization") token: String): List<DocumentsEntity>

    @GET("/documents/{id}")
    suspend fun getDocumentById(@Header("Authorization") token: String, @Path("id") id: Int): DocumentsEntity

    @POST("/documents")
    suspend fun createDocument(@Header("Authorization") token: String, @Body document: DocumentsEntity): DocumentsEntity

    @PUT("/documents/{id}")
    suspend fun updateDocument(@Header("Authorization") token: String, @Path("id") id: Int, @Body document: DocumentsEntity): DocumentsEntity

    @DELETE("/documents/{id}")
    suspend fun deleteDocument(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/audit_logs")
    suspend fun getAuditLogs(@Header("Authorization") token: String): List<AuditLogsEntity>

    @GET("/audit_logs/{id}")
    suspend fun getAuditLogById(@Header("Authorization") token: String, @Path("id") id: Int): AuditLogsEntity

    @POST("/audit_logs")
    suspend fun createAuditLog(@Header("Authorization") token: String, @Body log: AuditLogsEntity): AuditLogsEntity

    @PUT("/audit_logs/{id}")
    suspend fun updateAuditLog(@Header("Authorization") token: String, @Path("id") id: Int, @Body log: AuditLogsEntity): AuditLogsEntity

    @DELETE("/audit_logs/{id}")
    suspend fun deleteAuditLog(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/permissions")
    suspend fun getPermissions(@Header("Authorization") token: String): List<PermissionsEntity>

    @GET("/permissions/{id}")
    suspend fun getPermissionById(@Header("Authorization") token: String, @Path("id") id: Int): PermissionsEntity

    @POST("/permissions")
    suspend fun createPermission(@Header("Authorization") token: String, @Body permission: PermissionsEntity): PermissionsEntity

    @PUT("/permissions/{id}")
    suspend fun updatePermission(@Header("Authorization") token: String, @Path("id") id: Int, @Body permission: PermissionsEntity): PermissionsEntity

    @DELETE("/permissions/{id}")
    suspend fun deletePermission(@Header("Authorization") token: String, @Path("id") id: Int)

    @GET("/user_permissions")
    suspend fun getUserPermissions(@Header("Authorization") token: String): List<UserPermissionsEntity>

    @GET("/user_permissions/{user_id}/{permission_id}")
    suspend fun getUserPermissionById(@Header("Authorization") token: String, @Path("user_id") userId: Int, @Path("permission_id") permissionId: Int): UserPermissionsEntity

    @POST("/user_permissions")
    suspend fun createUserPermission(@Header("Authorization") token: String, @Body userPermission: UserPermissionsEntity): UserPermissionsEntity

    @PUT("/user_permissions/{user_id}/{permission_id}")
    suspend fun updateUserPermission(@Header("Authorization") token: String, @Path("user_id") userId: Int, @Path("permission_id") permissionId: Int, @Body userPermission: UserPermissionsEntity): UserPermissionsEntity

    @DELETE("/user_permissions/{user_id}/{permission_id}")
    suspend fun deleteUserPermission(@Header("Authorization") token: String, @Path("user_id") userId: Int, @Path("permission_id") permissionId: Int)
} 