package com.example.adoption_and_childcare.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.adoption_and_childcare.data.db.dao.SOSLocationDao
import com.example.adoption_and_childcare.data.db.entities.SOSLocationEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.data.session.SessionManager

@AndroidEntryPoint
class SOSLocationService : Service() {

    @Inject lateinit var sosLocationDao: SOSLocationDao
    @Inject lateinit var apiService: ApiService
    @Inject lateinit var sessionManager: SessionManager

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastSmsTime = 0L
    private var currentEventId: String? = null
    private var updateCount = 0

    companion object {
        private const val CHANNEL_ID = "sos_location_channel"
        private const val NOTIFICATION_ID = 999
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    handleLocationUpdate(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SOS") {
            stopSelf()
            return START_NOT_STICKY
        }
        
        currentEventId = intent?.getStringExtra("EVENT_ID")
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).apply {
            setMinUpdateIntervalMillis(5000L)
        }.build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Log error
        }
    }

    private fun handleLocationUpdate(location: Location) {
        // Save to local DB
        currentEventId?.let { eventId ->
            serviceScope.launch {
                sosLocationDao.insert(
                    SOSLocationEntity(
                        sosEventId = eventId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }

        // Feature E3 — Post to backend
        serviceScope.launch {
            try {
                currentEventId?.let { eventId ->
                    val token = "Bearer ${sessionManager.getAuthToken()}"
                    // Assuming endpoint exists or will be added to ApiService
                    // apiService.postEmergencyLocation(token, eventId, location.latitude, location.longitude, location.accuracy)
                }
            } catch (e: Exception) {}
        }

        // Feature E4 — SMS updates every 30 seconds
        val now = System.currentTimeMillis()
        if (now - lastSmsTime >= 30000L) {
            sendLocationSms(location)
            lastSmsTime = now
        }
    }

    private fun sendLocationSms(location: Location) {
        updateCount++
        val mapsLink = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        val message = if (updateCount == 1) {
            """
            EMERGENCY SOS — Child Welfare App
            Person: ${sessionManager.getUsername()}
            Time: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}
            LIVE LOCATION: $mapsLink
            """.trimIndent()
        } else {
            "LIVE UPDATE [$updateCount]: ${sessionManager.getUsername()} location — $mapsLink (Acc: ${location.accuracy.toInt()}m)"
        }

        try {
            val smsManager = SmsManager.getDefault()
            val prefs = getSharedPreferences("sos_prefs", Context.MODE_PRIVATE)
            val contactsJson = prefs.getString("sos_emergency_contacts", "[]")
            // Parse contacts and send. For now, sending to primary emergency phone.
            val primaryPhone = prefs.getString("sos_emergency_phone", "999") ?: "999"
            smsManager.sendTextMessage(primaryPhone, null, message, null, null)
        } catch (e: Exception) {}
    }

    private fun createNotification(): Notification {
        val cancelIntent = Intent(this, SOSLocationService::class.java).apply {
            action = "STOP_SOS"
        }
        val pendingCancelIntent = PendingIntent.getService(
            this, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Emergency active")
            .setContentText("Broadcasting live location...")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setOngoing(true)
            .addAction(0, "Cancel SOS", pendingCancelIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "SOS Location Updates",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
    }
}
