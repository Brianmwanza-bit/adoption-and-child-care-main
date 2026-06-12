package com.example.adoption_and_childcare.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manager for encrypted SharedPreferences storage.
 * 
 * This class provides secure storage for sensitive data like
 * authentication tokens, refresh tokens, and biometric data using
 * Android's EncryptedSharedPreferences with AES-256 encryption.
 * 
 * @property context Application context for SharedPreferences access.
 */
class EncryptedPreferencesManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) {
        encryptedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return encryptedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        encryptedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? {
        return encryptedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun saveUserId(userId: String) {
        encryptedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return encryptedPreferences.getString(KEY_USER_ID, null)
    }

    fun setBiometricData(userId: String, fingerprint: String) {
        encryptedPreferences.edit().putString("biometric_$userId", fingerprint).apply()
    }

    fun getBiometricData(userId: String): String? {
        return encryptedPreferences.getString("biometric_$userId", null)
    }

    fun removeBiometricData(userId: String) {
        encryptedPreferences.edit().remove("biometric_$userId").apply()
    }

    fun clearAll() {
        encryptedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "adoption_app_encrypted_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
    }
}
