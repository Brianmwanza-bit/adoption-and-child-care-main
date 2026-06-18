package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user's dashboard preferences.
 */
@Entity(tableName = "dashboard_preferences")
data class DashboardPreferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preference_id") val preferenceId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "layout_type") val layoutType: String? = "Grid",
    @ColumnInfo(name = "dark_mode") val darkMode: Boolean = false,
    @ColumnInfo(name = "show_stats") val showStats: Boolean = true,
    @ColumnInfo(name = "show_alerts") val showAlerts: Boolean = true,
    @ColumnInfo(name = "show_action_items") val showActionItems: Boolean = true,
    @ColumnInfo(name = "show_quick_actions") val showQuickActions: Boolean = true,
    @ColumnInfo(name = "show_recent_activity") val showRecentActivity: Boolean = true,
    @ColumnInfo(name = "update_frequency") val updateFrequency: String? = "Daily",
    @ColumnInfo(name = "notifications_enabled") val notificationsEnabled: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
