package com.github.henriquechsf.syscredentialapp.ui.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventsListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment

class EventsListFragment : BaseFragment<FragmentEventsListBinding, EventsListViewModel>() {

    override val viewModel: EventsListViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventsListBinding = FragmentEventsListBinding.inflate(inflater, container, false)
}