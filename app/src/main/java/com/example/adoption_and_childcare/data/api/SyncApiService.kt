package com.example.adoption_and_childcare.data.api

import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SyncApiService {
    @POST("api/sync/children/batch")
    suspend fun syncChildrenBatch(@Body children: List<ChildEntity>): Response<SyncResponse>

    @POST("api/sync/families/batch")
    suspend fun syncFamiliesBatch(@Body families: List<FamilyEntity>): Response<SyncResponse>

    @POST("api/sync/applications/batch")
    suspend fun syncApplicationsBatch(@Body applications: List<AdoptionApplicationEntity>): Response<SyncResponse>
}

data class SyncResponse(
    val success: Boolean,
    val synced_local_ids: List<Int>,
    val remote_ids: Map<Int, String>? = null,
    val errors: List<SyncError>? = null
)

data class SyncError(
    val id: String,
    val message: String
)
