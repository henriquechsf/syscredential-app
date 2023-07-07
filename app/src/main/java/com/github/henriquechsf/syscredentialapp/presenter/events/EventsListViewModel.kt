package com.github.henriquechsf.syscredentialapp.presenter.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.repository.EventRepository
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsListViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _eventList = MutableStateFlow<ResultState<List<Event>>>(ResultState.Empty())
    val eventList: StateFlow<ResultState<List<Event>>> = _eventList

    init {
        fetch()
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        eventRepository.insert(event)
    }

    fun removeEvent(event: Event) = viewModelScope.launch {
        eventRepository.delete(event)
    }

    fun fetch(query: String? = "") = viewModelScope.launch {
        eventRepository.getAll(query).collectLatest { events ->
            if (events.isEmpty()) {
                _eventList.value = ResultState.Empty()
            } else {
                _eventList.value = ResultState.Success(events)
            }
        }
    }
}
