package com.example.adoption_and_childcare.data.session

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("adoption_settings", Context.MODE_PRIVATE)

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) { prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply() }

    var wifiOnlySync: Boolean
        get() = prefs.getBoolean(KEY_WIFI_ONLY_SYNC, false)
        set(value) { prefs.edit().putBoolean(KEY_WIFI_ONLY_SYNC, value).apply() }

    // Network/API Configuration
    // For Emulator: Use http://10.0.2.2:50000/ (auto-mapped from localhost)
    // For Physical Device: Use your machine's IP (e.g., http://192.168.x.x:50000/)
    var apiBaseUrl: String
        get() = prefs.getString(KEY_API_BASE_URL, com.example.adoption_and_childcare.BuildConfig.BASE_URL) ?: com.example.adoption_and_childcare.BuildConfig.BASE_URL
        set(value) { prefs.edit().putString(KEY_API_BASE_URL, value).apply() }

    var apiTimeout: Int
        get() = prefs.getInt(KEY_API_TIMEOUT, 30)
        set(value) { prefs.edit().putInt(KEY_API_TIMEOUT, value).apply() }

    var apiRetryCount: Int
        get() = prefs.getInt(KEY_API_RETRY_COUNT, 3)
        set(value) { prefs.edit().putInt(KEY_API_RETRY_COUNT, value).apply() }

    // Sync Configuration
    var syncIntervalHours: Long
        get() = prefs.getLong(KEY_SYNC_INTERVAL, 6)
        set(value) { prefs.edit().putLong(KEY_SYNC_INTERVAL, value).apply() }

    var autoSyncEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SYNC, true)
        set(value) { prefs.edit().putBoolean(KEY_AUTO_SYNC, value).apply() }

    // File Upload Configuration
    var maxFileSizeMB: Long
        get() = prefs.getLong(KEY_MAX_FILE_SIZE, 10)
        set(value) { prefs.edit().putLong(KEY_MAX_FILE_SIZE, value).apply() }

    var uploadTimeout: Int
        get() = prefs.getInt(KEY_UPLOAD_TIMEOUT, 60)
        set(value) { prefs.edit().putInt(KEY_UPLOAD_TIMEOUT, value).apply() }

    // Security Configuration
    var sessionTimeoutMinutes: Int
        get() = prefs.getInt(KEY_SESSION_TIMEOUT, 30)
        set(value) { prefs.edit().putInt(KEY_SESSION_TIMEOUT, value).apply() }

    var maxLoginAttempts: Int
        get() = prefs.getInt(KEY_MAX_LOGIN_ATTEMPTS, 5)
        set(value) { prefs.edit().putInt(KEY_MAX_LOGIN_ATTEMPTS, value).apply() }

    // UI Configuration
    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) { prefs.edit().putString(KEY_THEME_MODE, value).apply() }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) { prefs.edit().putString(KEY_LANGUAGE, value).apply() }

    // Database Configuration
    var useLocalDatabase: Boolean
        get() = prefs.getBoolean(KEY_USE_LOCAL_DB, true)
        set(value) { prefs.edit().putBoolean(KEY_USE_LOCAL_DB, value).apply() }

    var localDatabasePath: String?
        get() = prefs.getString(KEY_LOCAL_DB_PATH, null)
        set(value) { prefs.edit().putString(KEY_LOCAL_DB_PATH, value).apply() }

    // Debug Configuration
    var debugMode: Boolean
        get() = prefs.getBoolean(KEY_DEBUG_MODE, false)
        set(value) { prefs.edit().putBoolean(KEY_DEBUG_MODE, value).apply() }

    var enableLogging: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_LOGGING, true)
        set(value) { prefs.edit().putBoolean(KEY_ENABLE_LOGGING, value).apply() }
    
    // SOS Emergency Contacts
    fun getSosContact(key: String): String {
        return prefs.getString("sos_$key", "") ?: ""
    }
    
    fun setSosContact(key: String, value: String) {
        prefs.edit().putString("sos_$key", value).apply()
    }

    companion object {
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_WIFI_ONLY_SYNC = "wifi_only_sync"
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_API_TIMEOUT = "api_timeout"
        private const val KEY_API_RETRY_COUNT = "api_retry_count"
        private const val KEY_SYNC_INTERVAL = "sync_interval"
        private const val KEY_AUTO_SYNC = "auto_sync"
        private const val KEY_MAX_FILE_SIZE = "max_file_size"
        private const val KEY_UPLOAD_TIMEOUT = "upload_timeout"
        private const val KEY_SESSION_TIMEOUT = "session_timeout"
        private const val KEY_MAX_LOGIN_ATTEMPTS = "max_login_attempts"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_USE_LOCAL_DB = "use_local_db"
        private const val KEY_LOCAL_DB_PATH = "local_db_path"
        private const val KEY_DEBUG_MODE = "debug_mode"
        private const val KEY_ENABLE_LOGGING = "enable_logging"
    }
}