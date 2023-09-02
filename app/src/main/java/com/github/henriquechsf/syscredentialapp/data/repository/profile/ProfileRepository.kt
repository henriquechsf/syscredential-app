package com.github.henriquechsf.syscredentialapp.data.repository.profile

import com.github.henriquechsf.syscredentialapp.data.model.User

interface ProfileRepository {

    suspend fun getProfile(userId: String): User

    suspend fun saveProfile(user: User)

    suspend fun getProfileList(): List<User>

    suspend fun saveImageProfile(image: String): String
}