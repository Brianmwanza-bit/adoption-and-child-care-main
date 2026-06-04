package com.example.adoption_and_childcare.data.repository

import android.content.Context
import com.example.adoption_and_childcare.data.db.dao.AdoptionApplicationDao
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdoptionApplicationRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val applicationDao: AdoptionApplicationDao
) : BaseSyncRepository(appContext), AdoptionApplicationRepository {

    override fun observeAll(): Flow<List<AdoptionApplicationEntity>> = applicationDao.observeAll()

    override fun observeForFamily(familyId: Int): Flow<List<AdoptionApplicationEntity>> = 
        applicationDao.observeForFamily(familyId)

    override suspend fun findById(id: Int): AdoptionApplicationEntity? = applicationDao.findById(id)

    override suspend fun insert(application: AdoptionApplicationEntity): Long {
        val id = applicationDao.insert(application)
        scheduleSync()
        return id
    }

    override suspend fun update(application: AdoptionApplicationEntity) {
        applicationDao.update(application)
        scheduleSync()
    }

    override suspend fun deleteById(id: Int) {
        applicationDao.deleteById(id)
        scheduleSync()
    }
}
