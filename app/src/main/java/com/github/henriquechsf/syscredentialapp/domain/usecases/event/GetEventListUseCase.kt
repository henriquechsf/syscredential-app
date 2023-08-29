package com.github.henriquechsf.syscredentialapp.domain.usecases.event

import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.repository.event.EventRepository
import javax.inject.Inject

class GetEventListUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke(): List<Event> {
        return eventRepository.getEventList()
    }
}