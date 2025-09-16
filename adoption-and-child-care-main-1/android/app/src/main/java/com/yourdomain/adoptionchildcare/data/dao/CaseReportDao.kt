package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.CaseReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseReportDao {
    @Query("SELECT * FROM case_reports WHERE child_id = :childId ORDER BY report_date DESC")
    fun observeForChild(childId: Int): Flow<List<CaseReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: CaseReportEntity)
}
