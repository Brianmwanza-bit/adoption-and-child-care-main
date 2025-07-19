package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.FosterTasksEntity
import com.adoptionapp.FosterTasksRepository
import kotlinx.coroutines.launch

class FosterTasksViewModel(private val repository: FosterTasksRepository) : ViewModel() {
    private val _tasks = MutableLiveData<List<FosterTasksEntity>>(emptyList())
    val tasks: LiveData<List<FosterTasksEntity>> = _tasks

    fun loadTasks() {
        viewModelScope.launch {
            _tasks.postValue(repository.getAll())
        }
    }

    fun addTask(task: FosterTasksEntity) {
        viewModelScope.launch {
            repository.insert(task)
            loadTasks()
        }
    }

    fun updateTask(task: FosterTasksEntity) {
        viewModelScope.launch {
            repository.update(task)
            loadTasks()
        }
    }

    fun deleteTask(task: FosterTasksEntity) {
        viewModelScope.launch {
            repository.delete(task)
            loadTasks()
        }
    }
} 