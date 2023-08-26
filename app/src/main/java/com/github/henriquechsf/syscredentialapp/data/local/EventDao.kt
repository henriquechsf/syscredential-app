package com.github.henriquechsf.syscredentialapp.data.local

import com.github.henriquechsf.syscredentialapp.data.model.Event
import kotlinx.coroutines.flow.Flow

//@Dao
interface EventDao {

    //  @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long

    //@Query("SELECT * FROM events WHERE title LIKE :query || '%' ORDER BY id DESC")
    fun getAll(query: String?): Flow<List<Event>>

    //@Delete
    suspend fun delete(event: Event)
}