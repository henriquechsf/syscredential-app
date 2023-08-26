package com.github.henriquechsf.syscredentialapp.presenter.auth.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegisterBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.presenter.profile.ProfileViewModel
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val registerViewModel: RegisterViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRegisterBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnEnter.setOnClickListener {
            submit()
        }
        btnLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun registerUser(user: User) {
        registerViewModel.register(user).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    saveProfile(user.copy(id = FirebaseHelper.getUserId()))
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false

                    Toast.makeText(
                        requireContext(),
                        stateView.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveProfile(user: User) {
        profileViewModel.saveProfile(user).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {}
                is StateView.Sucess -> {
                    toast("Perfil cadastrado com sucesso")
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    toast(message = stateView.message ?: "")
                }
            }
        }
    }

    private fun submit() = with(binding) {
        val isValid = listOf(
            validateField(edtName, tilName),
            validateField(edtEmail, tilEmail),
            validateField(edtPassword, tilPassword),
            validateField(edtConfirmPassword, tilConfirmPassword),
        ).all { it }

        if (isValid) {
            val user = User(
                name = edtName.text.toString().trim(),
                email = edtEmail.text.toString().trim(),
                password = edtPassword.text.toString().trim(),
            )
            registerUser(user)
        }
    }

    private fun validateField(
        editText: TextInputEditText,
        layout: TextInputLayout
    ): Boolean {
        val value = editText.text.toString()
        val errorMessage = when {
            value.isBlank() -> getString(R.string.required_field)
            else -> null
        }
        setTextInputLayoutStatus(layout, errorMessage)
        return errorMessage == null
    }

    private fun setTextInputLayoutStatus(
        layout: TextInputLayout,
        errorMessage: String?
    ) {
        if (errorMessage == null) {
            layout.isErrorEnabled = false
        }
        layout.error = errorMessage
    }
}