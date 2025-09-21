package com.yourdomain.adoptionchildcare.data.db.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.db.entities.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: DocumentEntity): Long

    @Update
    suspend fun update(doc: DocumentEntity)

    @Query("DELETE FROM documents WHERE document_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM documents WHERE child_id = :childId ORDER BY uploaded_at DESC")
    fun observeByChild(childId: Int): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY uploaded_at DESC")
    fun observeAll(): Flow<List<DocumentEntity>>

    @Query("SELECT COUNT(*) FROM documents")
    suspend fun count(): Int
}
