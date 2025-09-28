package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun findByEmail(email: String): UserEntity? = userDao.findByEmail(email)
    override suspend fun findByUsername(username: String): UserEntity? = userDao.findByUsername(username)
    override suspend fun insert(user: UserEntity) { userDao.insert(user) }
}

