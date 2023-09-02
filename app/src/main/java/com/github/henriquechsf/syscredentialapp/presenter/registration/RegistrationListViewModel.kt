package com.github.henriquechsf.syscredentialapp.presenter.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.GetCredentialUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.GetRegistrationListUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveCredentialUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveRegistrationUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.profile.GetProfileUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationListViewModel @Inject constructor(
    private val saveRegistrationUseCase: SaveRegistrationUseCase,
    private val getCredentialUseCase: GetCredentialUseCase,
    private val saveCredentialUseCase: SaveCredentialUseCase,
    private val getRegistrationListUseCase: GetRegistrationListUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _scanResult = MutableStateFlow<ResultState<String>>(ResultState.Empty())
    val scanResult = _scanResult.asStateFlow()

    private val _countRegistrations = MutableStateFlow<Int>(0)
    val countRegistrations = _countRegistrations.asStateFlow()

    fun saveRegistration(eventId: String, credential: String) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            val foundCredential = getCredentialUseCase(eventId, credential)

            saveRegistrationUseCase(foundCredential)

            foundCredential.isRegistered = true
            saveCredentialUseCase(foundCredential)

            _scanResult.value = ResultState.Success(foundCredential.id)
            emit(ResultState.Success(null))
        } catch (e: Exception) {
            _scanResult.value = ResultState.Error(e.message)
            emit(ResultState.Error(e.message))
        }
    }

    // TODO: refactor to flow
    fun getRegistrationList(eventId: String) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            val registrationList = getRegistrationListUseCase(eventId)
            _countRegistrations.value = registrationList.size

            if (registrationList.isEmpty()) {
                emit(ResultState.Empty())
            } else {
                emit(ResultState.Success(registrationList))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }
}