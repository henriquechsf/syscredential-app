package com.github.henriquechsf.syscredentialapp.presenter.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.domain.usecases.profile.GetProfileUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.profile.SaveImageProfileUseCase
import com.github.henriquechsf.syscredentialapp.domain.usecases.profile.SaveProfileUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class UserFormViewModel @Inject constructor(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val saveImageProfileUseCase: SaveImageProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {


    fun saveProfile(user: User) = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            saveProfileUseCase(user)

            emit(ResultState.Success(null))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }

    fun saveImageProfile(image: String) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            val urlImageProfile = saveImageProfileUseCase(image)

            emit(StateView.Sucess(urlImageProfile))
        } catch (e: Exception) {
            emit(StateView.Error(e.message))
        }
    }

    fun getProfile() = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            val user = getProfileUseCase(FirebaseHelper.getUserId())

            emit(StateView.Sucess(user))
        } catch (e: Exception) {
            emit(StateView.Error(e.message))
        }
    }
}
