package com.adoptionapp.ui.compose

class MapRepository(private val api: ApiService, private val tokenProvider: () -> String) {
    suspend fun getFamilyLocations() = api.getFamilyLocations("Bearer ${tokenProvider()}")
    suspend fun getUserLocations() = api.getUserLocations("Bearer ${tokenProvider()}")
} 