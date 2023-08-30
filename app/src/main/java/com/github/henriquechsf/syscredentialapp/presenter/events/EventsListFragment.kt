package com.github.henriquechsf.syscredentialapp.presenter.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventsListBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsListFragment : BaseFragment<FragmentEventsListBinding>() {

    private val eventsListViewModel: EventsListViewModel by viewModels()

    private val eventsAdapter by lazy { EventsAdapter() }

    private var eventList = listOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEventsListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        getEventList()
        setupRecyclerView()
        configSearchView()
        initListeners()
    }

    // TODO: update method Deprecated
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_register, menu)

        val item = menu.findItem(R.id.menu_search)
        binding.searchView.setMenuItem(item)

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

    private fun getEventList() {
        eventsListViewModel.getEventList().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is ResultState.Loading -> {
                    binding.progressBar.show()
                }
                is ResultState.Success -> {
                    binding.progressBar.hide()

                    stateView.data?.let {
                        binding.rvListEvents.show()

                        eventList = it
                        eventsAdapter.events = eventList
                    }
                }
                is ResultState.Error -> {
                    binding.progressBar.hide()
                    toast(message = stateView.message ?: "")
                }
                is ResultState.Empty -> {
                    binding.progressBar.hide()
                    binding.tvEmptyEvents.show()
                }
            }
        }
    }

    private fun initListeners() {
        eventsAdapter.setOnClickListener { event ->
            val action = EventsListFragmentDirections
                .actionEventsListFragmentToEventFormFragment(event)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvListEvents.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun configSearchView() = with(binding) {
        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText.isNotEmpty()) {
                    val newList = eventList.filter { event ->
                        event.title.contains(newText, true)
                    }
                    eventsAdapter.events = newList
                    true
                } else {
                    eventsAdapter.events = eventList
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
                eventsAdapter.events = eventList
            }

            override fun onSearchViewClosedAnimation() {}
            override fun onSearchViewShown() {}
            override fun onSearchViewShownAnimation() {}
        })
    }
}