package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.DocumentDao
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for document data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // Local database operations
    fun observeAll(): Flow<List<DocumentEntity>> = documentDao.observeAll()
    
    fun observeByChild(childId: Int): Flow<List<DocumentEntity>> = documentDao.observeByChild(childId)
    
    suspend fun insert(doc: DocumentEntity, token: String): Result<Long> {
        return try {
            // Insert locally first
            val localId = documentDao.insert(doc)
            
            // Sync to API in background
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createDocument(authHeader, doc)
                    if (!response.isSuccessful) {
                        println("Failed to sync document with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for document insert: ${e.message}")
            }
            
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(doc: DocumentEntity, token: String): Result<Unit> {
        return try {
            documentDao.update(doc)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateDocument(authHeader, doc.documentId, doc)
                    if (!response.isSuccessful) {
                        println("Failed to sync document update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for document update: ${e.message}")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            documentDao.deleteById(id)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteDocument(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for document delete: ${e.message}")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): DocumentEntity? {
        return documentDao.findById(id)
    }
    
    suspend fun count(): Int = documentDao.getAll().size
    
    // API operations
    suspend fun fetchFromApi(token: String): Result<List<DocumentEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllDocuments(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val apiDocs = response.body()!!
                // Optionally update local DB with API data
                // for (doc in apiDocs) documentDao.insert(doc)
                Result.success(apiDocs)
            } else {
                Result.failure(Exception("Failed to fetch documents: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun insertWithSync(doc: DocumentEntity, token: String): Result<Long> {
        return insert(doc, token)
    }
}
