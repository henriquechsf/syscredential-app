package com.github.henriquechsf.syscredentialapp.presenter.event_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Credential
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.GetCredentialUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.event.SaveCredentialUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.profile.GetProfileUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val saveCredentialUseCase: SaveCredentialUseCase,
    private val getCredentialUseCase: GetCredentialUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _userLogged = MutableLiveData<User>()
    val userLogged: LiveData<User> get() = _userLogged

    init {
        viewModelScope.launch {
            val user = getProfileUseCase(FirebaseHelper.getUserId())
            _userLogged.value = user
        }
    }

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
