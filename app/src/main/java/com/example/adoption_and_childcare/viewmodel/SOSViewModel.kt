package com.example.adoption_and_childcare.viewmodel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.session.AppSettings
import com.example.adoption_and_childcare.service.SOSLocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

enum class SOSState { IDLE, ARMING, ACTIVE, CANCELLED, SENT }

/**
 * ViewModel for managing SOS emergency functionality.
 * 
 * This ViewModel handles SOS state management, emergency calling,
 * and location tracking during emergency events.
 */
@HiltViewModel
class SOSViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appSettings: AppSettings
) : ViewModel() {
    private val _sosState = MutableStateFlow(SOSState.IDLE)
    val sosState: StateFlow<SOSState> = _sosState.asStateFlow()

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    private val _emergencyContacts = MutableStateFlow<Map<String, String>>(emptyMap())
    val emergencyContacts: StateFlow<Map<String, String>> = _emergencyContacts.asStateFlow()

    private var currentEventId: String? = null

    init {
        loadEmergencyContacts()
    }

    private fun loadEmergencyContacts() {
        val contacts = mapOf(
            "police" to appSettings.getSosContact("police"),
            "fire" to appSettings.getSosContact("fire"),
            "hospital" to appSettings.getSosContact("hospital"),
            "cps" to appSettings.getSosContact("cps"),
            "ems" to appSettings.getSosContact("ems"),
            "contact1_name" to appSettings.getSosContact("emergency1_name"),
            "contact1_phone" to appSettings.getSosContact("emergency1_phone"),
            "contact2_name" to appSettings.getSosContact("emergency2_name"),
            "contact2_phone" to appSettings.getSosContact("emergency2_phone")
        )
        _emergencyContacts.value = contacts
    }

    /**
     * Triggers the SOS emergency sequence.
     */
    fun triggerSOS() {
        _sosState.value = SOSState.ARMING
        currentEventId = UUID.randomUUID().toString()
        
        // 1. Place primary emergency call immediately (Police or 999)
        val primaryPhone = appSettings.getSosContact("police").ifBlank { "999" }
        placeCall(primaryPhone)

        // 2. Start Location Tracking Service
        val intent = Intent(context, SOSLocationService::class.java).apply {
            putExtra("EVENT_ID", currentEventId)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        // 3. Send SMS alerts to personal contacts
        sendEmergencySms()
    }

    /**
     * Places a phone call to a specified number.
     */
    fun placeCall(phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to dialer if CALL permission is not granted
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
        }
    }

    /**
     * Sends emergency SMS messages to personal contacts with the last known location.
     */
    private fun sendEmergencySms() {
        viewModelScope.launch {
            val contact1Phone = appSettings.getSosContact("emergency1_phone")
            val contact2Phone = appSettings.getSosContact("emergency2_phone")
            val location = _locationState.value
            
            val locationText = if (location != null) {
                " My location: https://www.google.com/maps?q=${location.latitude},${location.longitude}"
            } else ""
            
            val message = "EMERGENCY! I need help immediately.$locationText"

            try {
                val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }

                if (contact1Phone.isNotBlank()) {
                    smsManager.sendTextMessage(contact1Phone, null, message, null, null)
                }
                if (contact2Phone.isNotBlank()) {
                    smsManager.sendTextMessage(contact2Phone, null, message, null, null)
                }
            } catch (e: Exception) {
                // SMS sending might fail due to permissions or network
            }
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
