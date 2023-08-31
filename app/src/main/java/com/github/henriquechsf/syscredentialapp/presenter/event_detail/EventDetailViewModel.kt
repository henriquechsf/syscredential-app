package com.github.henriquechsf.syscredentialapp.presenter.event_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.data.model.Credential
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.GetCredentialUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveCredentialUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val saveCredentialUseCase: SaveCredentialUseCase,
    private val getCredentialUseCase: GetCredentialUseCase
) : ViewModel() {

    fun saveCredential(credential: Credential) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            saveCredentialUseCase(credential)

            emit(ResultState.Success(null))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    fun getCredential(eventId: String, credentialNumber: String) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            val credential = getCredentialUseCase(eventId, credentialNumber)

            emit(ResultState.Success(credential))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }
}
