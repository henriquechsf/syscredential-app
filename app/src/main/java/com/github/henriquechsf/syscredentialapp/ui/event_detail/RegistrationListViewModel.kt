package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.data.model.RegistrationUI
import com.github.henriquechsf.syscredentialapp.data.repository.PersonRepository
import com.github.henriquechsf.syscredentialapp.data.repository.RegistrationRepository
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RegistrationListViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _registrationsList =
        MutableStateFlow<ResultState<List<RegistrationUI>>>(ResultState.Empty())
    val registrationsList = _registrationsList.asStateFlow()

    private val _scanResult = MutableStateFlow<ResultState<String>>(ResultState.Empty())
    val scanResult = _scanResult.asStateFlow()

    fun insertRegistration(credential: String, eventId: Int) =
        viewModelScope.launch {
            try {
                val foundPerson = personRepository.getById(credential.toLong())

                if (foundPerson != null) {
                    val registration = Registration(
                        createdAt = LocalDateTime.now().toString(),
                        eventId = eventId,
                        personId = foundPerson.id
                    )

                    registrationRepository.insert(registration)
                    _scanResult.value = ResultState.Success(foundPerson.name)
                } else {
                    _scanResult.value = ResultState.Error("Credential not found")
                }
            } catch (exception: Exception) {
                _scanResult.value = ResultState.Error("Credential format not valid")
            }
        }

    fun fetchRegistrations(eventId: Int) = viewModelScope.launch {
        registrationRepository.getByEvent(eventId).collectLatest { registrations ->
            if (registrations.isEmpty()) {
                _registrationsList.value = ResultState.Empty()
            } else {
                val registrationsMapped = registrations.map {
                    val person = personRepository.getById(it.personId)
                    RegistrationUI(
                        id = it.id,
                        createdAt = it.createdAt,
                        eventId = it.eventId,
                        personId = it.personId,
                        personName = person?.name ?: ""
                    )
                }
                _registrationsList.value = ResultState.Success(registrationsMapped)
            }
        }
    }
}