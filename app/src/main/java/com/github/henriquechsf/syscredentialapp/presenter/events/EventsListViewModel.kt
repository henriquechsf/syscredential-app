package com.github.henriquechsf.syscredentialapp.presenter.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.GetEventListUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveEventUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsListViewModel @Inject constructor(
    private val saveEventUseCase: SaveEventUseCase,
    private val getEventListUseCase: GetEventListUseCase
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

    fun removeEvent(event: Event) = viewModelScope.launch {
        try {
            //  eventRepository.delete(event)
        } catch (e: Exception) {
            Log.i("TAG", "removeEvent: Error")
        }
    }
}
