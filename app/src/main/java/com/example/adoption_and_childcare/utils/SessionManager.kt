package com.example.adoption_and_childcare.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.adoption_and_childcare.data.db.entities.UserEntity

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveSession(user: UserEntity) {
        prefs.edit()
            .putString("username", user.username)
            .putString("role", user.role)
            .putString("email", user.email)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getUsername(): String? = prefs.getString("username", null)
    fun getRole(): String? = prefs.getString("role", null)
    fun getEmail(): String? = prefs.getString("email", null)
    fun isLoggedIn(): Boolean = getUsername() != null
}
