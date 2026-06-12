package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a system setting in the application.
 * 
 * This entity stores configurable system settings and preferences.
 * 
 * @property settingId Unique identifier for the setting (auto-generated).
 * @property settingKey Unique key identifying the setting.
 * @property settingValue Value of the setting.
 * @property category Category the setting belongs to (e.g., General, Security).
 * @property createdAt Timestamp when the setting was created.
 * @property updatedAt Timestamp when the setting was last updated.
 */
@Entity(tableName = "system_settings")
data class SystemSettingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "setting_id") val settingId: Int = 0,
    @ColumnInfo(name = "setting_key") val settingKey: String,
    @ColumnInfo(name = "setting_value") val settingValue: String? = null,
    @ColumnInfo(name = "category") val category: String? = "General",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)