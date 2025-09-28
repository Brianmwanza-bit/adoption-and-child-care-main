package com.example.adoption_and_childcare.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.adoption_and_childcare.data.db.entities.UserEntity

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("adoption_session", Context.MODE_PRIVATE)

    fun saveSession(user: UserEntity) {
        prefs.edit()
            .putInt(KEY_USER_ID, user.userId)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_ROLE, user.role)
            .putString(KEY_EMAIL, user.email)
            .apply()
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.contains(KEY_USER_ID) && !getAuthToken().isNullOrEmpty()

    fun userId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun username(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun role(): String = prefs.getString(KEY_ROLE, "") ?: ""
    fun email(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_EMAIL = "email"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
