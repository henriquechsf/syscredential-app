package com.github.henriquechsf.syscredentialapp.domain.usecases.auth

import com.github.henriquechsf.syscredentialapp.data.repository.auth.AuthFirebaseRepository
import javax.inject.Inject

class ForgotUseCase @Inject constructor(
    private val authFirebaseRepository: AuthFirebaseRepository
) {

    suspend operator fun invoke(email: String) {
        return authFirebaseRepository.forgot(email)
    }
}