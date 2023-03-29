package com.github.henriquechsf.syscredentialapp.ui.events

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
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventsListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventsListFragment : BaseFragment<FragmentEventsListBinding, EventsListViewModel>() {

    override val viewModel: EventsListViewModel by viewModels()

    private val eventsAdapter by lazy { EventsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        clickItemAdapter()
        observerEventList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_events, menu)
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
                    result.data?.let {
                        binding.tvEmptyEvents.hide()
                        eventsAdapter.events = it.toList()
                    }
                }
                is ResultState.Empty -> {
                    binding.tvEmptyEvents.show()
                }
                else -> {}
            }
        }
    }

    private fun clickItemAdapter() {
        eventsAdapter.setOnClickListener { event ->
            val action = EventsListFragmentDirections
                .actionEventsListFragmentToEventFormFragment()
            findNavController().navigate(action)
        }

        eventsAdapter.setRegisterClickListener { event ->
            val action = EventsListFragmentDirections
                .actionEventsListFragmentToRegistrationListFragment(event)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvListEvents.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventsListBinding = FragmentEventsListBinding
        .inflate(inflater, container, false)
}