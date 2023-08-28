package com.github.henriquechsf.syscredentialapp.presenter.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.henriquechsf.syscredentialapp.databinding.FragmentManualRegistrationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManualRegistrationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentManualRegistrationBinding? = null
    private val binding: FragmentManualRegistrationBinding get() = _binding!!

    private val args: ManualRegistrationFragmentArgs by navArgs()

    private val viewModel: RegistrationListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManualRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
    }

    private fun initClicks() = with(binding) {
        val eventId = args.eventId

        btnRegister.setOnClickListener {
            val credential = edtRegistration.text.toString().trim()

            if (credential.isNotEmpty()) {
                setFragmentResult(CREDENTIAL_KEY, bundleOf(CREDENTIAL_RESULT to credential))
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        const val CREDENTIAL_RESULT = "credential"
        const val CREDENTIAL_KEY = "credential"
    }
}