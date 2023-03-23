package com.github.henriquechsf.syscredentialapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegisterBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment

class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {

    override val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding = FragmentRegisterBinding
        .inflate(inflater, container, false)
}