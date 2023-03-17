package com.github.henriquechsf.syscredentialapp.data.repository

import com.github.henriquechsf.syscredentialapp.data.local.PersonDao
import com.github.henriquechsf.syscredentialapp.data.model.Person
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonRepository @Inject constructor(
    private val personDao: PersonDao,
) {

    fun getAll(): Flow<List<Person>> = personDao.getAll()

    suspend fun insert(person: Person): Long = personDao.insert(person)

    suspend fun delete(person: Person) = personDao.delete(person)
}