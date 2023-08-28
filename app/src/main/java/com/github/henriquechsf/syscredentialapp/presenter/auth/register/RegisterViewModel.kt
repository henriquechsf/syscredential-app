package com.github.henriquechsf.syscredentialapp.presenter.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.domain.usecases.auth.RegisterUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    fun register(user: User) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            val registeredUser = registerUseCase.invoke(user)

            emit(StateView.Sucess(registeredUser))
        } catch (e: Exception) {
            emit(StateView.Error(e.message))
        }
    }
}