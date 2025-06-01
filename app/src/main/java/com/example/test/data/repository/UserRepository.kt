// En UserRepository.kt
package com.example.test.data.repository

import com.example.test.data.dao.UserDao
import com.example.test.data.model.User
import kotlinx.coroutines.flow.Flow // Necesario

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUserById(userId: Int): Int {
        return userDao.deleteUserById(userId)
    }
}