package com.github.henriquechsf.syscredentialapp.presenter.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentLoginBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnForgotAccount.setOnClickListener {
        }
        btnRegister.setOnClickListener {
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
}