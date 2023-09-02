package com.github.henriquechsf.syscredentialapp.domain.usecases.event

import com.github.henriquechsf.syscredentialapp.data.model.Credential
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.data.repository.event.EventRepository
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class SaveRegistrationUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke(credential: Credential) {
        if (credential.isRegistered) {
            throw Exception("Credencial ja registrada")
        }
        val registration = Registration(
            id = UUID.randomUUID().toString(),
            eventId = credential.eventId,
            userId = credential.userId,
            userName = credential.userName,
            userDepartment = credential.userDepartment,
            userImage = credential.userImage,
            createdAt = LocalDateTime.now().toString()
        )

        return eventRepository.saveRegistration(registration)
    }
}