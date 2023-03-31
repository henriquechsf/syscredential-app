package com.github.henriquechsf.syscredentialapp.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonFormBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonFormFragment : BaseFragment<FragmentPersonFormBinding, PersonsListViewModel>() {

    override val viewModel: PersonsListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        initFieldListeners()
    }

    private fun initFieldListeners() = with(binding) {
        edtName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateFields(edtName, tilName)
            }
        }
        edtName.addTextChangedListener {
            if (binding.tilName.error != null) {
                validateFields(edtName, tilName)
            }
        }
    }

    private fun initClicks() = with(binding) {
        btnSave.setOnClickListener {
            submit()
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun submit() = with(binding) {

        val isValid = listOf(
            validateFields(edtName, tilName),
        ).all { it }

        if (isValid) {
            val person = Person(
                name = edtName.text.toString().trim(),
                info1 = edtInfo1.text.toString().trim(),
                info2 = edtInfo2.text.toString().trim()
            )

            viewModel.insertPerson(person)

            toast(getString(R.string.person_saved_successfully))
            findNavController().popBackStack()
        }
    }

    private fun validateFields(
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

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonFormBinding =
        FragmentPersonFormBinding.inflate(inflater, container, false)
}