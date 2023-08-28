package com.github.henriquechsf.syscredentialapp.domain.usecases.auth

import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.data.repository.auth.AuthFirebaseRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authFirebaseRepository: AuthFirebaseRepository
) {

    suspend operator fun invoke(user: User): User {
        return authFirebaseRepository.register(user)
    }
}