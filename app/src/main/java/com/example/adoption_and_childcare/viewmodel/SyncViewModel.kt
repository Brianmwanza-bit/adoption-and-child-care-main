package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.sync.SyncManager
import com.example.adoption_and_childcare.data.sync.SyncResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SyncState { ONLINE_IDLE, ONLINE_PENDING, SYNCING, OFFLINE, ERROR }

data class SyncStatus(
    val state: SyncState,
    val pendingCount: Int = 0,
    val lastSyncedAt: Long? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncManager: SyncManager,
    private val syncQueueDao: SyncQueueDao
) : ViewModel() {
    private val _syncStatus = MutableStateFlow(SyncStatus(SyncState.ONLINE_IDLE))
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    init {
        viewModelScope.launch {
            syncQueueDao.getPendingCount().collectLatest { count ->
                _syncStatus.value = _syncStatus.value.copy(
                    pendingCount = count,
                    state = if (count > 0 && _syncStatus.value.state != SyncState.SYNCING) 
                        SyncState.ONLINE_PENDING else _syncStatus.value.state
                )
            }
        }
    }

    fun triggerSync() {
        if (_syncStatus.value.state == SyncState.SYNCING) return

        viewModelScope.launch {
            _syncStatus.value = _syncStatus.value.copy(state = SyncState.SYNCING)
            val result = syncManager.sync()
            when (result) {
                is SyncResult.Success -> {
                    _syncStatus.value = _syncStatus.value.copy(
                        state = SyncState.ONLINE_IDLE,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                }
                is SyncResult.Error -> {
                    _syncStatus.value = _syncStatus.value.copy(
                        state = SyncState.ERROR,
                        errorMessage = result.message
                    )
                }
                is SyncResult.Offline -> {
                    _syncStatus.value = _syncStatus.value.copy(state = SyncState.OFFLINE)
                }
            }
        }
    }
}
