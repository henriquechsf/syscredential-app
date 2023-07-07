package com.github.henriquechsf.syscredentialapp.presenter.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentProfileBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
}