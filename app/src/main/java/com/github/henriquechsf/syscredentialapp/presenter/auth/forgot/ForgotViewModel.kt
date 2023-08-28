package com.github.henriquechsf.syscredentialapp.presenter.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.domain.usecases.auth.ForgotUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val forgotUseCase: ForgotUseCase
) : ViewModel() {

    fun forgot(email: String) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            forgotUseCase.invoke(email)

            emit(StateView.Sucess(Unit))
        } catch (e: Exception) {
            emit(StateView.Error(e.message))
        }
    }
}