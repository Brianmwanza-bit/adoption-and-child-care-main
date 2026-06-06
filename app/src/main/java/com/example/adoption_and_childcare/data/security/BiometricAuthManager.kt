package com.example.adoption_and_childcare.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.example.adoption_and_childcare.utils.EncryptedPreferencesManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricAuthManager(private val context: Context) {
    private val encryptedPrefs = EncryptedPreferencesManager(context)
    private val _authResult = Channel<BiometricAuthResult>()
    val authResult = _authResult.receiveAsFlow()

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    fun hasBiometricEnrolled(userId: String): Boolean {
        return encryptedPrefs.getBiometricData(userId) != null
    }

    fun saveBiometricData(userId: String, fingerprint: String) {
        encryptedPrefs.setBiometricData(userId, fingerprint)
    }

    fun authenticate(activity: FragmentActivity, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isBiometricAvailable()) {
            onError("Biometric authentication not available on this device")
            return
        }

        val executor = androidx.core.content.ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Authentication failed")
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun clearBiometricData(userId: String) {
        encryptedPrefs.removeBiometricData(userId)
    }
}

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    data class Error(val message: String) : BiometricAuthResult()
}
