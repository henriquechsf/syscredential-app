package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment

class EventDetailFragment : BaseFragment<FragmentEventDetailBinding, EventDetailViewModel>() {

    override val viewModel: EventDetailViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventDetailBinding = FragmentEventDetailBinding.inflate(inflater, container, false)
}