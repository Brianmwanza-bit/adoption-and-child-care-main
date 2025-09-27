package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.ChildrenEntity
import com.adoptionapp.repository.ChildRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChildViewModel(private val repository: ChildRepository) : ViewModel() {
    val children: LiveData<List<ChildrenEntity>> = repository.allChildren
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addChild(child: ChildrenEntity) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(child)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updateChild(child: ChildrenEntity) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(child)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deleteChild(child: ChildrenEntity) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(child)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncChildren() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncChildren()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getChildById(id: Int): LiveData<ChildrenEntity?> {
        return children
    }

    fun addChildWithPhoto(child: ChildrenEntity, photo: ByteArray?) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insertWithPhoto(child, photo)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }
} 