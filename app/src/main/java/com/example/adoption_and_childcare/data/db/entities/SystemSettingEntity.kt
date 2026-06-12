package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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