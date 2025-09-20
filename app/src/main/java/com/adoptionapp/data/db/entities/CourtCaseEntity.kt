package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "court_cases")
data class CourtCaseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "case_id") val caseId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "case_number") val caseNumber: String? = null,
    @ColumnInfo(name = "court_name") val courtName: String? = null,
    @ColumnInfo(name = "judge_name") val judgeName: String? = null,
    @ColumnInfo(name = "case_type") val caseType: String? = null,
    @ColumnInfo(name = "filing_date") val filingDate: String? = null,
    @ColumnInfo(name = "hearing_date") val hearingDate: String? = null,
    @ColumnInfo(name = "outcome") val outcome: String? = null,
    @ColumnInfo(name = "status") val status: String? = "Pending",
    @ColumnInfo(name = "next_hearing_date") val nextHearingDate: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
