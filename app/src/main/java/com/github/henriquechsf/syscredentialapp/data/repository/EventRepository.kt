package com.github.henriquechsf.syscredentialapp.data.repository

import com.github.henriquechsf.syscredentialapp.data.local.EventDao
import com.github.henriquechsf.syscredentialapp.data.model.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao
) {

    fun getAll(): Flow<List<Event>> = eventDao.getAll()

    suspend fun insert(event: Event): Long = eventDao.insert(event)

    suspend fun delete(event: Event) = eventDao.delete(event)
}