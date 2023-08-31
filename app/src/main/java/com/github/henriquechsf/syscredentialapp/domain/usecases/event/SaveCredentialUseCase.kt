package com.github.henriquechsf.syscredentialapp.domain.usecases.event

import com.github.henriquechsf.syscredentialapp.data.model.Credential
import com.github.henriquechsf.syscredentialapp.data.repository.event.EventRepository
import javax.inject.Inject

class SaveCredentialUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke(credential: Credential) {
        return eventRepository.saveCredential(credential)
    }
}