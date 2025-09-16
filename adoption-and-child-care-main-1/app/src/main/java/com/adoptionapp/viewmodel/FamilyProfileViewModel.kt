package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.FamilyProfileEntity
import com.adoptionapp.FamilyProfileRepository
import kotlinx.coroutines.launch

class FamilyProfileViewModel(private val repository: FamilyProfileRepository) : ViewModel() {
    private val _profiles = MutableLiveData<List<FamilyProfileEntity>>(emptyList())
    val profiles: LiveData<List<FamilyProfileEntity>> = _profiles

    fun loadProfiles() {
        viewModelScope.launch {
            _profiles.postValue(repository.getAll())
        }
    }

    fun addProfile(profile: FamilyProfileEntity) {
        viewModelScope.launch {
            repository.insert(profile)
            loadProfiles()
        }
    }

    fun updateProfile(profile: FamilyProfileEntity) {
        viewModelScope.launch {
            repository.update(profile)
            loadProfiles()
        }
    }

    fun deleteProfile(profile: FamilyProfileEntity) {
        viewModelScope.launch {
            repository.delete(profile)
            loadProfiles()
        }
    }
} 