package com.github.henriquechsf.syscredentialapp.domain.usecases.profile

import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.data.repository.profile.ProfileRepository
import javax.inject.Inject

class GetProfileListUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(): List<User> {
        return profileRepository.getProfileList()
    }
}