package com.github.henriquechsf.syscredentialapp.presenter.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.GetEventListUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.RemoveEventUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveEventUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveImageEventUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class EventsListViewModel @Inject constructor(
    private val saveEventUseCase: SaveEventUseCase,
    private val saveImageEventUseCase: SaveImageEventUseCase,
    private val getEventListUseCase: GetEventListUseCase,
    private val removeEventUseCase: RemoveEventUseCase
) : ViewModel() {

    // TODO: refactor to ResultState
    fun saveEvent(event: Event) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            saveEventUseCase(event)

            emit(ResultState.Success(null))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    fun saveImageEvent(eventId: String, image: String) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            val urlImageEvent = saveImageEventUseCase(eventId = eventId, imageEvent = image)

            emit(ResultState.Success(urlImageEvent))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    fun getEventList() = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            val eventList = getEventListUseCase()

            if (eventList.isEmpty()) {
                emit(ResultState.Empty())
            }

            emit(ResultState.Success(eventList))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    fun removeEvent(event: Event) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            removeEventUseCase(event)

            emit(ResultState.Success(null))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }
}
