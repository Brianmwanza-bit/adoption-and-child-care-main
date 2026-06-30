package com.example.adoption_and_childcare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.session.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing different types of search results.
 */
sealed class SearchResult {
    /**
     * Search result for a child.
     * @property id Unique identifier.
     * @property name Full name.
     * @property birthCert Birth certificate number.
     * @property county County of residence.
     * @property caseNumber Case file number.
     */
    data class Child(val id: Int, val name: String, val birthCert: String?, val county: String?, val caseNumber: String?) : SearchResult()

    /**
     * Search result for a family.
     * @property id Unique identifier.
     * @property name Primary contact name.
     * @property phone Contact phone number.
     * @property nationalId Masked or full National ID.
     * @property county County of residence.
     */
    data class Family(val id: Int, val name: String, val phone: String?, val nationalId: String?, val county: String?) : SearchResult()

    /**
     * Search result for a system user.
     * @property id Unique identifier.
     * @property username Account username.
     * @property role System role.
     * @property phone Contact phone number.
     * @property nationalId Masked or full National ID.
     * @property county Assigned county.
     */
    data class User(val id: Int, val username: String, val role: String, val phone: String?, val nationalId: String?, val county: String?) : SearchResult()

    /**
     * Search result for a placement record.
     * @property id Unique identifier.
     * @property type Type of placement (e.g., Foster, Adoption).
     * @property date Start date of placement.
     * @property childId ID of the child.
     * @property familyId ID of the family.
     */
    data class Placement(val id: Int, val type: String?, val date: String, val childId: Int?, val familyId: Int?) : SearchResult()

    /**
     * Search result for a financial record.
     * @property id Unique identifier.
     * @property amount Transaction amount.
     * @property receipt Masked or full receipt number.
     * @property date Transaction date.
     * @property childId ID of the related child.
     */
    data class Finance(val id: Int, val amount: Double, val receipt: String?, val date: String, val childId: Int?) : SearchResult()

    /**
     * Search result for an adoption application.
     * @property id Unique identifier.
     * @property status Current application status.
     * @property familyId ID of the applying family.
     * @property childId ID of the child.
     * @property appNumber Unique application number.
     */
    data class Application(val id: Int, val status: String, val familyId: Int, val childId: Int?, val appNumber: String?) : SearchResult()

    /**
     * Search result for a document.
     * @property id Unique identifier.
     * @property name File name.
     * @property type Type of document.
     * @property childId ID of the related child.
     */
    data class Document(val id: Int, val name: String, val type: String?, val childId: Int?) : SearchResult()

    /**
     * Search result for a court case.
     * @property id Unique identifier.
     * @property caseNumber Official court case number.
     * @property type Type of legal proceeding.
     * @property hearingDate Scheduled hearing date.
     * @property status Current legal status.
     */
    data class CourtCase(val id: Int, val caseNumber: String, val type: String?, val hearingDate: String?, val status: String?) : SearchResult()

    /**
     * Search result for a guardian.
     * @property id Unique identifier.
     * @property name Full name.
     * @property relationship Relationship to the child.
     * @property phone Contact phone number.
     * @property childId ID of the related child.
     */
    data class Guardian(val id: Int, val name: String, val relationship: String?, val phone: String?, val childId: Int?) : SearchResult()

    /**
     * Search result for a medical record.
     * @property id Unique identifier.
     * @property childId ID of the related child.
     * @property diagnosis Medical diagnosis.
     * @property hospital Hospital or clinic name.
     * @property visitDate Date of medical visit.
     */
    data class MedicalRecord(val id: Int, val childId: Int, val diagnosis: String?, val hospital: String?, val visitDate: String?) : SearchResult()

    /**
     * Search result for an education record.
     * @property id Unique identifier.
     * @property childId ID of the related child.
     * @property school Name of school.
     * @property grade Current grade or level.
     * @property year Academic year.
     */
    data class EducationRecord(val id: Int, val childId: Int, val school: String?, val grade: String?, val year: String?) : SearchResult()

    /**
     * Search result for a home study.
     * @property id Unique identifier.
     * @property familyId ID of the family.
     * @property result Outcome of the study.
     * @property startDate Date study commenced.
     * @property worker Assigned social worker ID string.
     */
    data class HomeStudy(val id: Int, val familyId: Int, val result: String?, val startDate: String?, val worker: String?) : SearchResult()

    /**
     * Search result for a foster task.
     * @property id Unique identifier.
     * @property description Task description.
     * @property status Current task status.
     * @property dueDate Task deadline.
     * @property familyId ID of the related family.
     */
    data class FosterTask(val id: Int, val description: String?, val status: String?, val dueDate: String?, val familyId: Int?) : SearchResult()

    /**
     * Search result for a foster match.
     * @property id Unique identifier.
     * @property childId ID of the child.
     * @property familyId ID of the family.
     * @property status Compatibility status.
     * @property matchedAt Date match was recorded.
     */
    data class FosterMatch(val id: Int, val childId: Int?, val familyId: Int?, val status: String?, val matchedAt: String?) : SearchResult()

    /**
     * Search result for a background check.
     * @property id Unique identifier.
     * @property familyId ID of the user/family member.
     * @property type Type of background check.
     * @property status Investigation status.
     * @property completedAt Date check was completed.
     */
    data class BackgroundCheck(val id: Int, val familyId: Int, val type: String?, val status: String?, val completedAt: String?) : SearchResult()

    /**
     * Search result for a case report.
     * @property id Unique identifier.
     * @property childId ID of the child.
     * @property title Report title.
     * @property reportDate Date report was filed.
     * @property author Author user ID string.
     */
    data class CaseReport(val id: Int, val childId: Int, val title: String?, val reportDate: String?, val author: String?) : SearchResult()

    /**
     * Search result for a task.
     * @property id Unique identifier.
     * @property title Task title.
     * @property priority Task priority level.
     * @property status Current completion status.
     * @property dueDate Task deadline.
     */
    data class Task(val id: Int, val title: String, val priority: String?, val status: String?, val dueDate: String?) : SearchResult()

    /**
     * Search result for a notification.
     * @property id Unique identifier.
     * @property title Notification title.
     * @property message Notification content.
     * @property type Category of notification.
     * @property createdAt Date notification was sent.
     */
    data class Notification(val id: Int, val title: String, val message: String?, val type: String?, val createdAt: String?) : SearchResult()
}

/**
 * ViewModel for global search functionality across the application.
 * 
 * This ViewModel performs parallel searches across multiple database tables,
 * applies role-based access control filters, and masks sensitive data
 * for non-admin users.
 * 
 * @param application Application context for database access.
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val session = SessionManager(application)

    /**
     * Flow containing the list of search results.
     */
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    /**
     * Flow indicating whether a search operation is currently in progress.
     */
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    /**
     * Performs a global search across all database tables using the provided query.
     * 
     * @param query The search string provided by the user.
     */
    fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        // Handle phone normalization for Kenya
        val normalizedQuery = if (query.startsWith("0") || query.startsWith("+254")) {
            "%" + normalizePhone(query) + "%"
        } else {
            "%$query%"
        }
        
        @Suppress("HardcodedStringLiteral")
        val userRole = session.getRole() ?: "User"
        @Suppress("HardcodedStringLiteral")
        val isAdmin = userRole == "Admin"
        val userCounty = session.getCounty()

        viewModelScope.launch {
            _isSearching.value = true
            
            try {
                // Parallel search across ALL 17 database tables
                val childrenDeferred = async { db.childDao().globalSearch(normalizedQuery) }
                val familiesDeferred = async { db.familyDao().globalSearch(normalizedQuery) }
                val usersDeferred = async { db.userDao().globalSearch(normalizedQuery) }
                val placementsDeferred = async { db.placementDao().globalSearch(normalizedQuery) }
                val financeDeferred = async { db.moneyRecordDao().searchByReceipt(normalizedQuery) }
                val appsDeferred = async { db.adoptionApplicationDao().globalSearch(normalizedQuery) }
                val docsDeferred = async { db.documentDao().globalSearch(normalizedQuery) }
                
                // Additional tables
                val courtCasesDeferred = async { 
                    try { db.courtCaseDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val guardiansDeferred = async { 
                    try { db.guardianDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val medicalDeferred = async { 
                    try { db.medicalRecordDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val educationDeferred = async { 
                    try { db.educationRecordDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val homeStudiesDeferred = async { 
                    try { db.homeStudyDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val fosterTasksDeferred = async { 
                    try { db.fosterTaskDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val fosterMatchesDeferred = async { 
                    try { db.fosterMatchDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val backgroundChecksDeferred = async { 
                    try { db.backgroundCheckDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val caseReportsDeferred = async { 
                    try { db.caseReportDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val tasksDeferred = async { 
                    try { db.taskDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }
                val notificationsDeferred = async { 
                    try { db.notificationDao().globalSearch(normalizedQuery) } catch (_: Exception) { emptyList() }
                }

                // Await all results
                val children = childrenDeferred.await()
                val families = familiesDeferred.await()
                val users = usersDeferred.await()
                val placements = placementsDeferred.await()
                val finance = financeDeferred.await()
                val apps = appsDeferred.await()
                val docs = docsDeferred.await()
                val courtCases = courtCasesDeferred.await()
                val guardians = guardiansDeferred.await()
                val medical = medicalDeferred.await()
                val education = educationDeferred.await()
                val homeStudies = homeStudiesDeferred.await()
                val fosterTasks = fosterTasksDeferred.await()
                val fosterMatches = fosterMatchesDeferred.await()
                val backgroundChecks = backgroundChecksDeferred.await()
                val caseReports = caseReportsDeferred.await()
                val tasks = tasksDeferred.await()
                val notifications = notificationsDeferred.await()

                val results = mutableListOf<SearchResult>()

                // Map and Filter results by county if required (Legal req #25)
                children.forEach {
                    if (isAdmin || it.county == userCounty) {
                        results.add(SearchResult.Child(it.childId, "${it.firstName} ${it.lastName}", it.birthCertificateNo, it.county, it.caseNumber))
                    }
                }

                families.forEach {
                    if (isAdmin || it.county == userCounty) {
                        val maskedId = if (isAdmin) it.nationalIdNo else maskSensitive(it.nationalIdNo)
                        results.add(SearchResult.Family(it.familyId, it.primaryContactName, it.phone, maskedId, it.county))
                    }
                }

                users.forEach {
                    // Sensitive field masking #8
                    if (isAdmin || it.county == userCounty) {
                        val maskedId = if (isAdmin) it.nationalIdNo else maskSensitive(it.nationalIdNo)
                        results.add(SearchResult.User(it.userId, it.username, it.role, it.phone, maskedId, it.county))
                    }
                }

                placements.forEach {
                    results.add(SearchResult.Placement(it.placementId, it.placementType, it.startDate, it.childId, it.destinationFamilyId))
                }

                finance.forEach {
                    // Sensitive field masking #20
                    val maskedReceipt = if (isAdmin) it.mpesaReceiptNo else maskSensitive(it.mpesaReceiptNo)
                    results.add(SearchResult.Finance(it.moneyId, it.amount, maskedReceipt, it.date, it.childId))
                }

                finance.forEach {
                    // Sensitive field masking #20
                    val maskedReceipt = if (isAdmin) it.mpesaReceiptNo else maskSensitive(it.mpesaReceiptNo)
                    results.add(SearchResult.Finance(it.moneyId, it.amount, maskedReceipt, it.date, it.childId))
                }

                apps.forEach {
                    results.add(SearchResult.Application(it.applicationId, it.status ?: "Pending", it.familyId, it.childId, it.applicationNumber))
                }

                docs.forEach {
                    results.add(SearchResult.Document(it.documentId, it.fileName, it.documentType, it.childId))
                }

                // Additional table results
                courtCases.forEach {
                    results.add(SearchResult.CourtCase(
                        it.caseId, 
                        it.caseNumber ?: "Case #${it.caseId}",
                        it.caseType,
                        it.hearingDate,
                        it.status
                    ))
                }

                guardians.forEach {
                    if (isAdmin) {
                        results.add(SearchResult.Guardian(
                            it.guardianId,
                            "${it.firstName} ${it.lastName}",
                            it.relationship,
                            it.phone,
                            it.childId
                        ))
                    }
                }

                medical.forEach { record ->
                    results.add(SearchResult.MedicalRecord(
                        record.recordId,
                        record.childId,
                        record.diagnosis,
                        record.hospitalName,
                        record.visitDate
                    ))
                }

                education.forEach { record ->
                    results.add(SearchResult.EducationRecord(
                        record.recordId,
                        record.childId,
                        record.schoolName,
                        record.grade,
                        record.enrollmentDate
                    ))
                }

                homeStudies.forEach { study ->
                    results.add(SearchResult.HomeStudy(
                        study.homeStudyId,
                        study.familyId,
                        study.result,
                        study.startedAt,
                        study.socialWorkerId.toString()
                    ))
                }

                fosterTasks.forEach { task ->
                    results.add(SearchResult.FosterTask(
                        task.taskId,
                        task.description,
                        task.status,
                        task.dueDate,
                        task.familyId
                    ))
                }

                fosterMatches.forEach { match ->
                    results.add(SearchResult.FosterMatch(
                        match.matchId,
                        match.childId,
                        match.familyId,
                        match.status,
                        match.matchedAt
                    ))
                }

                backgroundChecks.forEach { check ->
                    results.add(SearchResult.BackgroundCheck(
                        check.checkId,
                        check.userId,
                        check.result,
                        check.status,
                        check.completedAt
                    ))
                }

                caseReports.forEach { report ->
                    results.add(SearchResult.CaseReport(
                        report.reportId,
                        report.childId,
                        report.reportTitle,
                        report.reportDate,
                        report.userId.toString()
                    ))
                }

                tasks.forEach { task ->
                    results.add(SearchResult.Task(
                        task.taskId,
                        task.title,
                        task.priority,
                        task.status,
                        task.dueDate
                    ))
                }

                notifications.forEach { notif ->
                    results.add(SearchResult.Notification(
                        notif.notificationId,
                        notif.title,
                        notif.message,
                        null, // type field doesn't exist in entity
                        notif.createdAt
                    ))
                }

                _searchResults.value = results
            } catch (e: Exception) {
                // Log error but don't crash
                e.printStackTrace()
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    /**
     * Masks sensitive data by replacing all but the last 4 characters with asterisks.
     * 
     * @param value The sensitive string to mask.
     * @return The masked string, or the original string if it is too short to mask effectively.
     */
    private fun maskSensitive(value: String?): String? {
        if (value == null || value.length <= 4) return value
        return "***" + value.takeLast(4)
    }

    /**
     * Normalizes a phone number to international format (+254) for Kenyan numbers.
     * 
     * @param phone The phone number string to normalize.
     * @return The normalized phone number.
     */
    private fun normalizePhone(phone: String): String {
        return if (phone.startsWith("0")) {
            "+254" + phone.substring(1)
        } else {
            phone
        }
    }
}
