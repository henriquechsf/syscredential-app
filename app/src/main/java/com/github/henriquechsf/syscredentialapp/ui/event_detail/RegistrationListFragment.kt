package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.RegistrationUI
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegistrationListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.base.ResultState
import com.github.henriquechsf.syscredentialapp.ui.code_scanner.CaptureAct
import com.github.henriquechsf.syscredentialapp.ui.event_detail.ManualRegistrationFragment.Companion.CREDENTIAL_KEY
import com.github.henriquechsf.syscredentialapp.ui.event_detail.ManualRegistrationFragment.Companion.CREDENTIAL_RESULT
import com.github.henriquechsf.syscredentialapp.util.alert
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.github.henriquechsf.syscredentialapp.util.toast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException


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
                val csvHeader = "Nome,Info1,CheckIn,Evento"
                val csvData = createCsvData(registrationAdapter.registrations, csvHeader)
                val csvFile = saveCsvFile(csvData, "relatorio-credenciamento.csv")
                shareCsvFile(csvFile)
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
                    toast(getString(R.string.success_scan, result.data))
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(R.color.green_300))
                }
                is ResultState.Error -> {
                    toast(result.message.toString())
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
            toast(getString(R.string.cancelled_scan))
        }
    }

    private fun manualRegistration() {
        setFragmentResultListener(CREDENTIAL_KEY) { _, bundle ->
            bundle.getString(CREDENTIAL_RESULT)?.let { credential ->
                viewModel.insertRegistration(credential, event.id)
            }
        }
    }

    private fun createCsvData(registrations: List<RegistrationUI>, csvHeader: String): String {
        val csvData = StringBuilder().apply {
            append("$csvHeader\n")
            registrations.forEach {
                append("${it.personName},${it.personInfo1},${formatDateString(it.createdAt)},${event.title}\n")
            }
        }
        return csvData.toString()
    }

    private fun saveCsvFile(csvData: String, csvFileName: String): File {
        val csvFile = File(requireActivity().getExternalFilesDir(null), csvFileName)

        try {
            FileWriter(csvFile).use { writer ->
                writer.append(csvData)
                writer.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return csvFile
    }

    private fun shareCsvFile(csvFile: File) {
        val csvUri = FileProvider.getUriForFile(
            requireContext(),
            "com.github.henriquechsf.syscredentialapp.fileprovider",
            csvFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, csvUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share CSV"))
    }
}