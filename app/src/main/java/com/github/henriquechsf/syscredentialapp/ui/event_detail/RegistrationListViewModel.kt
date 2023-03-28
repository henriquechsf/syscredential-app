package com.github.henriquechsf.syscredentialapp.ui.event_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.data.repository.RegistrationRepository
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationListViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _registrationsList =
        MutableStateFlow<ResultState<List<Registration>>>(ResultState.Empty())
    val registrationsList = _registrationsList.asStateFlow()

    fun insertRegistration(registration: Registration) = viewModelScope.launch {
        registrationRepository.insert(registration)
    }

    fun fetchRegistrations(eventId: Int) = viewModelScope.launch {
        registrationRepository.getByEvent(eventId).collectLatest { registrations ->
            if (registrations.isEmpty()) {
                _registrationsList.value = ResultState.Empty()
            } else {
                _registrationsList.value = ResultState.Success(registrations)
            }
        }
    }
}