package com.adoptionapp.viewmodel

import com.adoptionapp.ApiService
import com.adoptionapp.RetrofitClient
import com.adoptionapp.AnalyticsSummaryResponse
import com.adoptionapp.PlacementOverTimeResponse
import android.content.Context
import com.adoptionapp.TokenManager

class AnalyticsRepository(private val context: Context) {
    private val api = RetrofitClient.apiService
    private val token: String get() = "Bearer " + (TokenManager.getToken(context) ?: "")
    suspend fun getSummary(): String {
        val response = api.getAnalyticsSummary(token)
        val data = response.body()
        return if (data != null) "Users: ${data.users}, Children: ${data.children}, Placements: ${data.placements}" else "No data"
    }
    suspend fun getPlacementsOverTime(): List<String> {
        val response = api.getPlacementsOverTime(token)
        return response.body()?.map { "${it.month}: ${it.count}" } ?: emptyList()
    }
    suspend fun getChildrenByStatus(): Map<String, Int> {
        val response = api.getChildrenByStatus(token)
        return response.body() ?: emptyMap()
    }
} 