package com.github.henriquechsf.syscredentialapp.data.repository.auth

import com.github.henriquechsf.syscredentialapp.data.model.User

interface AuthFirebaseRepository {

    suspend fun login(email: String, password: String)

    suspend fun register(user: User): User

    suspend fun forgot(email: String)
}