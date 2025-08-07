import androidx.room.*
import androidx.lifecycle.LiveData

@Dao
interface DocumentsDao {
    @Query("SELECT * FROM documents")
    suspend fun getAll(): List<DocumentsEntity>

    @Query("SELECT * FROM documents WHERE document_id = :id")
    suspend fun getById(id: Int): DocumentsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(documents: List<DocumentsEntity>)

    @Update
    suspend fun update(document: DocumentsEntity)

    @Delete
    suspend fun delete(document: DocumentsEntity)

    @Query("DELETE FROM documents")
    suspend fun clearAll()

    @Query("SELECT * FROM documents")
    fun getAllLive(): LiveData<List<DocumentsEntity>>
} 