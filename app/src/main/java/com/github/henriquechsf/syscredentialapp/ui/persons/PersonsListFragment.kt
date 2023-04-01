package com.github.henriquechsf.syscredentialapp.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonsListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonsListFragment : BaseFragment<FragmentPersonsListBinding, PersonsListViewModel>() {

    override val viewModel: PersonsListViewModel by viewModels()

    private val personListAdapter by lazy { PersonsListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observerPersonList()
        clickItemAdapter()
    }

    private fun clickItemAdapter() {
        personListAdapter.setOnClickListener { person ->
            val action = PersonsListFragmentDirections
                .actionPersonsListFragmentToPersonFormFragment(person)
            findNavController().navigate(action)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                findNavController().navigate(R.id.action_personsListFragment_to_personFormFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_events, menu)
    }

    private fun setupRecyclerView() = with(binding) {
        rvListPersons.apply {
            adapter = personListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observerPersonList() = lifecycleScope.launch {
        viewModel.personList.collect { result ->
            when (result) {
                is ResultState.Success -> {
                    result.data?.let {
                        binding.tvEmptyPersons.hide()
                        personListAdapter.persons = it.toList()
                    }
                }
                is ResultState.Empty -> {
                    binding.tvEmptyPersons.show()
                }
                else -> {}
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPersonsListBinding = FragmentPersonsListBinding
        .inflate(inflater, container, false)
}