package com.example.adoption_and_childcare.data.repository

import android.content.Context
import com.example.adoption_and_childcare.data.db.dao.FamilyDao
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val familyDao: FamilyDao
) : BaseSyncRepository(appContext), FamilyRepository {

    override fun observeAll(): Flow<List<FamilyEntity>> = familyDao.observeAll()

    override suspend fun findById(id: Int): FamilyEntity? = familyDao.findById(id)

    override suspend fun insert(family: FamilyEntity): Long {
        val id = familyDao.insert(family)
        scheduleSync()
        return id
    }

    override suspend fun update(family: FamilyEntity) {
        familyDao.update(family)
        scheduleSync()
    }

    override suspend fun deleteById(id: Int) {
        familyDao.deleteById(id)
        scheduleSync()
    }

    override fun searchByName(query: String): Flow<List<FamilyEntity>> = familyDao.searchByName("%$query%")
}
