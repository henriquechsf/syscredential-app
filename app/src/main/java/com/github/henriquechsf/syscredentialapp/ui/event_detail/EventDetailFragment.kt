package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailFragment : BaseFragment<FragmentEventDetailBinding, EventDetailViewModel>() {

    override val viewModel: EventDetailViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventDetailBinding = FragmentEventDetailBinding.inflate(inflater, container, false)
}