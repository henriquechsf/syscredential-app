package com.github.henriquechsf.syscredentialapp.ui.persons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.data.repository.PersonRepository
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonsListViewModel @Inject constructor(
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _personList = MutableStateFlow<ResultState<List<Person>>>(ResultState.Empty())
    val personList = _personList.asStateFlow()

    init {
        fetch()
    }

    fun insertPerson(person: Person) = viewModelScope.launch {
        personRepository.insert(person)
    }

    fun removePerson(person: Person) = viewModelScope.launch {
        personRepository.delete(person)
    }

    fun fetch(query: String? = "") = viewModelScope.launch {
        personRepository.getAll(query).collectLatest { persons ->
            if (persons.isEmpty()) {
                _personList.value = ResultState.Empty()
            } else {
                _personList.value = ResultState.Success(persons)
            }
        }
    }
}