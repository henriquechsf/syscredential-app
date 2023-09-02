package com.github.henriquechsf.syscredentialapp.domain.usecases.event

import com.github.henriquechsf.syscredentialapp.data.repository.event.EventRepository
import javax.inject.Inject

class SaveImageEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke(eventId: String, imageEvent: String): String {
        return eventRepository.saveImageEvent(eventId = eventId, image = imageEvent)
    }
}