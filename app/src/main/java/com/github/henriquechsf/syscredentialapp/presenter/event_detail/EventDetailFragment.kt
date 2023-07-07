package com.github.henriquechsf.syscredentialapp.presenter.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailFragment : BaseFragment<FragmentEventDetailBinding, EventDetailViewModel>() {

    override val viewModel: EventDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_event_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_event -> {
                true
            }
            R.id.menu_remove -> {
                toast("Remove Event")
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventDetailBinding = FragmentEventDetailBinding.inflate(inflater, container, false)
}