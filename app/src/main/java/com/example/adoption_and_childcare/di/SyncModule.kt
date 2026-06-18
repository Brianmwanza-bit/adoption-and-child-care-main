package com.example.adoption_and_childcare.di

import android.content.Context
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.data.sync.SyncManager
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        syncQueueDao: SyncQueueDao,
        @ApplicationContext context: Context,
        sessionManager: SessionManager
    ): SyncManager {
        return SyncManager(syncQueueDao, context, sessionManager)
    }
}
