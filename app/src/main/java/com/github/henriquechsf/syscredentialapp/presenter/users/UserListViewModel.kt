package com.github.henriquechsf.syscredentialapp.presenter.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.henriquechsf.syscredentialapp.domain.usecases.profile.GetProfileListUseCase
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getProfileListUseCase: GetProfileListUseCase
) : ViewModel() {

    fun getProfileList() = liveData(Dispatchers.IO) {
        try {
            emit(ResultState.Loading())

            val userList = getProfileListUseCase()

            if (userList.isEmpty()) {
                emit(ResultState.Empty())
            }

            emit(ResultState.Success(userList))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message))
        }
    }
}