package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.PlacementDao
import com.adoptionapp.data.entity.Placement
import com.adoptionapp.network.PlacementApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlacementRepository(
    private val placementDao: PlacementDao,
    private val placementApi: PlacementApi,
    private val syncManager: SyncManager
) {
    val allPlacements: LiveData<List<Placement>> = placementDao.getAllPlacements()

    suspend fun insert(placement: Placement) {
        withContext(Dispatchers.IO) {
            placementDao.insert(placement)
            syncManager.scheduleSyncPlacements()
        }
    }

    suspend fun update(placement: Placement) {
        withContext(Dispatchers.IO) {
            placementDao.update(placement)
            syncManager.scheduleSyncPlacements()
        }
    }

    suspend fun delete(placement: Placement) {
        withContext(Dispatchers.IO) {
            placementDao.delete(placement)
            syncManager.scheduleSyncPlacements()
        }
    }

    suspend fun syncPlacements() {
        withContext(Dispatchers.IO) {
            try {
                val remotePlacements = placementApi.getPlacements()
                placementDao.replaceAll(remotePlacements)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getPlacementById(id: Int): Placement? {
        return withContext(Dispatchers.IO) {
            placementDao.getPlacementById(id)
        }
    }

    suspend fun getPlacementsByChildId(childId: Int): List<Placement> {
        return withContext(Dispatchers.IO) {
            placementDao.getPlacementsByChildId(childId)
        }
    }
} 