package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: DocumentEntity): Long

    @Transaction
    suspend fun insertWithSync(doc: DocumentEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(doc)
        val payload = Gson().toJson(doc)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "documents",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(doc: DocumentEntity)

    @Transaction
    suspend fun updateWithSync(doc: DocumentEntity, syncQueueDao: SyncQueueDao) {
        update(doc)
        val payload = Gson().toJson(doc)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "documents",
                operation = "UPDATE",
                recordId = doc.documentId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM documents WHERE document_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "documents",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM documents WHERE child_id = :childId ORDER BY uploaded_at DESC")
    fun observeByChild(childId: Int): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY uploaded_at DESC")
    fun observeAll(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents")
    suspend fun getAll(): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE document_id = :id")
    suspend fun findById(id: Int): DocumentEntity?

    @Query("SELECT COUNT(*) FROM documents")
    suspend fun count(): Int

    @Query("SELECT * FROM documents WHERE file_name LIKE :q OR description LIKE :q OR document_type LIKE :q ORDER BY uploaded_at DESC LIMIT :limit")
    suspend fun globalSearch(q: String, limit: Int = 5): List<DocumentEntity>
}
