package com.example.adoption_and_childcare.data.repository

import android.content.Context
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val childDao: ChildDao
) : BaseSyncRepository(appContext), ChildRepository {

    override fun observeAll(): Flow<List<ChildEntity>> = childDao.observeAll()

    override suspend fun findById(id: Int): ChildEntity? = childDao.findById(id)

    override suspend fun insert(child: ChildEntity): Long {
        val id = childDao.insert(child)
        scheduleSync()
        return id
    }

    override suspend fun update(child: ChildEntity) {
        childDao.update(child)
        scheduleSync()
    }

    override suspend fun deleteById(id: Int) {
        childDao.deleteById(id)
        scheduleSync()
    }

    override fun searchByName(query: String): Flow<List<ChildEntity>> = childDao.searchByName("%$query%")
}
