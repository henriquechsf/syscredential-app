package com.github.henriquechsf.syscredentialapp.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
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
class PersonsListFragment : BaseFragment<FragmentPersonsListBinding, PersonsListViewModel>(),
    SearchView.OnQueryTextListener {

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
        inflater.inflate(R.menu.menu_register, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
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
                    if (binding.rvListPersons.isVisible.not()) {
                        binding.rvListPersons.show()
                    }

                    result.data?.let {
                        binding.tvEmptyPersons.hide()
                        personListAdapter.persons = it.toList()
                    }
                }
                is ResultState.Empty -> {
                    binding.rvListPersons.hide()
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) searchQuery(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) searchQuery(newText)
        return true
    }

    private fun searchQuery(query: String) {
        viewModel.fetch(query)
    }
}