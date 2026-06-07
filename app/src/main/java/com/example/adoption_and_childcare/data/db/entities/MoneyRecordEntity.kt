package com.example.adoption_and_childcare.data.db.entities

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
    @ColumnInfo(name = "mpesa_receipt_no") val mpesaReceiptNo: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "receipt_path") val receiptPath: String? = null,
    @ColumnInfo(name = "receipt_data", typeAffinity = ColumnInfo.BLOB) val receiptData: ByteArray? = null,
    @ColumnInfo(name = "receipt_mime_type") val receiptMimeType: String? = null,
    @ColumnInfo(name = "receipt_size") val receiptSize: Int? = null,
    @ColumnInfo(name = "approved_by") val approvedBy: Int? = null,
    @ColumnInfo(name = "approved_at") val approvedAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null,
    // New fields for M-Pesa and banking support
    @ColumnInfo(name = "payment_method") val paymentMethod: String? = null,
    @ColumnInfo(name = "mpesa_phone_number") val mpesaPhoneNumber: String? = null,
    @ColumnInfo(name = "mpesa_transaction_id") val mpesaTransactionId: String? = null,
    @ColumnInfo(name = "bank_account") val bankAccount: String? = null,
    @ColumnInfo(name = "bank_reference") val bankReference: String? = null
)