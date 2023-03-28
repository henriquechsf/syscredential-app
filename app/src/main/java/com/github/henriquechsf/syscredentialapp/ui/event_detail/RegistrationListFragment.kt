package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegistrationListBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.code_scanner.CaptureAct
import com.github.henriquechsf.syscredentialapp.util.toast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class RegistrationListFragment :
    BaseFragment<FragmentRegistrationListBinding, RegistrationListViewModel>() {

    override val viewModel: RegistrationListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationListBinding =
        FragmentRegistrationListBinding.inflate(inflater, container, false)

    private fun initListeners() = with(binding) {
        binding.fabQrcodeScan.setOnClickListener {
            //findNavController().navigate(R.id.action_registrationListFragment_to_codeScannerFragment)
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