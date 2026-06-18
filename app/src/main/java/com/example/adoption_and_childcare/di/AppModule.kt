package com.example.adoption_and_childcare.di

import android.content.Context
import com.example.adoption_and_childcare.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier annotation for application-level coroutine scope.
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

/**
 * Dagger Hilt module for providing application-level dependencies.
 * 
 * This module provides singleton-scoped dependencies such as SessionManager
 * that are used throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideAppSettings(@ApplicationContext context: Context): com.example.adoption_and_childcare.data.session.AppSettings {
        return com.example.adoption_and_childcare.data.session.AppSettings(context)
    }

    /**
     * Provides a singleton CoroutineScope bound to the application lifecycle.
     *
     * @return A [CoroutineScope] using [SupervisorJob] and [Dispatchers.Default].
     */
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
