package com.example.adoption_and_childcare.network

import android.content.Context
import com.example.adoption_and_childcare.data.session.AppSettings
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
        
        // Auto-map localhost/emulator to 10.0.2.2 for convenience
        if (baseUrl.contains("localhost") || baseUrl.contains("127.0.0.1")) {
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
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }

    /**
     * Static access for standard API service (Legacy support).
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:50000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
