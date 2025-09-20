package com.adoptionapp.data.session

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

    companion object {
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_WIFI_ONLY_SYNC = "wifi_only_sync"
    }
}
