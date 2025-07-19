import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChildrenEntity::class, UsersEntity::class, GuardiansEntity::class, CourtCasesEntity::class, PlacementsEntity::class, MedicalRecordsEntity::class, CaseReportsEntity::class, MoneyRecordsEntity::class, EducationRecordsEntity::class, DocumentsEntity::class, AuditLogsEntity::class, PermissionsEntity::class, UserPermissionsEntity::class, FamilyProfileEntity::class, FosterTasksEntity::class, FosterMatchesEntity::class, BackgroundChecksEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun childrenDao(): ChildrenDao
    abstract fun usersDao(): UsersDao
    abstract fun guardiansDao(): GuardiansDao
    abstract fun courtCasesDao(): CourtCasesDao
    abstract fun placementsDao(): PlacementsDao
    abstract fun medicalRecordsDao(): MedicalRecordsDao
    abstract fun caseReportsDao(): CaseReportsDao
    abstract fun moneyRecordsDao(): MoneyRecordsDao
    abstract fun educationRecordsDao(): EducationRecordsDao
    abstract fun documentsDao(): DocumentsDao
    abstract fun auditLogsDao(): AuditLogsDao
    abstract fun permissionsDao(): PermissionsDao
    abstract fun userPermissionsDao(): UserPermissionsDao
    abstract fun familyProfileDao(): FamilyProfileDao
    abstract fun fosterTasksDao(): FosterTasksDao
    abstract fun fosterMatchesDao(): FosterMatchesDao
    abstract fun backgroundChecksDao(): BackgroundChecksDao
} 