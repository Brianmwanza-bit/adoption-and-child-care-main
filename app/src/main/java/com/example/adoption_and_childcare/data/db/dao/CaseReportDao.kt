package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CaseReportEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: CaseReportEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "case_reports",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: CaseReportEntity)

    @Transaction
    suspend fun updateWithSync(entity: CaseReportEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "case_reports",
                operation = "UPDATE",
                recordId = entity.reportId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM case_reports WHERE report_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "case_reports",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM case_reports WHERE child_id = :childId ORDER BY report_date DESC")
    fun observeForChild(childId: Int): Flow<List<CaseReportEntity>>

    @Query("SELECT * FROM case_reports WHERE report_id = :id")
    suspend fun findById(id: Int): CaseReportEntity?

    @Query("SELECT COUNT(*) FROM case_reports")
    suspend fun count(): Int

    @Query("SELECT * FROM case_reports ORDER BY report_date DESC")
    fun observeAll(): Flow<List<CaseReportEntity>>
    
    @Query("""
        SELECT * FROM case_reports 
        WHERE report_title LIKE :query 
           OR content LIKE :query
           OR report_date LIKE :query
           OR report_type LIKE :query
        ORDER BY report_date DESC
    """)
    suspend fun globalSearch(query: String): List<CaseReportEntity>
}
