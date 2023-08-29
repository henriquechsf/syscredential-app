package com.github.henriquechsf.syscredentialapp.presenter.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.FragmentUserFormBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.alertRemove
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserFormFragment : BaseFragment<FragmentUserFormBinding>() {

    private val args: UserFormFragmentArgs by navArgs()
    private lateinit var user: User

    private val viewModel: UserListViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserFormBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        user = args.user

        bindUserFormData(user)
        initClicks()
        initFieldListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_form, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_remove -> {
                alertRemove {
                    findNavController().popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUserFormData(user: User) = with(binding) {
        edtId.setText(user.id)
        edtName.setText(user.name)
    }

    private fun initFieldListeners() = with(binding) {
        edtId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateField(edtId, tilId)
            }
        }
        edtId.addTextChangedListener {
            if (binding.tilId.error != null) {
                validateField(edtId, tilId)
            }
        }

        edtName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateField(edtName, tilName)
            }
        }
        edtName.addTextChangedListener {
            if (binding.tilName.error != null) {
                validateField(edtName, tilName)
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
            validateField(edtName, tilName),
            validateField(edtId, tilId)
        ).all { it }

        if (isValid) {

            //viewModel.insertPerson(person)

            findNavController().popBackStack()
            layout.snackBar(getString(R.string.saved_successfully))
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