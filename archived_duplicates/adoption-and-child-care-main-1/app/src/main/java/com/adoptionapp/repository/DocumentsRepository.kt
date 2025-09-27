package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.DocumentsDao
import com.adoptionapp.DocumentsEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.adoptionapp.RetrofitClient
import android.content.Context
import com.adoptionapp.TokenManager
 
class DocumentsRepository(private val dao: DocumentsDao, private val context: Context) {
    fun getAllDocuments(): LiveData<List<DocumentsEntity>> = dao.getAllDocuments()
    suspend fun insert(document: DocumentsEntity) = dao.insert(document)
    suspend fun insertWithFile(document: DocumentsEntity, fileBytes: ByteArray?) {
        dao.insert(document)
        if (fileBytes != null) {
            val requestFile = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), fileBytes)
            val body = MultipartBody.Part.createFormData("file", "file.bin", requestFile)
            val token = "Bearer " + (TokenManager.getToken(context) ?: "")
            try {
                RetrofitClient.apiService.uploadDocumentFile(token, document.document_id, body)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
} 