package com.github.henriquechsf.syscredentialapp.data.repository.event

import com.github.henriquechsf.syscredentialapp.data.model.Credential
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.Registration

interface EventRepository {

    suspend fun getEventList(): List<Event>

    suspend fun saveEvent(event: Event)

    suspend fun removeEvent(event: Event)

    suspend fun saveCredential(credential: Credential)

    suspend fun getCredential(eventId: String, userId: String): Credential

    suspend fun saveRegistration(registration: Registration)
}