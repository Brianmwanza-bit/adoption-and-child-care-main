package com.example.adoption_and_childcare.utils

import android.content.Context
import com.example.adoption_and_childcare.data.session.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized authentication manager for handling JWT tokens.
 * Provides a unified interface for all repositories to access authentication tokens.
 * 
 * This manager integrates with SessionManager to retrieve tokens stored during login,
 * enabling authenticated API calls across the entire application.
 */
@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sessionManager: SessionManager by lazy { SessionManager(context) }
    
    /**
     * Gets the current authentication token.
     * Returns null if user is not logged in.
     */
    fun getAuthToken(): String? {
        return sessionManager.getAuthToken()
    }
    
    /**
     * Checks if user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    /**
     * Gets the authorization header value for API calls.
     * Returns null if no token is available.
     * 
     * Example return value: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     */
    fun getAuthHeader(): String? {
        val token = getAuthToken()
        return if (token != null) {
            "Bearer $token"
        } else {
            null
        }
    }
    
    /**
     * Gets user ID from current session.
     * Returns -1 if not logged in.
     */
    fun getUserId(): Int {
        return sessionManager.getUserId()
    }
    
    /**
     * Gets username from current session.
     */
    fun getUsername(): String {
        return sessionManager.getUsername() ?: ""
    }
    
    /**
     * Gets user role from current session.
     */
    fun getRole(): String {
        return sessionManager.getRole() ?: ""
    }
    
    /**
     * Clears all authentication data (logout).
     */
    fun logout() {
        sessionManager.clearSession()
    }
}
