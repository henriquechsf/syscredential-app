package com.github.henriquechsf.syscredentialapp.domain.usecases.auth

import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    // private val firebaseAuthentication: FirebaseAuthentication
) {

    suspend operator fun invoke(email: String, password: String) {
        //firebaseAuthentication.register(email, password)
    }
}