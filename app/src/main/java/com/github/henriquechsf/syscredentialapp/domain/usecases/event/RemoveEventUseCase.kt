package com.github.henriquechsf.syscredentialapp.domain.usecases.event

import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.repository.event.EventRepository
import javax.inject.Inject

class RemoveEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke(event: Event) {
        return eventRepository.removeEvent(event)
    }
}