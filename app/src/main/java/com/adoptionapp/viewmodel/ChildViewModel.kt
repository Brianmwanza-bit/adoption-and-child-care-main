package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.Child
import com.adoptionapp.repository.ChildRepository
import kotlinx.coroutines.launch

class ChildViewModel(private val repository: ChildRepository) : ViewModel() {
    val children: LiveData<List<Child>> = repository.allChildren

    fun addChild(child: Child) {
        viewModelScope.launch {
            repository.insert(child)
        }
    }

    fun updateChild(child: Child) {
        viewModelScope.launch {
            repository.update(child)
        }
    }

    fun deleteChild(child: Child) {
        viewModelScope.launch {
            repository.delete(child)
        }
    }

    fun syncChildren() {
        viewModelScope.launch {
            repository.syncChildren()
        }
    }

    fun getChildById(id: Int): LiveData<Child?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return children
    }
} 