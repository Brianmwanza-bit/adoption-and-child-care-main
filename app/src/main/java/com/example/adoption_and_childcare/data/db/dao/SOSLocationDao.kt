package com.example.adoption_and_childcare.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.adoption_and_childcare.data.db.entities.SOSLocationEntity

@Dao
interface SOSLocationDao {
    @Insert
    suspend fun insert(location: SOSLocationEntity)

    @Query("SELECT * FROM sos_locations WHERE sos_event_id = :eventId ORDER BY timestamp DESC")
    suspend fun getHistoryForEvent(eventId: String): List<SOSLocationEntity>
}
