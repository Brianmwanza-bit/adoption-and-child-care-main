package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "money_records")
data class MoneyRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val money_id: Int = 0,
    val child_id: Int,
    val amount: Double,
    val transaction_type: String?,
    val description: String?,
    val date: String,
    val receipt_path: String?,
    val approved_by: Int?,
    val approved_at: String?,
    val created_at: String?,
    val created_by: Int?
)
