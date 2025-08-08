package com.yourdomain.adoptionchildcare.repository

import com.yourdomain.adoptionchildcare.ChildrenDao
import com.yourdomain.adoptionchildcare.ChildrenEntity
import com.yourdomain.adoptionchildcare.RetrofitClient
import com.yourdomain.adoptionchildcare.ErrorHandler
import com.yourdomain.adoptionchildcare.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for child management operations
 * Implements repository pattern with local database and remote API
 */
class ChildRepository(
    private val childDao: ChildrenDao,
    private val apiService: com.yourdomain.adoptionchildcare.ApiService = RetrofitClient.apiService
) {
    
    /**
     * Get all children from local database
     */
    suspend fun getAllChildren(): List<ChildrenEntity> = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("SELECT", "children")
            childDao.getAllChildren()
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to get all children from database")
            emptyList()
        }
    }

    /**
     * Get child by ID from local database
     */
    suspend fun getChildById(id: Int): ChildrenEntity? = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("SELECT", "children", id.toString())
            childDao.getChildById(id)
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to get child by ID: $id")
            null
        }
    }

    /**
     * Insert child into local database
     */
    suspend fun insertChild(child: ChildrenEntity): Long = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("INSERT", "children", child.id.toString())
            childDao.insertChild(child)
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to insert child: ${child.id}")
            -1
        }
    }

    /**
     * Update child in local database
     */
    suspend fun updateChild(child: ChildrenEntity): Int = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("UPDATE", "children", child.id.toString())
            childDao.updateChild(child)
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to update child: ${child.id}")
            0
        }
    }

    /**
     * Delete child from local database
     */
    suspend fun deleteChild(child: ChildrenEntity): Int = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("DELETE", "children", child.id.toString())
            childDao.deleteChild(child)
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to delete child: ${child.id}")
            0
        }
    }

    /**
     * Sync children with remote API
     */
    suspend fun syncChildren(token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logSync("SYNC", "children", true)
            val children = apiService.getChildren("Bearer $token")
            children.forEach { child ->
                insertChild(child)
            }
            Logger.logSync("SYNC", "children", true, "Synced ${children.size} children")
            true
        } catch (e: Exception) {
            Logger.logSync("SYNC", "children", false, e.message)
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Create child via API
     */
    suspend fun createChildRemote(token: String, child: ChildrenEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logApiRequest("POST", "/children")
            val response = apiService.createChild("Bearer $token", child)
            if (response.isSuccessful) {
                insertChild(child)
                Logger.logApiResponse("/children", response.code(), 0)
                true
            } else {
                Logger.logApiResponse("/children", response.code(), 0)
                false
            }
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to create child remotely")
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Update child via API
     */
    suspend fun updateChildRemote(token: String, child: ChildrenEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logApiRequest("PUT", "/children/${child.id}")
            val response = apiService.updateChild("Bearer $token", child.id, child)
            if (response.isSuccessful) {
                updateChild(child)
                Logger.logApiResponse("/children/${child.id}", response.code(), 0)
                true
            } else {
                Logger.logApiResponse("/children/${child.id}", response.code(), 0)
                false
            }
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to update child remotely")
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Delete child via API
     */
    suspend fun deleteChildRemote(token: String, childId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logApiRequest("DELETE", "/children/$childId")
            val response = apiService.deleteChild("Bearer $token", childId)
            if (response.isSuccessful) {
                val child = getChildById(childId)
                child?.let { deleteChild(it) }
                Logger.logApiResponse("/children/$childId", response.code(), 0)
                true
            } else {
                Logger.logApiResponse("/children/$childId", response.code(), 0)
                false
            }
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to delete child remotely")
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Search children by name
     */
    suspend fun searchChildrenByName(name: String): List<ChildrenEntity> = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("SEARCH", "children", "name: $name")
            getAllChildren().filter { it.name.contains(name, ignoreCase = true) }
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to search children by name: $name")
            emptyList()
        }
    }

    /**
     * Get children by age range
     */
    suspend fun getChildrenByAgeRange(minAge: Int, maxAge: Int): List<ChildrenEntity> = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("FILTER", "children", "age: $minAge-$maxAge")
            getAllChildren().filter { it.age in minAge..maxAge }
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to get children by age range: $minAge-$maxAge")
            emptyList()
        }
    }

    /**
     * Get children by status
     */
    suspend fun getChildrenByStatus(status: String): List<ChildrenEntity> = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("FILTER", "children", "status: $status")
            getAllChildren().filter { it.status == status }
        } catch (e: Exception) {
            Logger.logError("ChildRepository", e, "Failed to get children by status: $status")
            emptyList()
        }
    }
}
