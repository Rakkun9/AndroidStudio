// En UserRepository.kt
package com.example.test.data.repository // O tu paquete

import com.example.test.data.dao.UserDao
import com.example.test.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

//Esto tal vez sirva para el registro de un usuario
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
}