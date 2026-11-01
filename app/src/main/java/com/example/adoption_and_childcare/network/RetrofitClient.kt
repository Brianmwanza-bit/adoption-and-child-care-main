package com.example.adoption_and_childcare.network

import android.content.Context
import com.example.adoption_and_childcare.data.session.AppSettings
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton client for managing Retrofit instances and API services.
 * Dynamically uses the base URL defined in AppSettings.
 */
object RetrofitClient {
    private const val EMULATOR_HOST = "10.0.2.2"
    
    private var retrofit: Retrofit? = null
    private var currentUrl: String? = null

    /**
     * Returns the API Service instance, initializing Retrofit if necessary.
     * 
     * @param context Application context to retrieve settings.
     */
    fun getDynamicApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }

    private fun getRetrofit(context: Context): Retrofit {
        val settings = AppSettings(context)
        var baseUrl = settings.apiBaseUrl.trim()
        
        val isEmulator = android.os.Build.FINGERPRINT.startsWith("generic") ||
                         android.os.Build.MODEL.contains("google_sdk") ||
                         android.os.Build.MODEL.contains("Emulator") ||
                         android.os.Build.DEVICE.startsWith("generic") ||
                         android.os.Build.PRODUCT.contains("sdk")

        // Auto-map localhost, 127.0.0.1, or local network IPs to 10.0.2.2 when running on emulator
        if (isEmulator && (baseUrl.contains("localhost") || baseUrl.contains("127.0.0.1") || baseUrl.contains("192.168."))) {
            baseUrl = baseUrl.replace(Regex("https?://[^/:]+"), "http://$EMULATOR_HOST")
        } else if (baseUrl.contains("localhost") || baseUrl.contains("127.0.0.1")) {
            baseUrl = baseUrl.replace("localhost", EMULATOR_HOST).replace("127.0.0.1", EMULATOR_HOST)
        }
        
        // Ensure protocol is present, default to http if missing
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "http://$baseUrl"
        }
        
        if (!baseUrl.endsWith("/")) {
            baseUrl = "$baseUrl/"
        }

        synchronized(this) {
            if (retrofit == null || currentUrl != baseUrl) {
                currentUrl = baseUrl
                val gson = GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
                
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
    }

    /**
     * Resolves photo data (URL, Base64 data URI, content URI, or relative path) into a Coil-compatible model.
     */
    fun getNormalizedPhotoModel(photoData: String?): Any {
        if (photoData.isNullOrBlank()) {
            return com.example.adoption_and_childcare.R.drawable.app_logo
        }
        val trimmed = photoData.trim()
        return when {
            trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) -> trimmed
            trimmed.startsWith("content://", ignoreCase = true) || trimmed.startsWith("file://", ignoreCase = true) -> trimmed
            trimmed.startsWith("data:", ignoreCase = true) -> {
                try {
                    val base64Part = trimmed.substringAfter("base64,", trimmed)
                    android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                } catch (_: Exception) { trimmed }
            }
            !trimmed.contains("/") && trimmed.length > 50 -> {
                try {
                    android.util.Base64.decode(trimmed, android.util.Base64.DEFAULT)
                } catch (_: Exception) { trimmed }
            }
            else -> trimmed
        }
    }
}
