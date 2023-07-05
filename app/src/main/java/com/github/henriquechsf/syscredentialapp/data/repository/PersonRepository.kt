package com.github.henriquechsf.syscredentialapp.data.repository

import com.github.henriquechsf.syscredentialapp.data.local.PersonDao
import com.github.henriquechsf.syscredentialapp.data.model.Person
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonRepository @Inject constructor(
    private val personDao: PersonDao,
) {

    fun getAll(query: String? = ""): Flow<List<Person>> = personDao.getAll(query)

    suspend fun getById(personId: Long) = personDao.getById(personId)

    suspend fun insert(person: Person): Long = personDao.insert(person)

    suspend fun delete(person: Person) = personDao.delete(person)
}