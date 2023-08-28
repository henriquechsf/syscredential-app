package com.github.henriquechsf.syscredentialapp.domain.usecases.profile

import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.data.repository.profile.ProfileRepository
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
){

    suspend operator fun invoke(user: User) {
        return profileRepository.saveProfile(user)
    }
}