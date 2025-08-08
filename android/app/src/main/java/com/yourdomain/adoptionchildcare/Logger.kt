package com.yourdomain.adoptionchildcare

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Comprehensive logging system for the adoption and child care app
 */
object Logger {
    private const val TAG_PREFIX = "AdoptionApp"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    
    /**
     * Log levels for different types of messages
     */
    enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    /**
     * Log verbose messages (development only)
     */
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.VERBOSE, tag, message, throwable)
    }

    /**
     * Log debug messages
     */
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.DEBUG, tag, message, throwable)
    }

    /**
     * Log info messages
     */
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.INFO, tag, message, throwable)
    }

    /**
     * Log warning messages
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.WARNING, tag, message, throwable)
    }

    /**
     * Log error messages
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.ERROR, tag, message, throwable)
    }

    /**
     * Central logging method
     */
    private fun log(level: Level, tag: String, message: String, throwable: Throwable?) {
        val timestamp = dateFormat.format(Date())
        val fullTag = "$TAG_PREFIX:$tag"
        val logMessage = "[$timestamp] $message"

        when (level) {
            Level.VERBOSE -> Log.v(fullTag, logMessage, throwable)
            Level.DEBUG -> Log.d(fullTag, logMessage, throwable)
            Level.INFO -> Log.i(fullTag, logMessage, throwable)
            Level.WARNING -> Log.w(fullTag, logMessage, throwable)
            Level.ERROR -> Log.e(fullTag, logMessage, throwable)
        }
    }

    /**
     * Log API requests
     */
    fun logApiRequest(method: String, url: String, headers: Map<String, String>? = null) {
        i("API", "Request: $method $url")
        headers?.forEach { (key, value) ->
            d("API", "Header: $key = $value")
        }
    }

    /**
     * Log API responses
     */
    fun logApiResponse(url: String, statusCode: Int, responseTime: Long) {
        i("API", "Response: $url - Status: $statusCode - Time: ${responseTime}ms")
    }

    /**
     * Log database operations
     */
    fun logDatabaseOperation(operation: String, table: String, id: String? = null) {
        i("Database", "$operation on $table${id?.let { " (ID: $it)" } ?: ""}")
    }

    /**
     * Log user actions
     */
    fun logUserAction(action: String, userId: String? = null, details: String? = null) {
        i("UserAction", "$action${userId?.let { " by user $it" } ?: ""}${details?.let { " - $it" } ?: ""}")
    }

    /**
     * Log security events
     */
    fun logSecurityEvent(event: String, userId: String? = null, details: String? = null) {
        w("Security", "$event${userId?.let { " for user $it" } ?: ""}${details?.let { " - $it" } ?: ""}")
    }

    /**
     * Log performance metrics
     */
    fun logPerformance(operation: String, duration: Long, details: String? = null) {
        i("Performance", "$operation took ${duration}ms${details?.let { " - $it" } ?: ""}")
    }

    /**
     * Log sync operations
     */
    fun logSync(operation: String, entity: String, success: Boolean, details: String? = null) {
        val status = if (success) "SUCCESS" else "FAILED"
        i("Sync", "$operation $entity - $status${details?.let { " - $it" } ?: ""}")
    }

    /**
     * Log error with context
     */
    fun logError(context: String, error: Throwable, additionalInfo: String? = null) {
        e(context, "Error occurred${additionalInfo?.let { " - $it" } ?: ""}", error)
    }

    /**
     * Log startup sequence
     */
    fun logStartup(component: String, status: String) {
        i("Startup", "$component: $status")
    }

    /**
     * Log configuration changes
     */
    fun logConfigChange(setting: String, oldValue: String?, newValue: String?) {
        i("Config", "Changed $setting from '$oldValue' to '$newValue'")
    }
}
