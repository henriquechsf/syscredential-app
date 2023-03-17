package com.github.henriquechsf.syscredentialapp.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        clickItemAdapter()
        observeEventList()
    }

    private fun observeEventList() = lifecycleScope.launch {
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
                    binding.rvListEvents.hide()
                }
                else -> {}
            }
        }
    }

    private fun clickItemAdapter() {
        eventsAdapter.setOnClickListener { event ->
            val action = EventsListFragmentDirections
                .actionEventsListFragmentToEventDetailFragment(event)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvListEvents.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventsListBinding = FragmentEventsListBinding.inflate(inflater, container, false)
}