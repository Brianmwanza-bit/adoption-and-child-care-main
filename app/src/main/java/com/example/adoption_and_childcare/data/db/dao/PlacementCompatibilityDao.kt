package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.PlacementCompatibilityEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacementCompatibilityDao {
    @Query("SELECT * FROM placement_compatibility")
    fun observeAll(): Flow<List<PlacementCompatibilityEntity>>

    @Query("SELECT * FROM placement_compatibility")
    suspend fun getAll(): List<PlacementCompatibilityEntity>

    @Query("SELECT * FROM placement_compatibility WHERE compatibility_id = :id")
    suspend fun getById(id: Int): PlacementCompatibilityEntity?

    @Query("SELECT * FROM placement_compatibility WHERE child_id = :childId")
    suspend fun getByChildId(childId: Int): List<PlacementCompatibilityEntity>

    @Query("SELECT * FROM placement_compatibility WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<PlacementCompatibilityEntity>

    @Query("SELECT * FROM placement_compatibility WHERE child_id = :childId AND family_id = :familyId LIMIT 1")
    suspend fun getByChildAndFamily(childId: Int, familyId: Int): PlacementCompatibilityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(compatibility: PlacementCompatibilityEntity): Long

    @Transaction
    suspend fun insertWithSync(compatibility: PlacementCompatibilityEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(compatibility)
        val payload = Gson().toJson(compatibility)
        syncQueueDao.insert(SyncQueueEntity(tableName = "placement_compatibility", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(compatibilities: List<PlacementCompatibilityEntity>)

    @Update
    suspend fun update(compatibility: PlacementCompatibilityEntity)

    @Transaction
    suspend fun updateWithSync(compatibility: PlacementCompatibilityEntity, syncQueueDao: SyncQueueDao) {
        update(compatibility)
        val payload = Gson().toJson(compatibility)
        syncQueueDao.insert(SyncQueueEntity(tableName = "placement_compatibility", operation = "UPDATE", recordId = compatibility.compatibilityId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(compatibility: PlacementCompatibilityEntity)

    @Query("DELETE FROM placement_compatibility WHERE compatibility_id = :id")
    suspend fun deleteById(id: Int)
}
