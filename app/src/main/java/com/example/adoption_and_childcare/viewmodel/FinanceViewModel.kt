package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.MoneyRecordRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing financial records and related operations.
 */
@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val moneyRecordRepository: MoneyRecordRepositoryImpl,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val moneyRecords = moneyRecordRepository.observeAll()
    val children = childRepository.observeAll()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refreshFromApi()
    }

    fun refreshFromApi() {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: return@launch
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = moneyRecordRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertMoneyRecord(record: MoneyRecordEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            moneyRecordRepository.insert(record, token)
        }
    }

    fun updateMoneyRecord(record: MoneyRecordEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            moneyRecordRepository.update(record, token)
        }
    }

    fun deleteMoneyRecord(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            moneyRecordRepository.delete(id, token)
        }
    }
}
