package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var event: Event

    override val viewModel: RegistrationListViewModel by viewModels()

    private val registrationAdapter by lazy { RegistrationAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event = args.event

        getRegistrations(event.id)
        initClicks()
        setupRecyclerView()
        observerRegistrationList()
        observerScanResult()
        observerCountRegistrations()
    }

    private fun observerCountRegistrations() = lifecycleScope.launch {
        viewModel.countRegistrations.collect {
            binding.tvCountRegistrations.text = it.toString()
        }
    }

    private fun getRegistrations(eventId: Int) = lifecycleScope.launch {
        viewModel.fetchRegistrations(eventId)
    }

    private fun observerRegistrationList() = lifecycleScope.launch {
        viewModel.registrationsList.collect { result ->
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

    private fun observerScanResult() = lifecycleScope.launch {
        viewModel.scanResult.collect { result ->
            when (result) {
                is ResultState.Success -> {
                    toast("Scanned: ${result.data}")
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(R.color.secondary))
                }
                is ResultState.Error -> {
                    toast("Error: ${result.message}")
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
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

    private fun initClicks() = with(binding) {
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
            val credential = result.contents
            viewModel.insertRegistration(credential, event.id)
        } else {
            toast(getString(R.string.cancelled_scan))
        }
    }
}