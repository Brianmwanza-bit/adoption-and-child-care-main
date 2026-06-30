package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the children list and child-related operations.
 */
/**
 * ViewModel for managing the list of children and child-related operations.
 * 
 * This class provides access to child data through [ChildRepository] and manages
 * the UI state for loading, errors, and child records.
 * 
 * @property childRepository Repository for child data operations.
 * @property authManager Manager for authentication state.
 */
@HiltViewModel
class ChildrenViewModel @Inject constructor(
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    /** Observable flow of all children records. */
    val children = childRepository.observeAll()

    private val _isLoading = MutableStateFlow(false)
    /** Observable state indicating if a data operation is in progress. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    /** Observable state for error messages. */
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refreshFromApi()
    }

    /**
     * Refreshes the children data from the remote API.
     */
    fun refreshFromApi() {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: return@launch
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = childRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    /**
     * Inserts a new child record.
     * 
     * @param child The child entity to insert.
     */
    fun insertChild(child: ChildEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            childRepository.insert(child, token)
        }
    }

    /**
     * Updates an existing child record.
     * 
     * @param child The child entity to update.
     */
    fun updateChild(child: ChildEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            childRepository.update(child, token)
        }
    }

    /**
     * Deletes a child record by its ID.
     * 
     * @param id The ID of the child to delete.
     */
    fun deleteChild(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            childRepository.delete(id, token)
        }
    }

    /**
     * Searches for children by name.
     * 
     * @param query The search query.
     * @return A Flow containing the list of matching children.
     */
    fun search(query: String) = childRepository.searchByName(query)
}
