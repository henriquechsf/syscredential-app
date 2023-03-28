package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegistrationListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import com.github.henriquechsf.syscredentialapp.ui.code_scanner.CaptureAct
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.toast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegistrationListFragment :
    BaseFragment<FragmentRegistrationListBinding, RegistrationListViewModel>() {

    private val args: RegistrationListFragmentArgs by navArgs()

    override val viewModel: RegistrationListViewModel by viewModels()

    private val registrationAdapter by lazy { RegistrationAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRegistrations(args.eventId)
        initListeners()
        setupRecyclerView()
        observerRegistrationList()
    }

    private fun getRegistrations(eventId: Int) = lifecycleScope.launch {
        viewModel.fetchRegistrations(eventId)
    }

    private fun observerRegistrationList() = lifecycleScope.launch {
        viewModel.registrationsList.collect {result ->
            when (result) {
                is ResultState.Success -> {
                    result.data?.let {
                        binding.tvEmptyRegistrations.hide()
                        registrationAdapter.registrations = it.toList()
                    }
                }
                is ResultState.Empty -> {
                    binding.tvEmptyRegistrations.show()
                }
                else -> {}
            }
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvEventRegistrations.apply {
            adapter = registrationAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationListBinding =
        FragmentRegistrationListBinding.inflate(inflater, container, false)

    private fun initListeners() = with(binding) {
        binding.fabQrcodeScan.setOnClickListener {
            scanCode()
        }
    }

    private fun scanCode() {
        val options = ScanOptions().apply {
            setPrompt(getString(R.string.volume_up_flash_scan))
            setBeepEnabled(true)
            setOrientationLocked(true)
            captureActivity = CaptureAct::class.java
        }
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            toast("Scanned: ${result.contents}", Toast.LENGTH_LONG)
        } else {
            toast("Cancelled", Toast.LENGTH_LONG)
        }
    }


}