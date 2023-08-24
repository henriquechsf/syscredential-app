package com.github.henriquechsf.syscredentialapp.presenter.auth.forgot

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.henriquechsf.syscredentialapp.databinding.FragmentForgotBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotFragment : BaseFragment<FragmentForgotBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentForgotBinding.inflate(inflater, container, false)
}