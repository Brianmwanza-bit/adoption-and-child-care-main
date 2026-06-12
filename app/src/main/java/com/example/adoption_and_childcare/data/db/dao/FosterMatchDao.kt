package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface FosterMatchDao {
    @Query("SELECT * FROM foster_matches")
    fun observeAll(): Flow<List<FosterMatchEntity>>

    @Query("SELECT * FROM foster_matches")
    suspend fun getAll(): List<FosterMatchEntity>

    @Query("SELECT * FROM foster_matches WHERE match_id = :id")
    suspend fun getById(id: Int): FosterMatchEntity?

    @Query("SELECT * FROM foster_matches WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<FosterMatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fosterMatch: FosterMatchEntity): Long

    @Transaction
    suspend fun insertWithSync(fosterMatch: FosterMatchEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(fosterMatch)
        val payload = Gson().toJson(fosterMatch)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "foster_matches",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fosterMatches: List<FosterMatchEntity>)

    @Update
    suspend fun update(fosterMatch: FosterMatchEntity)

    @Transaction
    suspend fun updateWithSync(fosterMatch: FosterMatchEntity, syncQueueDao: SyncQueueDao) {
        update(fosterMatch)
        val payload = Gson().toJson(fosterMatch)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "foster_matches",
                operation = "UPDATE",
                recordId = fosterMatch.matchId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Delete
    suspend fun delete(fosterMatch: FosterMatchEntity)

    @Query("DELETE FROM foster_matches WHERE match_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "foster_matches",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
