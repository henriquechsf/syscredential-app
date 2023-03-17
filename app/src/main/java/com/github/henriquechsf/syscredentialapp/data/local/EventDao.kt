package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.henriquechsf.syscredentialapp.data.model.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventEntity: EventEntity): Long

    @Query("SELECT * FROM events ORDER BY id")
    fun getAll(): Flow<List<EventEntity>>

    @Delete
    suspend fun delete(eventEntity: EventEntity)
}