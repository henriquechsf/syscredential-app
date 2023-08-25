package com.github.henriquechsf.syscredentialapp.data.repository.auth

import com.github.henriquechsf.syscredentialapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthFirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthFirebaseRepository {
    override suspend fun login(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun register(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun forgot(email: String) {
        TODO("Not yet implemented")
    }
}