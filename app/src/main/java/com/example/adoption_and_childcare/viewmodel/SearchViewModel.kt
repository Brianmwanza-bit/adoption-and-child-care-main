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

sealed class SearchResult {
    data class Child(val id: Int, val name: String, val birthCert: String?, val county: String?, val caseNumber: String?) : SearchResult()
    data class Family(val id: Int, val name: String, val phone: String?, val nationalId: String?, val county: String?) : SearchResult()
    data class User(val id: Int, val username: String, val role: String, val phone: String?, val nationalId: String?, val county: String?) : SearchResult()
    data class Placement(val id: Int, val type: String?, val date: String, val childId: Int?, val familyId: Int?) : SearchResult()
    data class Finance(val id: Int, val amount: Double, val receipt: String?, val date: String, val childId: Int?) : SearchResult()
    data class Application(val id: Int, val status: String, val familyId: Int, val childId: Int?, val appNumber: String?) : SearchResult()
    data class Document(val id: Int, val name: String, val type: String?, val childId: Int?) : SearchResult()
    data class CourtCase(val id: Int, val caseNumber: String, val type: String?, val hearingDate: String?, val status: String?) : SearchResult()
    data class Guardian(val id: Int, val name: String, val relationship: String?, val phone: String?, val childId: Int?) : SearchResult()
    data class MedicalRecord(val id: Int, val childId: Int, val diagnosis: String?, val hospital: String?, val visitDate: String?) : SearchResult()
    data class EducationRecord(val id: Int, val childId: Int, val school: String?, val grade: String?, val year: String?) : SearchResult()
    data class HomeStudy(val id: Int, val familyId: Int, val result: String?, val startDate: String?, val worker: String?) : SearchResult()
    data class FosterTask(val id: Int, val description: String?, val status: String?, val dueDate: String?, val familyId: Int?) : SearchResult()
    data class FosterMatch(val id: Int, val childId: Int?, val familyId: Int?, val status: String?, val matchedAt: String?) : SearchResult()
    data class BackgroundCheck(val id: Int, val familyId: Int, val type: String?, val status: String?, val completedAt: String?) : SearchResult()
    data class CaseReport(val id: Int, val childId: Int, val title: String?, val reportDate: String?, val author: String?) : SearchResult()
    data class Task(val id: Int, val title: String, val priority: String?, val status: String?, val dueDate: String?) : SearchResult()
    data class Notification(val id: Int, val title: String, val message: String?, val type: String?, val createdAt: String?) : SearchResult()
}

/**
 * ViewModel for global search functionality across the application.
 * 
 * This ViewModel performs parallel searches across multiple database tables,
 * applies role-based access control filters, and masks sensitive data
 * for non-admin users.
 * 
 * @property application Application context for database access.
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val session = SessionManager(application)

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

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
        
        val userRole = session.getRole() ?: "User"
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
                    try { db.courtCaseDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val guardiansDeferred = async { 
                    try { db.guardianDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val medicalDeferred = async { 
                    try { db.medicalRecordDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val educationDeferred = async { 
                    try { db.educationRecordDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val homeStudiesDeferred = async { 
                    try { db.homeStudyDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val fosterTasksDeferred = async { 
                    try { db.fosterTaskDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val fosterMatchesDeferred = async { 
                    try { db.fosterMatchDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val backgroundChecksDeferred = async { 
                    try { db.backgroundCheckDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val caseReportsDeferred = async { 
                    try { db.caseReportDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val tasksDeferred = async { 
                    try { db.taskDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
                }
                val notificationsDeferred = async { 
                    try { db.notificationDao().globalSearch(normalizedQuery) } catch (e: Exception) { emptyList() }
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
                    if (isAdmin || it.county == userCounty) {
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
                        study.studyId,
                        study.familyId,
                        study.result,
                        study.startedAt,
                        study.conductedBy?.toString()
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
                        report.userId?.toString()
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

    private fun maskSensitive(value: String?): String? {
        if (value == null || value.length <= 4) return value
        return "***" + value.takeLast(4)
    }

    // Phone normalization helper for Kenya #7
    private fun normalizePhone(phone: String): String {
        return if (phone.startsWith("0")) {
            "+254" + phone.substring(1)
        } else {
            phone
        }
    }
}
