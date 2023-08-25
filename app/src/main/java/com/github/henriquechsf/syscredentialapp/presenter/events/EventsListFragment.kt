package com.github.henriquechsf.syscredentialapp.presenter.events

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
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventsListBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventsListFragment : BaseFragment<FragmentEventsListBinding>(),
    SearchView.OnQueryTextListener {

    private val viewModel: EventsListViewModel by viewModels()

    private val eventsAdapter by lazy { EventsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEventsListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        setupRecyclerView()
        clickItemAdapter()
        observerEventList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_register, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.queryHint = getString(R.string.search)
        searchView?.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                findNavController().navigate(R.id.action_eventsListFragment_to_eventFormFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observerEventList() = lifecycleScope.launch {
        viewModel.eventList.collect { result ->
            when (result) {
                is ResultState.Success -> {
                    if (binding.rvListEvents.isVisible.not()) {
                        binding.rvListEvents.show()
                    }

                    result.data?.let {
                        binding.tvEmptyEvents.hide()
                        eventsAdapter.events = it.toList()
                    }
                }
                is ResultState.Empty -> {
                    binding.rvListEvents.hide()
                    binding.tvEmptyEvents.show()
                }
                else -> {}
            }
        }
    }

    private fun clickItemAdapter() {
        eventsAdapter.setOnClickListener { event ->
            val action = EventsListFragmentDirections
                .actionEventsListFragmentToEventFormFragment(event)
            findNavController().navigate(action)
        }

        eventsAdapter.setRegisterClickListener { event ->
            val action = EventsListFragmentDirections
                .actionEventsListFragmentToRegistrationListFragment(event, event.title)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvListEvents.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

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