package com.github.henriquechsf.syscredentialapp.di

import com.github.henriquechsf.syscredentialapp.data.repository.auth.AuthFirebaseRepository
import com.github.henriquechsf.syscredentialapp.data.repository.auth.AuthFirebaseRepositoryImpl
import com.github.henriquechsf.syscredentialapp.data.repository.profile.ProfileRepository
import com.github.henriquechsf.syscredentialapp.data.repository.profile.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface DomainModule {

    @Binds
    fun bindsAuthRepository(
        authFirebaseRepositoryImpl: AuthFirebaseRepositoryImpl
    ): AuthFirebaseRepository

    @Binds
    fun bindsProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}