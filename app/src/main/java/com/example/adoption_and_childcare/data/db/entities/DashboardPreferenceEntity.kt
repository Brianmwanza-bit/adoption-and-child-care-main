package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user's dashboard preferences.
 *
 * This entity stores per-user customization for the dashboard layout and display.
 *
 * @property preferenceId Unique identifier (auto-generated).
 * @property userId User ID this preference belongs to.
 * @property layoutType Layout style (e.g., compact, cards, list).
 * @property showMetrics Whether to show metric cards.
 * @property showAlerts Whether to show alert cards.
 * @property showActionItems Whether to show action items section.
 * @property showRecentUpdates Whether to show recent updates section.
 * @property darkMode Whether dark mode is enabled.
 * @property notificationFrequency How often notifications are sent (e.g., immediate, hourly, daily).
 * @property quietHoursEnabled Whether quiet hours are enabled.
 * @property quietHoursStart Start time for quiet hours (HH:mm).
 * @property quietHoursEnd End time for quiet hours (HH:mm).
 * @property createdAt Date when the preference was created.
 * @property updatedAt Date when the preference was last updated.
 */
@Entity(tableName = "dashboard_preferences")
data class DashboardPreferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preference_id") val preferenceId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "layout_type") val layoutType: String? = "cards",
    @ColumnInfo(name = "show_metrics") val showMetrics: Boolean = true,
    @ColumnInfo(name = "show_alerts") val showAlerts: Boolean = true,
    @ColumnInfo(name = "show_action_items") val showActionItems: Boolean = true,
    @ColumnInfo(name = "show_recent_updates") val showRecentUpdates: Boolean = true,
    @ColumnInfo(name = "dark_mode") val darkMode: Boolean = false,
    @ColumnInfo(name = "notification_frequency") val notificationFrequency: String? = "immediate",
    @ColumnInfo(name = "quiet_hours_enabled") val quietHoursEnabled: Boolean = false,
    @ColumnInfo(name = "quiet_hours_start") val quietHoursStart: String? = null,
    @ColumnInfo(name = "quiet_hours_end") val quietHoursEnd: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
