package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailFragment : BaseFragment<FragmentEventDetailBinding, EventDetailViewModel>() {

    private val args: EventDetailFragmentArgs by navArgs()
    private lateinit var event: Event

    override val viewModel: EventDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event = args.event
        bindingEventData()
        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnRegistration.setOnClickListener {
            val action = EventDetailFragmentDirections
                .actionEventDetailFragmentToRegistrationListFragment(eventId = event.id)
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_event_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_event -> {
                findNavController().navigate(R.id.action_eventDetailFragment_to_eventFormFragment)
                true
            }
            R.id.menu_remove_event -> {
                toast("Remove Event")
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindingEventData() = with(binding) {
        tvEventTitle.text = event.title
        tvEventDescription.text = event.description
        tvDatetime.text = formatDateString(event.datetime)
        tvLocation.text = event.local
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventDetailBinding = FragmentEventDetailBinding.inflate(inflater, container, false)
}