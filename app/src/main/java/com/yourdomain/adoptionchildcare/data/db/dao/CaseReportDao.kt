package com.yourdomain.adoptionchildcare.data.db.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.db.entities.CaseReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CaseReportEntity): Long

    @Update
    suspend fun update(entity: CaseReportEntity)

    @Query("DELETE FROM case_reports WHERE report_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM case_reports WHERE child_id = :childId ORDER BY report_date DESC")
    fun observeForChild(childId: Int): Flow<List<CaseReportEntity>>

    @Query("SELECT COUNT(*) FROM case_reports")
    suspend fun count(): Int

    @Query("SELECT * FROM case_reports ORDER BY report_date DESC")
    fun observeAll(): Flow<List<CaseReportEntity>>
}
