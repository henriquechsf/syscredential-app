package com.github.henriquechsf.syscredentialapp.presenter.auth.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.databinding.FragmentForgotBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotFragment : BaseFragment<FragmentForgotBinding>() {

    private val forgotViewModel: ForgotViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentForgotBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
    }

    private fun initListeners() {
        binding.btnSend.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() = with(binding) {
        val email = edtEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            forgotAccount(email)
        } else {
            toast(message = "Digite todos os campos")
        }
    }

    private fun forgotAccount(email: String) {
        forgotViewModel.forgot(email).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false

                    findNavController().popBackStack()
                    toast(message = getString(R.string.send_email_success_forgot_fragment))
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    toast(message = stateView.message ?: "")
                }
            }
        }
    }
}