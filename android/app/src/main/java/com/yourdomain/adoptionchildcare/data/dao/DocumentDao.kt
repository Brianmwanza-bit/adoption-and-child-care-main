package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE child_id = :childId ORDER BY uploaded_at DESC")
    fun observeForChild(childId: Int): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY uploaded_at DESC")
    suspend fun getAllDocuments(): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: DocumentEntity)
}
