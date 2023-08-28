package com.github.henriquechsf.syscredentialapp.presenter.persons

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonsListBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonsListFragment : BaseFragment<FragmentPersonsListBinding>() {

    private val viewModel: PersonsListViewModel by viewModels()

    private val personListAdapter by lazy { PersonsListAdapter() }

    private var personList = listOf<Person>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPersonsListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        setupRecyclerView()
        observerPersonList()
        clickItemAdapter()
        configSearchView()
    }

    private fun clickItemAdapter() {
        personListAdapter.setOnClickListener { person ->
            val action = PersonsListFragmentDirections
                .actionPersonsListFragmentToPersonFormFragment(person)
            findNavController().navigate(action)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_register, menu)

        val item = menu.findItem(R.id.menu_search)
        binding.searchView.setMenuItem(item)

        super.onCreateOptionsMenu(menu, inflater)
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
                        personList = it
                        personListAdapter.persons = personList
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

    private fun configSearchView() = with(binding) {
        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText.isNotEmpty()) {
                    val newList = personList.filter { person ->
                        person.name.contains(newText, true)
                    }
                    personListAdapter.persons = newList
                    true
                } else {
                    personListAdapter.persons = personList
                    false
                }
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })



        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                personListAdapter.persons = personList
            }

            override fun onSearchViewClosedAnimation() {}
            override fun onSearchViewShown() {}
            override fun onSearchViewShownAnimation() {}
        })
    }
}