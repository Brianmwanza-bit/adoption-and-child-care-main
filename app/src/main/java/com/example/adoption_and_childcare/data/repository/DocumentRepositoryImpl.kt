package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.DocumentDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for document data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // Local database operations
    fun observeAll(): Flow<List<DocumentEntity>> = documentDao.observeAll()
    
    fun observeByChild(childId: Int): Flow<List<DocumentEntity>> = documentDao.observeByChild(childId)
    
    suspend fun insert(doc: DocumentEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            documentDao.insertWithSync(doc, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createDocument(authHeader, doc)
                }
            } catch (e: Exception) {
                // Ignore
            }
            
            Result.success(doc.documentId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(doc: DocumentEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            documentDao.updateWithSync(doc, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateDocument(authHeader, doc.documentId, doc)
                }
            } catch (e: Exception) {
                // Ignore
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            documentDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteDocument(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
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
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val apiDocs = body
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
