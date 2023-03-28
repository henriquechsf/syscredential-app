package com.github.henriquechsf.syscredentialapp.data.repository

import com.github.henriquechsf.syscredentialapp.data.local.RegistrationDao
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegistrationRepository @Inject constructor(
    private val registrationDao: RegistrationDao
) {

    fun getByEvent(eventId: Int): Flow<List<Registration>> =
        registrationDao.getRegistrationsByEvent(eventId)

    suspend fun insert(registration: Registration) = registrationDao.insert(registration)

    suspend fun delete(registration: Registration) = registrationDao.delete(registration)
}