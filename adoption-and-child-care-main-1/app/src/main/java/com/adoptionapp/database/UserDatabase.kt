package com.adoptionapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adoptionapp.entity.User
import com.adoptionapp.entity.ChildrenEntity
import com.adoptionapp.entity.BackgroundChecksEntity
import com.adoptionapp.entity.DocumentsEntity
import com.adoptionapp.entity.FamilyProfilesEntity
import com.adoptionapp.entity.FosterTasksEntity
import com.adoptionapp.entity.PlacementsEntity
import com.adoptionapp.entity.AuditLogEntity
import com.adoptionapp.entity.CaseReportEntity
import com.adoptionapp.entity.CourtCaseEntity
import com.adoptionapp.entity.EducationRecordEntity
import com.adoptionapp.entity.GuardianEntity
import com.adoptionapp.entity.MedicalRecordEntity
import com.adoptionapp.entity.MoneyRecordEntity
import com.adoptionapp.entity.PermissionEntity
import com.adoptionapp.entity.UserPermissionEntity
import com.adoptionapp.dao.UserDao

@Database(
    entities = [
        User::class,
        ChildrenEntity::class,
        BackgroundChecksEntity::class,
        DocumentsEntity::class,
        FamilyProfilesEntity::class,
        FosterTasksEntity::class,
        PlacementsEntity::class,
        AuditLogEntity::class,
        CaseReportEntity::class,
        CourtCaseEntity::class,
        EducationRecordEntity::class,
        GuardianEntity::class,
        MedicalRecordEntity::class,
        MoneyRecordEntity::class,
        PermissionEntity::class,
        UserPermissionEntity::class
    ],
    version = 2
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // TODO: Add DAOs for other entities as needed
}
