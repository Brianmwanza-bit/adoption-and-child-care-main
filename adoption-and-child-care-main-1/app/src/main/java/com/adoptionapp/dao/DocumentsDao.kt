package com.adoptionapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adoptionapp.DocumentsEntity

@Dao
interface DocumentsDao {
    @Query("SELECT * FROM DocumentsEntity ORDER BY document_id DESC")
    fun getAllDocuments(): LiveData<List<DocumentsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentsEntity)
} 