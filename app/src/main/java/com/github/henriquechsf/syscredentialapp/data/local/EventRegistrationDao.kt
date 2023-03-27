package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.henriquechsf.syscredentialapp.data.model.EventRegistration
import kotlinx.coroutines.flow.Flow

@Dao
interface EventRegistrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(registration: EventRegistration): Long

    @Query("SELECT * FROM event_registrations WHERE event_id = :eventId ORDER BY id")
    fun getRegistrationsByEvent(eventId: Int): Flow<List<EventRegistration>>

    @Delete
    suspend fun delete(eventRegistration: EventRegistration)
}