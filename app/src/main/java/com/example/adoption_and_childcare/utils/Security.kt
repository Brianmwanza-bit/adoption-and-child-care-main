package com.example.adoption_and_childcare.utils

import java.security.MessageDigest

/**
 * Utility object for security-related operations.
 * 
 * Provides methods for password hashing using SHA-256 algorithm.
 */
object Security {
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
