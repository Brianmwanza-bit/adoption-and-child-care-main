package com.yourdomain.adoptionchildcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icons")
data class IconEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "icon_id") val iconId: Int = 0,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "provider") val provider: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
