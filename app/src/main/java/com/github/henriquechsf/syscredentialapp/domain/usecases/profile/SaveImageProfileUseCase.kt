package com.github.henriquechsf.syscredentialapp.domain.usecases.profile

import com.github.henriquechsf.syscredentialapp.data.repository.profile.ProfileRepository
import javax.inject.Inject

class SaveImageProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(imageProfile: String): String {
        return profileRepository.saveImageProfile(imageProfile)
    }
}