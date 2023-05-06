package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegistrationListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import com.github.henriquechsf.syscredentialapp.ui.code_scanner.CaptureAct
import com.github.henriquechsf.syscredentialapp.ui.event_detail.ManualRegistrationFragment.Companion.CREDENTIAL_KEY
import com.github.henriquechsf.syscredentialapp.ui.event_detail.ManualRegistrationFragment.Companion.CREDENTIAL_RESULT
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.util.Calendar


@AndroidEntryPoint
class RegistrationListFragment :
    BaseFragment<FragmentRegistrationListBinding, RegistrationListViewModel>() {

    private val args: RegistrationListFragmentArgs by navArgs()
    private lateinit var event: Event

    override val viewModel: RegistrationListViewModel by viewModels()

    private val registrationAdapter by lazy { RegistrationAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event = args.event

        getRegistrations(event.id)
        initClicks()
        setupRecyclerView()
        observerRegistrationList()
        observerScanResult()
        observerCountRegistrations()
        manualRegistration()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_registration, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_report -> {
                // TODO: call share report here
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Registration Report")
                    type = "text/csv"
                }
                startActivity(shareIntent)
            }
        }
        return super.onOptionsItemSelected(item)
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
                    binding.layout.snackBar(getString(R.string.success_scan, result.data))
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(R.color.green_300))
                }
                is ResultState.Error -> {
                    binding.layout.snackBar("Erro: ${result.message}")
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(R.color.red_500))
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
        fabQrcodeScan.setOnClickListener {
            scanCode()
        }

        fabManualScan.setOnClickListener {
            val action = RegistrationListFragmentDirections
                .actionRegistrationListFragmentToManualRegistrationFragment(eventId = event.id)
            findNavController().navigate(action)
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
            binding.layout.snackBar(getString(R.string.cancelled_scan))
        }
    }

    private fun manualRegistration() {
        setFragmentResultListener(CREDENTIAL_KEY) { _, bundle ->
            bundle.getString(CREDENTIAL_RESULT)?.let { credential ->
                viewModel.insertRegistration(credential, event.id)
            }
        }
    }
}