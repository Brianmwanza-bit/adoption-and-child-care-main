package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import com.example.adoption_and_childcare.data.repository.SystemSettingRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing application settings with Room as primary storage and API as secondary.
 * Supports reactive UI updates and "live" sync when online.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SystemSettingRepositoryImpl,
    private val authManager: AuthManager
) : ViewModel() {

    /**
     * Flow of all system settings from Room database.
     */
    val settingsFlow: StateFlow<List<SystemSettingEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Updates a single setting in Room and attempts live sync with remote API.
     * 
     * @param key Unique key for the setting.
     * @param value String representation of the setting value.
     * @param category Category for grouping settings.
     */
    fun saveSetting(key: String, value: String?, category: String) {
        viewModelScope.launch {
            val existing = settingsFlow.value.find { it.settingKey == key }
            val token = authManager.getAuthToken() ?: ""
            
            if (existing != null) {
                repository.update(existing.copy(settingValue = value, category = category, updatedAt = System.currentTimeMillis().toString()), token)
            } else {
                repository.insert(SystemSettingEntity(settingKey = key, settingValue = value, category = category, createdAt = System.currentTimeMillis().toString()), token)
            }
        }
    }

    /**
     * Resets all system settings by deleting them from the local database.
     */
    fun resetSettings() {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            settingsFlow.value.forEach { 
                repository.delete(it.settingId, token)
            }
        }
    }
}
