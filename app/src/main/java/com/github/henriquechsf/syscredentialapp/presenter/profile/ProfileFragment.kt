package com.github.henriquechsf.syscredentialapp.presenter.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.FragmentProfileBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.util.hideKeyboard
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.loadImage
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val profileViewModel: ProfileViewModel by viewModels()

    private var user: User? = null

    private var imageProfile: String? = null
    private var currentPhotoPath: String? = null

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        getProfile()
        initListeners()
    }

    private fun initListeners() {
        //binding.imgUser.setOnClickListener { showBottomSheetPickImage() }
        binding.btnSave.setOnClickListener {
            if (user != null) {
                imageProfile?.let {
                    //saveProfileWithImage(it)
                } ?: run {
                    validateData()
                }
            }
        }
    }

    private fun configData() {
        user?.image?.let {
            if (it.isNotEmpty()) {
                loadImage(binding.imgUser, it)
            } else {
                binding.imgUser.setImageResource(R.drawable.ic_image_profile)
            }
        }
        binding.edtName.setText(user?.name)
        binding.edtEmail.setText(user?.email)
    }

    private fun validateData() = with(binding) {
        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (name.isNotEmpty() || email.isNotEmpty()) {
            hideKeyboard()

            user?.let {
                it.name = name
                it.email = email

                //saveProfile(it)
            }
        } else {
            //toast(message = getString(R.string.common_error_required_field))
        }
    }

    private fun getProfile() {
        profileViewModel.getProfile().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    stateView.data?.let { user = it }
                    configData()
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    toast(message = stateView.message ?: "")
                }
            }
        }
    }
}