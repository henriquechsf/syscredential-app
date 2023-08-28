package com.github.henriquechsf.syscredentialapp.data.repository.event

import com.github.henriquechsf.syscredentialapp.data.model.Event

interface EventRepository {

    suspend fun getAll(query: String? = ""): List<Event>

    suspend fun getById(id: String): Event?

    suspend fun insert(eventEntity: Event)

    suspend fun delete(eventEntity: Event)
}