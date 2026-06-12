package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

@Dao
abstract class HomeStudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: HomeStudyEntity): Long

    @Update
    abstract suspend fun update(entity: HomeStudyEntity)

    @Transaction
    open suspend fun insertWithSync(entity: HomeStudyEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity.copy(homeStudyId = id.toInt()))
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "home_studies",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    open suspend fun updateWithSync(entity: HomeStudyEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "home_studies",
                operation = "UPDATE",
                recordId = entity.homeStudyId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM home_studies WHERE home_study_id = :id")
    abstract suspend fun deleteById(id: Int)

    @Transaction
    open suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "home_studies",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM home_studies WHERE family_id = :familyId ORDER BY started_at DESC")
    abstract fun observeForFamily(familyId: Int): Flow<List<HomeStudyEntity>>

    @Query("SELECT COUNT(*) FROM home_studies")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM home_studies ORDER BY started_at DESC")
    abstract fun observeAll(): Flow<List<HomeStudyEntity>>

    @Query("SELECT * FROM home_studies WHERE completed_at IS NULL ORDER BY started_at ASC")
    abstract fun observeUpcoming(): Flow<List<HomeStudyEntity>>
}
