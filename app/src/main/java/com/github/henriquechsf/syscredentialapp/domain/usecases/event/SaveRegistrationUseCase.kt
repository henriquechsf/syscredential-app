package com.github.henriquechsf.syscredentialapp.domain.usecases.event

import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.data.repository.event.EventRepository
import javax.inject.Inject

class SaveRegistrationUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke(registration: Registration) {
        return eventRepository.saveRegistration(registration)
    }
}