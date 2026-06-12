package com.example.adoption_and_childcare.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.adoption_and_childcare.utils.EncryptedPreferencesManager
import com.yourdomain.adoptionchildcare.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Manager class for handling biometric authentication.
 *
 * @property context The application context.
 */
class BiometricAuthManager(private val context: Context) {
    private val encryptedPrefs: EncryptedPreferencesManager = EncryptedPreferencesManager(context)
    private val _authResult: Channel<BiometricAuthResult> = Channel<BiometricAuthResult>()
    
    /**
     * Flow of biometric authentication results.
     */
    val authResult: Flow<BiometricAuthResult> = _authResult.receiveAsFlow()

    /**
     * Checks if biometric authentication is available on the device.
     *
     * @return True if biometric authentication is available, false otherwise.
     */
    fun isBiometricAvailable(): Boolean {
        val biometricManager: BiometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Checks if biometric data is enrolled for a specific user.
     *
     * @param userId The ID of the user.
     * @return True if biometric data is enrolled, false otherwise.
     */
    fun hasBiometricEnrolled(userId: String): Boolean {
        return encryptedPrefs.getBiometricData(userId) != null
    }

    /**
     * Saves biometric data for a specific user.
     *
     * @param userId The ID of the user.
     * @param fingerprint The fingerprint data to save.
     */
    fun saveBiometricData(userId: String, fingerprint: String) {
        encryptedPrefs.setBiometricData(userId, fingerprint)
    }

    /**
     * Initiates the biometric authentication process.
     *
     * @param activity The fragment activity used to host the biometric prompt.
     * @param onSuccess Callback for successful authentication.
     * @param onError Callback for authentication errors, providing an error message.
     */
    fun authenticate(
        activity: FragmentActivity, 
        onSuccess: () -> Unit, 
        onError: (String) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onError(context.getString(R.string.biometric_error_unavailable))
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity, 
            executor, 
            object : BiometricPrompt.AuthenticationCallback() {
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
                    onError(context.getString(R.string.biometric_auth_failed))
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle))
            .setNegativeButtonText(context.getString(R.string.biometric_negative_button))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Clears biometric data for a specific user.
     *
     * @param userId The ID of the user.
     */
    fun clearBiometricData(userId: String) {
        encryptedPrefs.removeBiometricData(userId)
    }
}

/**
 * Sealed class representing the result of a biometric authentication attempt.
 */
sealed class BiometricAuthResult {
    /**
     * Represents a successful biometric authentication.
     */
    object Success : BiometricAuthResult()
    
    /**
     * Represents a failed biometric authentication with an error message.
     *
     * @property message The error message.
     */
    data class Error(val message: String) : BiometricAuthResult()
}
