package com.github.henriquechsf.syscredentialapp.domain.usecases.profile

import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.data.repository.profile.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(userId: String): User {
        return profileRepository.getProfile(userId)
    }
}