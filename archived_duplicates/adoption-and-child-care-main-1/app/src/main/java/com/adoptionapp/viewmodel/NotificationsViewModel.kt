package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(private val repository: NotificationsRepository) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadNotifications() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _notifications.value = repository.getNotifications()
                _unreadCount.value = repository.getUnreadCount()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }
    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.markAsRead(notificationId)
                loadNotifications()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }
    fun markAllAsRead() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.markAllAsRead()
                loadNotifications()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }
} 