package com.yourdomain.adoptionchildcare.data

import android.content.Context

object DatabaseProvider {
    @Volatile private var db: AppDatabase? = null

    fun get(context: Context): AppDatabase = db ?: synchronized(this) {
        db ?: AppDatabase.getInstance(context).also { db = it }
    }
}
