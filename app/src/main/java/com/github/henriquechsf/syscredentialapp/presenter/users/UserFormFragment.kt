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
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonFormBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.alertRemove
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserFormFragment : BaseFragment<FragmentPersonFormBinding>() {

    //private val args: PersonFormFragmentArgs by navArgs()
    private var user: User? = null

    private val viewModel: UserListViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPersonFormBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        /*
        args.user?.let {
            setHasOptionsMenu(true)
            user = it
            bindUpdateFormData(it)
        }
         */

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
                user?.let {
                    alertRemove {
                        //viewModel.removePerson(it)
                        findNavController().popBackStack()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUpdateFormData(person: User) = with(binding) {
        edtCod.setText(user?.id)
        edtCod.isEnabled = false
        edtName.setText(person.name)
    }

    private fun initFieldListeners() = with(binding) {
        edtCod.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateField(edtCod, tilCod)
            }
        }
        edtCod.addTextChangedListener {
            if (binding.tilCod.error != null) {
                validateField(edtCod, tilCod)
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
            validateField(edtCod, tilCod)
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