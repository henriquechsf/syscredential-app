package com.github.henriquechsf.syscredentialapp.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonsListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment

class PersonsListFragment : BaseFragment<FragmentPersonsListBinding, PersonsListViewModel>() {

    override val viewModel: PersonsListViewModel by viewModels()

    private val personListAdapter by lazy { PersonsListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        personListAdapter.persons = listOf(
            Person(10, "Henrique", "Tecnologia", "Desenvolvedor"),
            Person(11, "Guilherme", "Tecnologia", "Bebe"),
            Person(12, "Daniele", "Contabilidade", "Contadora"),
        )
    }

    private fun setupRecyclerView() = with(binding) {
        rvListPersons.apply {
            adapter = personListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPersonsListBinding = FragmentPersonsListBinding
        .inflate(inflater, container, false)
}