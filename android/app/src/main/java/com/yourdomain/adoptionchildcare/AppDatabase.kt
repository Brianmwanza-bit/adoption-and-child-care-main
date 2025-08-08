import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ChildrenEntity::class, UsersEntity::class, GuardiansEntity::class, CourtCasesEntity::class, PlacementsEntity::class, MedicalRecordsEntity::class, CaseReportsEntity::class, MoneyRecordsEntity::class, EducationRecordsEntity::class, DocumentsEntity::class, AuditLogsEntity::class, PermissionsEntity::class, UserPermissionsEntity::class, FamilyProfileEntity::class, FosterTasksEntity::class, FosterMatchesEntity::class, BackgroundChecksEntity::class], version = 3)
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

    companion object {
        // Migration from version 2 to 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add any new columns or tables here if needed
                // For now, just update version for compatibility
            }
        }
    }
} 