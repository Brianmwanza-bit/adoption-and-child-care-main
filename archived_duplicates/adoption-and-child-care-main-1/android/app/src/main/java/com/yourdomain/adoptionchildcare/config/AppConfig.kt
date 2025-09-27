package com.yourdomain.adoptionchildcare.config

/**
 * Application configuration constants and settings
 */
object AppConfig {
    
    // API Configuration
    const val API_BASE_URL = "http://10.0.2.2:50000/"
    const val API_TIMEOUT_SECONDS = 30L
    const val API_RETRY_COUNT = 3
    
    // Database Configuration
    const val DATABASE_NAME = "adoption_child_care.db"
    const val DATABASE_VERSION = 3
    
    // Sync Configuration
    const val SYNC_INTERVAL_HOURS = 6L
    const val MAX_SYNC_RETRIES = 3
    
    // File Upload Configuration
    const val MAX_FILE_SIZE_MB = 10L
    const val ALLOWED_FILE_TYPES = "jpg,jpeg,png,pdf,doc,docx"
    const val UPLOAD_TIMEOUT_SECONDS = 60L
    
    // Security Configuration
    const val SESSION_TIMEOUT_MINUTES = 30L
    const val PASSWORD_MIN_LENGTH = 8
    const val MAX_LOGIN_ATTEMPTS = 5
    
    // UI Configuration
    const val ANIMATION_DURATION_MS = 300L
    const val TOAST_DURATION_MS = 3000L
    const val DIALOG_TIMEOUT_MS = 5000L
    
    // Notification Configuration
    const val NOTIFICATION_CHANNEL_ID = "adoption_app_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Adoption App Notifications"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Important updates and reminders"
    
    // Logging Configuration
    const val LOG_TAG_PREFIX = "AdoptionApp"
    const val MAX_LOG_SIZE_MB = 5L
    const val LOG_RETENTION_DAYS = 7L
    
    // Child Status Values
    object ChildStatus {
        const val AVAILABLE = "available"
        const val PLACED = "placed"
        const val PENDING = "pending"
        const val ADOPTED = "adopted"
        const val INACTIVE = "inactive"
    }
    
    // User Roles
    object UserRoles {
        const val ADMIN = "admin"
        const val SOCIAL_WORKER = "social_worker"
        const val GUARDIAN = "guardian"
        const val VOLUNTEER = "volunteer"
        const val VIEWER = "viewer"
    }
    
    // Case Status Values
    object CaseStatus {
        const val OPEN = "open"
        const val IN_PROGRESS = "in_progress"
        const val PENDING_REVIEW = "pending_review"
        const val APPROVED = "approved"
        const val REJECTED = "rejected"
        const val CLOSED = "closed"
    }
    
    // Background Check Status
    object BackgroundCheckStatus {
        const val PENDING = "pending"
        const val IN_PROGRESS = "in_progress"
        const val PASSED = "passed"
        const val FAILED = "failed"
        const val EXPIRED = "expired"
    }
    
    // Document Types
    object DocumentTypes {
        const val BIRTH_CERTIFICATE = "birth_certificate"
        const val MEDICAL_RECORD = "medical_record"
        const val SCHOOL_RECORD = "school_record"
        const val LEGAL_DOCUMENT = "legal_document"
        const val PHOTO = "photo"
        const val OTHER = "other"
    }
    
    // Error Messages
    object ErrorMessages {
        const val NETWORK_ERROR = "Network connection error. Please check your internet connection."
        const val SERVER_ERROR = "Server error. Please try again later."
        const val AUTHENTICATION_ERROR = "Authentication failed. Please log in again."
        const val VALIDATION_ERROR = "Please check your input and try again."
        const val PERMISSION_ERROR = "You don't have permission for this action."
        const val FILE_UPLOAD_ERROR = "File upload failed. Please try again."
        const val DATABASE_ERROR = "Database error. Please try again."
        const val UNKNOWN_ERROR = "An unexpected error occurred. Please try again."
    }
    
    // Success Messages
    object SuccessMessages {
        const val LOGIN_SUCCESS = "Login successful"
        const val REGISTRATION_SUCCESS = "Registration successful"
        const val DATA_SAVED = "Data saved successfully"
        const val DATA_UPDATED = "Data updated successfully"
        const val DATA_DELETED = "Data deleted successfully"
        const val SYNC_SUCCESS = "Data synchronized successfully"
        const val FILE_UPLOADED = "File uploaded successfully"
    }
    
    // Validation Rules
    object ValidationRules {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50
        const val MIN_AGE = 0
        const val MAX_AGE = 18
        const val MIN_PHONE_LENGTH = 10
        const val MAX_PHONE_LENGTH = 15
        const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    }
}
