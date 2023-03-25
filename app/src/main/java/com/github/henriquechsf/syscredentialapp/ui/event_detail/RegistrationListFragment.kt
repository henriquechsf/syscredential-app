package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegistrationListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment

class RegistrationListFragment :
    BaseFragment<FragmentRegistrationListBinding, RegistrationListViewModel>() {

    override val viewModel: RegistrationListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        binding.fabQrcodeScan.setOnClickListener {
            findNavController().navigate(R.id.action_registrationListFragment_to_codeScannerFragment)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationListBinding =
        FragmentRegistrationListBinding.inflate(inflater, container, false)


}