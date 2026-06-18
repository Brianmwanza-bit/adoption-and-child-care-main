package com.example.adoption_and_childcare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.adoption_and_childcare.data.db.dao.NotificationDao
import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity

/**
 * Minimal Room database for the application.
 * 
 * Provides access to basic entities like Users and Notifications.
 */
@Database(
    entities = [
        UserEntity::class,
        NotificationEntity::class,
        PermissionEntity::class,
        UserPermissionEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabaseMinimal : RoomDatabase() {
    /**
     * Returns the Data Access Object for users.
     */
    abstract fun userDao(): UserDao

    /**
     * Returns the Data Access Object for notifications.
     */
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabaseMinimal? = null

        /**
         * Returns the singleton instance of the database.
         * 
         * @param context The application context.
         */
        fun getInstance(context: Context): AppDatabaseMinimal = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabaseMinimal::class.java,
                "adoption_childcare_minimal.db"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { INSTANCE = it }
        }
    }
}
