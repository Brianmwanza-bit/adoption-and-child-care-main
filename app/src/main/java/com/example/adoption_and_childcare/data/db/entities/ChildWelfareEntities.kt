package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a child's behavior assessment.
 *
 * @property assessmentId Unique identifier for the assessment.
 * @property childId ID of the child being assessed.
 * @property assessmentDate Date the assessment was conducted.
 * @property assessmentTool Tool or method used for assessment.
 * @property behavioralScore Score for behavioral development.
 * @property emotionalScore Score for emotional development.
 * @property socialScore Score for social development.
 * @property academicBehaviorScore Score for behavior in an academic setting.
 * @property overallScore Overall assessment score.
 * @property strengths Observed strengths of the child.
 * @property challenges Observed challenges or areas for improvement.
 * @property recommendations Recommended actions or interventions.
 * @property assessedBy ID of the individual who conducted the assessment.
 * @property createdAt Timestamp when the record was created.
 */
@Entity(tableName = ChildBehaviorAssessmentEntity.TABLE_NAME)
data class ChildBehaviorAssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) val assessmentId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_ASSESSMENT_DATE) val assessmentDate: String,
    @ColumnInfo(name = COLUMN_ASSESSMENT_TOOL) val assessmentTool: String? = null,
    @ColumnInfo(name = COLUMN_BEHAVIORAL_SCORE) val behavioralScore: Int? = null,
    @ColumnInfo(name = COLUMN_EMOTIONAL_SCORE) val emotionalScore: Int? = null,
    @ColumnInfo(name = COLUMN_SOCIAL_SCORE) val socialScore: Int? = null,
    @ColumnInfo(name = COLUMN_ACADEMIC_SCORE) val academicBehaviorScore: Int? = null,
    @ColumnInfo(name = COLUMN_OVERALL_SCORE) val overallScore: Int? = null,
    @ColumnInfo(name = COLUMN_STRENGTHS) val strengths: String? = null,
    @ColumnInfo(name = COLUMN_CHALLENGES) val challenges: String? = null,
    @ColumnInfo(name = COLUMN_RECOMMENDATIONS) val recommendations: String? = null,
    @ColumnInfo(name = COLUMN_ASSESSED_BY) val assessedBy: Int? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null
) {
    /** Companion object for ChildBehaviorAssessmentEntity containing database constants. */
    companion object {
        /** Table name for child behavior assessments. */
        const val TABLE_NAME = "child_behavior_assessments"
        /** Column name for the primary key. */
        const val COLUMN_ID = "assessment_id"
        /** Column name for the child ID. */
        const val COLUMN_CHILD_ID = "child_id"
        /** Column name for the assessment date. */
        const val COLUMN_ASSESSMENT_DATE = "assessment_date"
        /** Column name for the assessment tool. */
        const val COLUMN_ASSESSMENT_TOOL = "assessment_tool"
        /** Column name for the behavioral score. */
        const val COLUMN_BEHAVIORAL_SCORE = "behavioral_score"
        /** Column name for the emotional score. */
        const val COLUMN_EMOTIONAL_SCORE = "emotional_score"
        /** Column name for the social score. */
        const val COLUMN_SOCIAL_SCORE = "social_score"
        /** Column name for the academic behavior score. */
        const val COLUMN_ACADEMIC_SCORE = "academic_behavior_score"
        /** Column name for the overall score. */
        const val COLUMN_OVERALL_SCORE = "overall_score"
        /** Column name for strengths. */
        const val COLUMN_STRENGTHS = "strengths"
        /** Column name for challenges. */
        const val COLUMN_CHALLENGES = "challenges"
        /** Column name for recommendations. */
        const val COLUMN_RECOMMENDATIONS = "recommendations"
        /** Column name for the individual who assessed. */
        const val COLUMN_ASSESSED_BY = "assessed_by"
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
    }
}

/**
 * Entity representing an incident related to child welfare.
 *
 * @property incidentId Unique identifier for the incident.
 * @property childId ID of the child involved.
 * @property incidentDate Date the incident occurred.
 * @property incidentType Type or category of the incident.
 * @property severity Severity level of the incident (e.g., low, medium, high).
 * @property description Detailed description of the incident.
 * @property location Where the incident took place.
 * @property reportedBy ID of the individual who reported the incident.
 * @property actionsTaken Actions taken in response to the incident.
 * @property policeInvolved Whether law enforcement was involved.
 * @property policeReportNo Police report number, if applicable.
 * @property followUpRequired Whether follow-up action is necessary.
 * @property resolvedDate Date the incident was officially resolved.
 * @property createdAt Timestamp when the record was created.
 */
@Entity(tableName = ChildWelfareIncidentEntity.TABLE_NAME)
data class ChildWelfareIncidentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) val incidentId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_INCIDENT_DATE) val incidentDate: String,
    @ColumnInfo(name = COLUMN_INCIDENT_TYPE) val incidentType: String? = null,
    @ColumnInfo(name = COLUMN_SEVERITY) val severity: String? = SEVERITY_MEDIUM,
    @ColumnInfo(name = COLUMN_DESCRIPTION) val description: String? = null,
    @ColumnInfo(name = COLUMN_LOCATION) val location: String? = null,
    @ColumnInfo(name = COLUMN_REPORTED_BY) val reportedBy: Int? = null,
    @ColumnInfo(name = COLUMN_ACTIONS_TAKEN) val actionsTaken: String? = null,
    @ColumnInfo(name = COLUMN_POLICE_INVOLVED) val policeInvolved: Boolean = false,
    @ColumnInfo(name = COLUMN_POLICE_REPORT_NO) val policeReportNo: String? = null,
    @ColumnInfo(name = COLUMN_FOLLOW_UP) val followUpRequired: Boolean = false,
    @ColumnInfo(name = COLUMN_RESOLVED_DATE) val resolvedDate: String? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null
) {
    /** Companion object for ChildWelfareIncidentEntity containing database constants. */
    companion object {
        /** Table name for child welfare incidents. */
        const val TABLE_NAME = "child_welfare_incidents"
        /** Column name for the primary key. */
        const val COLUMN_ID = "incident_id"
        /** Column name for the child ID. */
        const val COLUMN_CHILD_ID = "child_id"
        /** Column name for the incident date. */
        const val COLUMN_INCIDENT_DATE = "incident_date"
        /** Column name for the incident type. */
        const val COLUMN_INCIDENT_TYPE = "incident_type"
        /** Column name for the severity level. */
        const val COLUMN_SEVERITY = "severity"
        /** Column name for the description. */
        const val COLUMN_DESCRIPTION = "description"
        /** Column name for the location. */
        const val COLUMN_LOCATION = "location"
        /** Column name for the reporter ID. */
        const val COLUMN_REPORTED_BY = "reported_by"
        /** Column name for actions taken. */
        const val COLUMN_ACTIONS_TAKEN = "actions_taken"
        /** Column name for police involvement status. */
        const val COLUMN_POLICE_INVOLVED = "police_involved"
        /** Column name for the police report number. */
        const val COLUMN_POLICE_REPORT_NO = "police_report_no"
        /** Column name for follow-up requirement status. */
        const val COLUMN_FOLLOW_UP = "follow_up_required"
        /** Column name for the resolution date. */
        const val COLUMN_RESOLVED_DATE = "resolved_date"
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
        
        /** Default medium severity level. */
        const val SEVERITY_MEDIUM = "medium"
    }
}

/**
 * Entity representing a child's vaccination record.
 *
 * @property vaccinationId Unique identifier for the vaccination record.
 * @property childId ID of the child.
 * @property vaccineName Name of the vaccine administered.
 * @property doseNumber Dose sequence number (e.g., 1, 2, booster).
 * @property administrationDate Date the vaccine was administered.
 * @property nextDueDate Date the next dose is due.
 * @property administeredBy Individual who administered the vaccine.
 * @property facilityName Name of the medical facility.
 * @property batchNumber Batch or lot number of the vaccine.
 * @property reactions Any observed reactions to the vaccine.
 * @property status Current status of the vaccination (e.g., completed, scheduled).
 * @property createdAt Timestamp when the record was created.
 */
@Entity(tableName = VaccinationRecordEntity.TABLE_NAME)
data class VaccinationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) val vaccinationId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_VACCINE_NAME) val vaccineName: String,
    @ColumnInfo(name = COLUMN_DOSE_NUMBER) val doseNumber: Int? = null,
    @ColumnInfo(name = COLUMN_ADMIN_DATE) val administrationDate: String,
    @ColumnInfo(name = COLUMN_NEXT_DUE) val nextDueDate: String? = null,
    @ColumnInfo(name = COLUMN_ADMIN_BY) val administeredBy: String? = null,
    @ColumnInfo(name = COLUMN_FACILITY) val facilityName: String? = null,
    @ColumnInfo(name = COLUMN_BATCH) val batchNumber: String? = null,
    @ColumnInfo(name = COLUMN_REACTIONS) val reactions: String? = null,
    @ColumnInfo(name = COLUMN_STATUS) val status: String? = STATUS_COMPLETED,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null
) {
    /** Companion object for VaccinationRecordEntity containing database constants. */
    companion object {
        /** Table name for vaccination records. */
        const val TABLE_NAME = "vaccination_records"
        /** Column name for the primary key. */
        const val COLUMN_ID = "vaccination_id"
        /** Column name for the child ID. */
        const val COLUMN_CHILD_ID = "child_id"
        /** Column name for the vaccine name. */
        const val COLUMN_VACCINE_NAME = "vaccine_name"
        /** Column name for the dose number. */
        const val COLUMN_DOSE_NUMBER = "dose_number"
        /** Column name for the administration date. */
        const val COLUMN_ADMIN_DATE = "administration_date"
        /** Column name for the next due date. */
        const val COLUMN_NEXT_DUE = "next_due_date"
        /** Column name for the individual who administered. */
        const val COLUMN_ADMIN_BY = "administered_by"
        /** Column name for the facility name. */
        const val COLUMN_FACILITY = "facility_name"
        /** Column name for the batch number. */
        const val COLUMN_BATCH = "batch_number"
        /** Column name for any reactions. */
        const val COLUMN_REACTIONS = "reactions"
        /** Column name for the status. */
        const val COLUMN_STATUS = "status"
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
        
        /** Default completed status. */
        const val STATUS_COMPLETED = "completed"
    }
}

/**
 * Entity representing a sibling relationship between children.
 *
 * @property siblingId Unique identifier for the sibling relationship.
 * @property childId ID of the first child.
 * @property siblingChildId ID of the sibling child.
 * @property relationshipType Type of relationship (e.g., biological, step).
 * @property samePlacement Whether the siblings are placed together.
 * @property contactAllowed Whether contact between siblings is permitted.
 * @property notes Additional notes regarding the relationship.
 * @property createdAt Timestamp when the record was created.
 */
@Entity(tableName = SiblingEntity.TABLE_NAME)
data class SiblingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) val siblingId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_SIBLING_CHILD_ID) val siblingChildId: Int,
    @ColumnInfo(name = COLUMN_RELATIONSHIP) val relationshipType: String? = null,
    @ColumnInfo(name = COLUMN_SAME_PLACEMENT) val samePlacement: Boolean = false,
    @ColumnInfo(name = COLUMN_CONTACT_ALLOWED) val contactAllowed: Boolean = true,
    @ColumnInfo(name = COLUMN_NOTES) val notes: String? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null
) {
    /** Companion object for SiblingEntity containing database constants. */
    companion object {
        /** Table name for siblings. */
        const val TABLE_NAME = "siblings"
        /** Column name for the primary key. */
        const val COLUMN_ID = "sibling_id"
        /** Column name for the child ID. */
        const val COLUMN_CHILD_ID = "child_id"
        /** Column name for the sibling child ID. */
        const val COLUMN_SIBLING_CHILD_ID = "sibling_child_id"
        /** Column name for the relationship type. */
        const val COLUMN_RELATIONSHIP = "relationship_type"
        /** Column name for same placement status. */
        const val COLUMN_SAME_PLACEMENT = "same_placement"
        /** Column name for contact allowed status. */
        const val COLUMN_CONTACT_ALLOWED = "contact_allowed"
        /** Column name for notes. */
        const val COLUMN_NOTES = "notes"
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
    }
}

/**
 * Entity representing a legal or medical consent record.
 *
 * @property consentId Unique identifier for the consent record.
 * @property childId ID of the child.
 * @property consentType Type of consent (e.g., medical, travel, media).
 * @property providedBy Name of the person providing consent.
 * @property relationshipToChild Relationship of the provider to the child.
 * @property consentDate Date consent was given.
 * @property expiryDate Date the consent expires.
 * @property consentFormFile Path or reference to the signed consent form.
 * @property witnessName Name of the individual witnessing the consent.
 * @property isValid Whether the consent is currently valid.
 * @property revokedDate Date consent was revoked, if applicable.
 * @property revokedReason Reason for revocation.
 * @property createdAt Timestamp when the record was created.
 */
@Entity(tableName = ConsentRecordEntity.TABLE_NAME)
data class ConsentRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) val consentId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_TYPE) val consentType: String,
    @ColumnInfo(name = COLUMN_PROVIDED_BY) val providedBy: String? = null,
    @ColumnInfo(name = COLUMN_RELATIONSHIP) val relationshipToChild: String? = null,
    @ColumnInfo(name = COLUMN_DATE) val consentDate: String,
    @ColumnInfo(name = COLUMN_EXPIRY) val expiryDate: String? = null,
    @ColumnInfo(name = COLUMN_FILE) val consentFormFile: String? = null,
    @ColumnInfo(name = COLUMN_WITNESS) val witnessName: String? = null,
    @ColumnInfo(name = COLUMN_IS_VALID) val isValid: Boolean = true,
    @ColumnInfo(name = COLUMN_REVOKED_DATE) val revokedDate: String? = null,
    @ColumnInfo(name = COLUMN_REVOKED_REASON) val revokedReason: String? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null
) {
    /** Companion object for ConsentRecordEntity containing database constants. */
    companion object {
        /** Table name for consent records. */
        const val TABLE_NAME = "consent_records"
        /** Column name for the primary key. */
        const val COLUMN_ID = "consent_id"
        /** Column name for the child ID. */
        const val COLUMN_CHILD_ID = "child_id"
        /** Column name for the consent type. */
        const val COLUMN_TYPE = "consent_type"
        /** Column name for the individual who provided consent. */
        const val COLUMN_PROVIDED_BY = "provided_by"
        /** Column name for the relationship to the child. */
        const val COLUMN_RELATIONSHIP = "relationship_to_child"
        /** Column name for the consent date. */
        const val COLUMN_DATE = "consent_date"
        /** Column name for the expiry date. */
        const val COLUMN_EXPIRY = "expiry_date"
        /** Column name for the consent form file. */
        const val COLUMN_FILE = "consent_form_file"
        /** Column name for the witness name. */
        const val COLUMN_WITNESS = "witness_name"
        /** Column name for the validity status. */
        const val COLUMN_IS_VALID = "is_valid"
        /** Column name for the revocation date. */
        const val COLUMN_REVOKED_DATE = "revoked_date"
        /** Column name for the revocation reason. */
        const val COLUMN_REVOKED_REASON = "revoked_reason"
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
    }
}

/**
 * Entity representing a referral for child services.
 *
 * @property referralId Unique identifier for the referral.
 * @property childId ID of the child.
 * @property serviceType Type of service being referred (e.g., therapy, tutoring).
 * @property providerId ID of the service provider.
 * @property referralDate Date the referral was made.
 * @property authorizationDate Date the referral was authorized.
 * @property startDate Expected or actual start date of services.
 * @property endDate Expected or actual end date of services.
 * @property frequency Frequency of services (e.g., weekly, monthly).
 * @property status Current status of the referral (e.g., pending, approved).
 * @property authorizedBy ID of the individual who authorized the referral.
 * @property costEstimate Estimated cost of services.
 * @property actualCost Actual cost incurred for services.
 * @property notes Additional notes regarding the referral.
 * @property createdAt Timestamp when the record was created.
 */
@Entity(tableName = ChildServicesReferralEntity.TABLE_NAME)
data class ChildServicesReferralEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) val referralId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_SERVICE_TYPE) val serviceType: String,
    @ColumnInfo(name = COLUMN_PROVIDER_ID) val providerId: Int? = null,
    @ColumnInfo(name = COLUMN_REFERRAL_DATE) val referralDate: String,
    @ColumnInfo(name = COLUMN_AUTH_DATE) val authorizationDate: String? = null,
    @ColumnInfo(name = COLUMN_START_DATE) val startDate: String? = null,
    @ColumnInfo(name = COLUMN_END_DATE) val endDate: String? = null,
    @ColumnInfo(name = COLUMN_FREQUENCY) val frequency: String? = null,
    @ColumnInfo(name = COLUMN_STATUS) val status: String? = STATUS_PENDING,
    @ColumnInfo(name = COLUMN_AUTH_BY) val authorizedBy: Int? = null,
    @ColumnInfo(name = COLUMN_COST_ESTIMATE) val costEstimate: Double? = null,
    @ColumnInfo(name = COLUMN_ACTUAL_COST) val actualCost: Double? = null,
    @ColumnInfo(name = COLUMN_NOTES) val notes: String? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null
) {
    /** Companion object for ChildServicesReferralEntity containing database constants. */
    companion object {
        /** Table name for child services referrals. */
        const val TABLE_NAME = "child_services_referrals"
        /** Column name for the primary key. */
        const val COLUMN_ID = "referral_id"
        /** Column name for the child ID. */
        const val COLUMN_CHILD_ID = "child_id"
        /** Column name for the service type. */
        const val COLUMN_SERVICE_TYPE = "service_type"
        /** Column name for the provider ID. */
        const val COLUMN_PROVIDER_ID = "provider_id"
        /** Column name for the referral date. */
        const val COLUMN_REFERRAL_DATE = "referral_date"
        /** Column name for the authorization date. */
        const val COLUMN_AUTH_DATE = "authorization_date"
        /** Column name for the start date. */
        const val COLUMN_START_DATE = "start_date"
        /** Column name for the end date. */
        const val COLUMN_END_DATE = "end_date"
        /** Column name for the frequency. */
        const val COLUMN_FREQUENCY = "frequency"
        /** Column name for the status. */
        const val COLUMN_STATUS = "status"
        /** Column name for the individual who authorized. */
        const val COLUMN_AUTH_BY = "authorized_by"
        /** Column name for the cost estimate. */
        const val COLUMN_COST_ESTIMATE = "cost_estimate"
        /** Column name for the actual cost. */
        const val COLUMN_ACTUAL_COST = "actual_cost"
        /** Column name for notes. */
        const val COLUMN_NOTES = "notes"
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
        
        /** Default pending status. */
        const val STATUS_PENDING = "pending"
    }
}
