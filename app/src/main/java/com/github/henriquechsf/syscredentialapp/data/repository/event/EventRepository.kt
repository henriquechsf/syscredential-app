package com.github.henriquechsf.syscredentialapp.data.repository.event

import com.github.henriquechsf.syscredentialapp.data.model.Event

interface EventRepository {

    suspend fun getEventList(): List<Event>

    suspend fun saveEvent(event: Event)

    suspend fun removeEvent(event: Event)
}