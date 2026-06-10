package com.example.adoption_and_childcare.di

import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.apiService
}
