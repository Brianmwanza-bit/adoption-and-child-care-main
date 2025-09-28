package com.example.adoption_and_childcare

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.network.FcmTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Log.d("FCM", "User not logged in, storing token for later")
            // Store the token locally to send when user logs in
            storeTokenLocally(token)
            return
        }

        val authToken = sessionManager.getAuthToken()
        if (authToken.isNullOrEmpty()) {
            Log.w("FCM", "No auth token available")
            storeTokenLocally(token)
            return
        }

        // Get unique device ID
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        val request = FcmTokenRequest(
            fcmToken = token,
            deviceId = deviceId,
            platform = "android"
        )

        // Send token to server using coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.updateFcmToken(
                    authToken = "Bearer $authToken",
                    request = request
                )

                if (response.isSuccessful) {
                    Log.d("FCM", "FCM token successfully sent to server")
                    // Clear any locally stored token since it's now on the server
                    clearStoredToken()
                } else {
                    Log.e("FCM", "Failed to send FCM token: ${response.code()}")
                    // Store token locally to retry later
                    storeTokenLocally(token)
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error sending FCM token to server: ${e.message}")
                // Store token locally to retry later
                storeTokenLocally(token)
            }
        }
    }

    private fun storeTokenLocally(token: String) {
        val prefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("pending_fcm_token", token).apply()
        Log.d("FCM", "FCM token stored locally for later transmission")
    }

    private fun clearStoredToken() {
        val prefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("pending_fcm_token").apply()
    }

    // Call this method from your login activity/fragment after successful login
    companion object {
        fun sendPendingTokenToServer(context: Context) {
            val prefs = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
            val pendingToken = prefs.getString("pending_fcm_token", null)

            if (!pendingToken.isNullOrEmpty()) {
                val sessionManager = SessionManager(context)
                if (sessionManager.isLoggedIn()) {
                    // Create a temporary service instance to send the token
                    val service = MyFirebaseMessagingService()
                    service.sessionManager = sessionManager // Assigning the locally created SessionManager for the temp service
                    service.sendTokenToServer(pendingToken)
                } else {
                    Log.d("FCM", "User not logged in (in sendPendingTokenToServer), cannot send.")
                }
            } else {
                Log.d("FCM", "No pending token found (in sendPendingTokenToServer).")
            }
        } // Closes fun sendPendingTokenToServer
    } // Closes companion object

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel"

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Check if notification permission is granted (for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("FCM", "Notification permission not granted")
                return
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon
            .setContentTitle(title ?: "Notification")
            .setContentText(message ?: "")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            Log.e("FCM", "Failed to show notification: ${e.message}")
        }
    }
}
