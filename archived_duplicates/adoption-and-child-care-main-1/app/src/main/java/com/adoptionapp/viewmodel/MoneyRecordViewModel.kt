package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.MoneyRecord
import com.adoptionapp.repository.MoneyRecordRepository
import kotlinx.coroutines.launch

class MoneyRecordViewModel(private val repository: MoneyRecordRepository) : ViewModel() {
    val moneyRecords: LiveData<List<MoneyRecord>> = repository.allMoneyRecords

    fun addMoneyRecord(moneyRecord: MoneyRecord) {
        viewModelScope.launch {
            repository.insert(moneyRecord)
        }
    }

    fun updateMoneyRecord(moneyRecord: MoneyRecord) {
        viewModelScope.launch {
            repository.update(moneyRecord)
        }
    }

    fun deleteMoneyRecord(moneyRecord: MoneyRecord) {
        viewModelScope.launch {
            repository.delete(moneyRecord)
        }
    }

    fun syncMoneyRecords() {
        viewModelScope.launch {
            repository.syncMoneyRecords()
        }
    }

    fun getMoneyRecordById(id: Int): LiveData<MoneyRecord?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return moneyRecords
    }

    fun getMoneyRecordsByChildId(childId: Int): LiveData<List<MoneyRecord>> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return moneyRecords
    }
} 