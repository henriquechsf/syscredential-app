package com.github.henriquechsf.syscredentialapp.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.databinding.FragmentPersonFormBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonFormFragment : BaseFragment<FragmentPersonFormBinding, PersonsListViewModel>() {

    override val viewModel: PersonsListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
    }

    private fun initClicks() = with(binding) {
        btnSave.setOnClickListener {
            val name = edtName.text?.trim().toString()
            val info1 = edtInfo1.text?.trim().toString()
            val info2 = edtInfo2.text?.trim().toString()

            val person = Person(
                name = name,
                info1 = info1,
                info2 = info2
            )

            viewModel.insertPerson(person)
            toast(getString(R.string.person_saved_successfully))
            findNavController().popBackStack()
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonFormBinding =
        FragmentPersonFormBinding.inflate(inflater, container, false)
}