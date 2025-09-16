package com.yourdomain.adoptionchildcare

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    
    // Dynamic base URL selection supporting emulator, physical device, and Gradle override
    private val resolvedBaseUrl: String by lazy {
        val override = BuildConfig.BASE_URL_OVERRIDE?.trim()
        if (!override.isNullOrEmpty()) {
            if (override.endsWith("/")) override else "$override/"
        } else {
            // Heuristic: emulator default host, else localhost (for adb reverse)
            val isEmulator = android.os.Build.FINGERPRINT.contains("generic") ||
                android.os.Build.FINGERPRINT.lowercase().contains("emulator") ||
                android.os.Build.MODEL.contains("Emulator") ||
                android.os.Build.MODEL.contains("Android SDK built for x86")
            val host = if (isEmulator) "http://10.0.2.2:50000/" else "http://localhost:50000/"
            host
        }
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(resolvedBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
