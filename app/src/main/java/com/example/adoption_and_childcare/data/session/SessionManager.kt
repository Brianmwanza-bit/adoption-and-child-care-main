package com.example.adoption_and_childcare.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.adoption_and_childcare.MyFirebaseMessagingService
import com.example.adoption_and_childcare.data.db.entities.UserEntity

/**
 * Manager for user session persistence.
 * 
 * This class handles saving and retrieving user session data
 * including user ID, username, role, and authentication token.
 * 
 * @property context Application context for SharedPreferences access.
 */
class SessionManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("adoption_session", Context.MODE_PRIVATE)

    fun saveSession(user: UserEntity) {
        prefs.edit()
            .putInt(KEY_USER_ID, user.userId)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_ROLE, user.role)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_COUNTY, user.county)
            .apply()

        // Sync pending FCM token upon login session save
        MyFirebaseMessagingService.sendPendingTokenToServer(context)
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.contains(KEY_USER_ID)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getRole(): String? = prefs.getString(KEY_ROLE, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getCounty(): String? = prefs.getString(KEY_COUNTY, null)

    fun userId(): Int = getUserId()
    fun username(): String = getUsername() ?: ""
    fun role(): String = getRole() ?: ""
    fun email(): String = getEmail() ?: ""

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_EMAIL = "email"
        private const val KEY_COUNTY = "county"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
