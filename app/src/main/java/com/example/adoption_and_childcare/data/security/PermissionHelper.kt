package com.example.adoption_and_childcare.data.security

import android.content.Context
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.dao.GuardianDao
import com.example.adoption_and_childcare.data.session.SessionManager
import kotlinx.coroutines.flow.first

/**
 * Permission helper class for role-based access control (RBAC)
 * 
 * Roles and their access levels:
 * - Admin: Full access to all data
 * - Case Worker: Full access to all data
 * - Supervisor: Full access to all data  
 * - Foster Parent: Limited to children they are guardians of
 * - Guest: Read-only access to their assigned children
 */
class PermissionHelper(private val context: Context) {
    
    private val sessionManager = SessionManager(context)
    private val database: AppDatabase = AppDatabase.getInstance(context)
    private val guardianDao: GuardianDao = database.guardianDao()
    
    /**
     * Check if current user has full access (admin or case worker)
     */
    suspend fun hasFullAccess(): Boolean {
        val role = sessionManager.getRole()
        return role in listOf("Admin", "Case Worker", "Supervisor")
    }
    
    /**
     * Check if current user is a foster parent or guardian with restricted access
     */
    suspend fun isRestrictedUser(): Boolean {
        val role = sessionManager.getRole()
        return role in listOf("Foster Parent", "Parent", "Guardian", "Guest")
    }
    
    /**
     * Get the list of child IDs that the current user can access
     * For admins/case workers, this returns null (no restriction)
     * For guardians, this returns only their assigned children
     */
    suspend fun getAccessibleChildIds(): List<Int>? {
        if (hasFullAccess()) {
            return null // No restriction
        }
        
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            return emptyList() // Not logged in
        }
        
        return guardianDao.getChildIdsByUserId(userId)
    }
    
    /**
     * Check if user can access a specific child's data
     */
    suspend fun canAccessChild(childId: Int): Boolean {
        if (hasFullAccess()) {
            return true
        }
        
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            return false
        }
        
        val accessibleChildIds = guardianDao.getChildIdsByUserId(userId)
        return childId in accessibleChildIds
    }
    
    /**
     * Check if user can update a specific child's data
     * Foster parents/guardians can only update their assigned children
     */
    suspend fun canUpdateChild(childId: Int): Boolean {
        if (hasFullAccess()) {
            return true
        }
        
        val role = sessionManager.getRole()
        if (role == "Guest") {
            return false // Guests are read-only
        }
        
        return canAccessChild(childId)
    }
    
    /**
     * Check if user can delete a specific child's data
     * Only admins and case workers can delete child records
     */
    suspend fun canDeleteChild(): Boolean {
        val role = sessionManager.getRole()
        return role in listOf("Admin", "Case Worker")
    }
    
    /**
     * Check if user can access all children (unrestricted view)
     */
    suspend fun canViewAllChildren(): Boolean {
        return hasFullAccess()
    }
    
    /**
     * Get current user's role
     */
    fun getUserRole(): String? {
        return sessionManager.getRole()
    }
    
    /**
     * Get current user's ID
     */
    fun getCurrentUserId(): Int {
        return sessionManager.getUserId()
    }
}