package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "money_records")
data class MoneyRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "money_id") val moneyId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "transaction_type") val transactionType: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "receipt_path") val receiptPath: String? = null,
    @ColumnInfo(name = "approved_by") val approvedBy: Int? = null,
    @ColumnInfo(name = "approved_at") val approvedAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null
)
