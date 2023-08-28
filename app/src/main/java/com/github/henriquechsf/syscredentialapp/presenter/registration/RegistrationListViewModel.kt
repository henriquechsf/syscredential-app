package com.github.henriquechsf.syscredentialapp.presenter.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.data.model.RegistrationUI
import com.github.henriquechsf.syscredentialapp.data.repository.PersonRepository
import com.github.henriquechsf.syscredentialapp.data.repository.RegistrationRepository
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
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

    private val _countRegistrations = MutableStateFlow<Int>(0)
    val countRegistrations = _countRegistrations.asStateFlow()

    fun insertRegistration(credential: String, eventId: Int) =
        viewModelScope.launch {
            try {
                /*
                val foundPerson = personRepository.getByRegistrationCode(credential.toLong())

                if (foundPerson != null) {
                    val registration = Registration(
                        createdAt = LocalDateTime.now().toString(),
                        eventId = eventId,
                        personId = foundPerson.id
                    )

                    registrationRepository.insert(registration)
                    _scanResult.value = ResultState.Success(foundPerson.name)
                } else {
                    _scanResult.value = ResultState.Error("Credencial não encontrada")
                }

                 */
            } catch (exception: Exception) {
                _scanResult.value = ResultState.Error("Credencial inválida")
            }
        }

    fun fetchRegistrations(eventId: Int) = viewModelScope.launch {
        /*
        registrationRepository.getByEvent(eventId).collectLatest { registrations ->
            _countRegistrations.value = registrations.size
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
                        personName = person?.name ?: "",
                        personInfo1 = person?.info1
                    )
                }
                _registrationsList.value = ResultState.Success(registrationsMapped)
            }
        }

         */
    }
}