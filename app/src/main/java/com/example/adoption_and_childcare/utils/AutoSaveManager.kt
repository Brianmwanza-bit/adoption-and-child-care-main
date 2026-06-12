package com.example.adoption_and_childcare.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Auto-save manager for auth form fields.
 *
 * Persists registration and login form data so users don't lose
 * input on screen rotation, accidental navigation, or app restart.
 * Data is cleared automatically on successful submission.
 */
class AutoSaveManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Registration fields ──────────────────────────────────────────

    fun saveRegisterField(key: String, value: String) {
        prefs.edit().putString("${PREFIX_REG}$key", value.trim()).apply()
    }

    fun getRegisterField(key: String): String =
        prefs.getString("${PREFIX_REG}$key", "") ?: ""

    fun clearRegisterData() {
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith(PREFIX_REG) }.forEach { editor.remove(it) }
        editor.apply()
    }

    // ── Login fields ─────────────────────────────────────────────────

    fun saveLoginField(key: String, value: String) {
        prefs.edit().putString("${PREFIX_LOGIN}$key", value.trim()).apply()
    }

    fun getLoginField(key: String): String =
        prefs.getString("${PREFIX_LOGIN}$key", "") ?: ""

    fun clearLoginData() {
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith(PREFIX_LOGIN) }.forEach { editor.remove(it) }
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "auth_autosave"
        private const val PREFIX_REG = "reg_"
        private const val PREFIX_LOGIN = "login_"

        // Registration field keys
        const val REG_USERNAME = "username"
        const val REG_EMAIL = "email"
        const val REG_PASSWORD = "password"
        const val REG_PHONE = "phone"
        const val REG_NATIONAL_ID = "national_id"
        const val REG_ID_NUMBER = "id_number"
        const val REG_COUNTY = "county"
        const val REG_SUB_COUNTY = "sub_county"
        const val REG_OCCUPATION = "occupation"

        // Login field keys
        const val LOGIN_EMAIL = "email"
        const val LOGIN_PASSWORD = "password"
    }
}
