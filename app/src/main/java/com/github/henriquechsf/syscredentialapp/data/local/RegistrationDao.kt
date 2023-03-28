package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(registration: Registration): Long

    @Query("SELECT * FROM registrations WHERE event_id = :eventId ORDER BY id")
    fun getRegistrationsByEvent(eventId: Int): Flow<List<Registration>>

    @Delete
    suspend fun delete(registration: Registration)
}