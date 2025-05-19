// En PasswordUtils.kt
package com.example.test.util // O el paquete que elijas

import java.security.MessageDigest

object PasswordUtils {
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    fun verifyPassword(inputPassword: String, storedHashedPassword: String): Boolean {
        return hashPassword(inputPassword) == storedHashedPassword
    }
}