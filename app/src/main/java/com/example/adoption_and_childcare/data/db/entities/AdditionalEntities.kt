package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placement_disruptions")
data class PlacementDisruptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "disruption_id") val disruptionId: Int = 0,
    @ColumnInfo(name = "placement_id") val placementId: Int,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "disruption_date") val disruptionDate: String,
    @ColumnInfo(name = "disruption_type") val disruptionType: String? = null,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "child_behavior_factor") val childBehaviorFactor: String? = null,
    @ColumnInfo(name = "family_factor") val familyFactor: String? = null,
    @ColumnInfo(name = "agency_factor") val agencyFactor: String? = null,
    @ColumnInfo(name = "reunification_attempted") val reunificationAttempted: Boolean = false,
    @ColumnInfo(name = "new_placement_id") val newPlacementId: Int? = null,
    @ColumnInfo(name = "caseworker_id") val caseworkerId: Int? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "foster_family_training")
data class FosterFamilyTrainingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "training_id") val trainingId: Int = 0,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "training_name") val trainingName: String,
    @ColumnInfo(name = "training_date") val trainingDate: String,
    @ColumnInfo(name = "completion_date") val completionDate: String? = null,
    @ColumnInfo(name = "status") val status: String? = "scheduled",
    @ColumnInfo(name = "trainer_name") val trainerName: String? = null,
    @ColumnInfo(name = "certificate_issued") val certificateIssued: Boolean = false,
    @ColumnInfo(name = "certificate_number") val certificateNumber: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "reports_generated")
data class ReportGeneratedEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "report_id") val reportId: Int = 0,
    @ColumnInfo(name = "report_name") val reportName: String,
    @ColumnInfo(name = "report_type") val reportType: String? = null,
    @ColumnInfo(name = "generated_by") val generatedBy: Int? = null,
    @ColumnInfo(name = "generated_date") val generatedDate: String? = null,
    @ColumnInfo(name = "file_path") val filePath: String? = null,
    @ColumnInfo(name = "file_size") val fileSize: Int? = null,
    @ColumnInfo(name = "download_count") val downloadCount: Int? = 0,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "emergency_events")
data class EmergencyEventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id") val eventId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int? = null,
    @ColumnInfo(name = "reported_by") val reportedBy: Int? = null,
    @ColumnInfo(name = "event_type") val eventType: String? = null,
    @ColumnInfo(name = "event_date") val eventDate: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "action_taken") val actionTaken: String? = null,
    @ColumnInfo(name = "status") val status: String? = "open",
    @ColumnInfo(name = "resolved_by") val resolvedBy: Int? = null,
    @ColumnInfo(name = "resolved_at") val resolvedAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "global_document_storage")
data class GlobalDocumentStorageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "document_id") val documentId: Int = 0,
    @ColumnInfo(name = "document_name") val documentName: String,
    @ColumnInfo(name = "document_category") val documentCategory: String? = null,
    @ColumnInfo(name = "document_type") val documentType: String? = null,
    @ColumnInfo(name = "file_path") val filePath: String? = null,
    @ColumnInfo(name = "file_size") val fileSize: Int? = null,
    @ColumnInfo(name = "mime_type") val mimeType: String? = null,
    @ColumnInfo(name = "uploaded_by") val uploadedBy: Int? = null,
    @ColumnInfo(name = "is_public") val isPublic: Boolean = false,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "version") val version: Int? = 1,
    @ColumnInfo(name = "uploaded_at") val uploadedAt: String? = null
)

@Entity(tableName = "inter_county_transfers")
data class InterCountyTransferEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transfer_id") val transferId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "from_county") val fromCounty: String? = null,
    @ColumnInfo(name = "to_county") val toCounty: String? = null,
    @ColumnInfo(name = "transfer_date") val transferDate: String,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "authorized_by") val authorizedBy: Int? = null,
    @ColumnInfo(name = "receiving_officer") val receivingOfficer: Int? = null,
    @ColumnInfo(name = "documents_transferred") val documentsTransferred: Boolean = false,
    @ColumnInfo(name = "transfer_status") val transferStatus: String? = "pending",
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "worker_location_tracking")
data class WorkerLocationTrackingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracking_id") val trackingId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "accuracy") val accuracy: Double? = null,
    @ColumnInfo(name = "tracking_time") val trackingTime: String,
    @ColumnInfo(name = "activity_type") val activityType: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

