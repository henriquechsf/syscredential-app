package com.github.henriquechsf.syscredentialapp.domain.usecases.auth

import com.github.henriquechsf.syscredentialapp.data.repository.auth.AuthFirebaseRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authFirebaseRepository: AuthFirebaseRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return authFirebaseRepository.login(email, password)
    }
}