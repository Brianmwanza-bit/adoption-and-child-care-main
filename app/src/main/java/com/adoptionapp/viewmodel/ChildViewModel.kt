package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.ChildrenEntity
import com.adoptionapp.repository.ChildRepository
import kotlinx.coroutines.launch

class ChildViewModel(private val repository: ChildRepository) : ViewModel() {
    val children: LiveData<List<ChildrenEntity>> = repository.allChildren

    fun addChild(child: ChildrenEntity) {
        viewModelScope.launch {
            repository.insert(child)
        }
    }

    fun updateChild(child: ChildrenEntity) {
        viewModelScope.launch {
            repository.update(child)
        }
    }

    fun deleteChild(child: ChildrenEntity) {
        viewModelScope.launch {
            repository.delete(child)
        }
    }

    fun syncChildren() {
        viewModelScope.launch {
            repository.syncChildren()
        }
    }

    fun getChildById(id: Int): LiveData<ChildrenEntity?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return children
    }
} 