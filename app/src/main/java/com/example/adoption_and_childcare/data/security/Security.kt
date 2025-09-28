package com.example.adoption_and_childcare.data.security

import java.security.MessageDigest

object Security {
    fun hashPassword(plain: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(plain.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
