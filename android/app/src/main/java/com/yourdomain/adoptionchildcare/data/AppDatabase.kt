package com.yourdomain.adoptionchildcare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yourdomain.adoptionchildcare.data.dao.UserDao
import com.yourdomain.adoptionchildcare.data.entities.ChildEntity
import com.yourdomain.adoptionchildcare.data.entities.UserEntity

@Database(entities = [UserEntity::class, ChildEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adoption_childcare.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
