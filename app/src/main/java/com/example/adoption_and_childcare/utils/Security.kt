package com.example.adoption_and_childcare.utils

import java.security.MessageDigest

/**
 * Utility object for security-related operations.
 * 
 * Provides methods for password hashing using SHA-256 algorithm.
 */
object Security {
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
