package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.persons.PersonsListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailFragment : BaseFragment<FragmentEventDetailBinding, EventDetailViewModel>() {

    private val args: EventDetailFragmentArgs by navArgs()
    private lateinit var event: Event

    override val viewModel: EventDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event = args.event
        bindingEventData()
    }

    private fun bindingEventData() = with(binding) {
        tvEventTitle.text = event.title
        tvEventDescription.text = event.description
        tvDatetime.text = event.datetime.toString()
        tvLocation.text = event.local
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventDetailBinding = FragmentEventDetailBinding.inflate(inflater, container, false)
}