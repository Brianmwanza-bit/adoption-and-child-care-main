package com.example.adoption_and_childcare.viewmodel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.adoption_and_childcare.service.SOSLocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

enum class SOSState { IDLE, ARMING, ACTIVE, CANCELLED, SENT }

/**
 * ViewModel for managing SOS emergency functionality.
 * 
 * This ViewModel handles SOS state management, emergency calling,
 * and location tracking during emergency events.
 * 
 * @property context Application context for starting services.
 */
@HiltViewModel
class SOSViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _sosState = MutableStateFlow(SOSState.IDLE)
    val sosState: StateFlow<SOSState> = _sosState.asStateFlow()

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    private var currentEventId: String? = null

    fun triggerSOS() {
        _sosState.value = SOSState.ARMING
        currentEventId = UUID.randomUUID().toString()
        
        // Feature E4 Step A — Place emergency call immediately
        placeEmergencyCall()

        // Feature E3/E4 — Start service
        val intent = Intent(context, SOSLocationService::class.java).apply {
            putExtra("EVENT_ID", currentEventId)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun placeEmergencyCall() {
        val prefs = context.getSharedPreferences("sos_prefs", Context.MODE_PRIVATE)
        val emergencyPhone = prefs.getString("sos_emergency_phone", "999") ?: "999"
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$emergencyPhone")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$emergencyPhone")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
        }
    }

    fun activateSOS() {
        _sosState.value = SOSState.ACTIVE
    }

    fun cancelSOS() {
        _sosState.value = SOSState.CANCELLED
        context.stopService(Intent(context, SOSLocationService::class.java))
    }

    fun setLocation(location: Location) {
        _locationState.value = location
    }
}
