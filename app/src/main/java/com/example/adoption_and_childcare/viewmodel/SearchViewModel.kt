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
    data class Child(val id: Int, val name: String, val birthCert: String?, val county: String?) : SearchResult()
    data class Family(val id: Int, val name: String, val phone: String?, val nationalId: String?, val county: String?) : SearchResult()
    data class User(val id: Int, val username: String, val role: String, val phone: String?, val nationalId: String?, val county: String?) : SearchResult()
    data class Placement(val id: Int, val type: String?, val date: String) : SearchResult()
    data class Finance(val id: Int, val amount: Double, val receipt: String?, val date: String) : SearchResult()
    data class Application(val id: Int, val status: String, val familyId: Int) : SearchResult()
    data class Document(val id: Int, val name: String, val type: String?) : SearchResult()
}

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
            
            // Parallel search across tables (7 categories)
            val childrenDeferred = async { db.childDao().globalSearch(normalizedQuery) }
            val familiesDeferred = async { db.familyDao().globalSearch(normalizedQuery) }
            val usersDeferred = async { db.userDao().globalSearch(normalizedQuery) }
            val placementsDeferred = async { db.placementDao().globalSearch(normalizedQuery) }
            val financeDeferred = async { db.moneyRecordDao().searchByReceipt(normalizedQuery) }
            val appsDeferred = async { db.adoptionApplicationDao().globalSearch(normalizedQuery) }
            val docsDeferred = async { db.documentDao().globalSearch(normalizedQuery) }

            val children = childrenDeferred.await()
            val families = familiesDeferred.await()
            val users = usersDeferred.await()
            val placements = placementsDeferred.await()
            val finance = financeDeferred.await()
            val apps = appsDeferred.await()
            val docs = docsDeferred.await()

            val results = mutableListOf<SearchResult>()

            // Map and Filter results by county if required (Legal req #25)
            children.forEach {
                if (isAdmin || it.county == userCounty) {
                    results.add(SearchResult.Child(it.childId, "${it.firstName} ${it.lastName}", it.birthCertificateNo, it.county))
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
                results.add(SearchResult.Placement(it.placementId, it.placementType, it.startDate))
            }

            finance.forEach {
                // Sensitive field masking #20
                val maskedReceipt = if (isAdmin) it.mpesaReceiptNo else maskSensitive(it.mpesaReceiptNo)
                results.add(SearchResult.Finance(it.moneyId, it.amount, maskedReceipt, it.date))
            }

            apps.forEach {
                results.add(SearchResult.Application(it.applicationId, it.status ?: "Pending", it.familyId))
            }

            docs.forEach {
                results.add(SearchResult.Document(it.documentId, it.fileName, it.documentType))
            }

            _searchResults.value = results
            _isSearching.value = false
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
