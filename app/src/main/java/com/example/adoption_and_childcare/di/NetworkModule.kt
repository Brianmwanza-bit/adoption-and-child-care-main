package com.example.adoption_and_childcare.di

import android.content.Context
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a singleton instance of [ApiService].
     * 
     * @param context Application context for retrieving settings.
     * @return The configured API service.
     */
    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): ApiService = 
        RetrofitClient.getDynamicApiService(context)
}
