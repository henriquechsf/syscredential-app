package com.github.henriquechsf.syscredentialapp.ui.persons

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
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonFormBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.alertRemove
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PersonFormFragment : BaseFragment<FragmentPersonFormBinding, PersonsListViewModel>() {

    private val args: PersonFormFragmentArgs by navArgs()
    private var person: Person? = null

    override val viewModel: PersonsListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.person?.let {
            setHasOptionsMenu(true)
            person = it
            bindUpdateFormData(it)
        }

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
                person?.let {
                    alertRemove {
                        viewModel.removePerson(it)
                        findNavController().popBackStack()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUpdateFormData(person: Person) = with(binding) {
        tvId.show()
        tvId.text = getString(R.string.label_id, person.id.toString())
        edtName.setText(person.name)
        edtInfo1.setText(person.info1)
        edtInfo2.setText(person.info2)
    }

    private fun initFieldListeners() = with(binding) {
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
        ).all { it }

        if (isValid) {
            val person = Person(
                id = person?.id ?: 0L,
                name = edtName.text.toString().trim(),
                info1 = edtInfo1.text.toString().trim(),
                info2 = edtInfo2.text.toString().trim()
            )

            viewModel.insertPerson(person)

            val message = if (person.id > 0) {
                R.string.updated_successfully
            } else {
                R.string.saved_successfully
            }
            findNavController().popBackStack()
            layout.snackBar(getString(message))
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

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonFormBinding =
        FragmentPersonFormBinding.inflate(inflater, container, false)
}