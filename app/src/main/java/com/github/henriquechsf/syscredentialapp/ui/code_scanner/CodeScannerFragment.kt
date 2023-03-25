package com.github.henriquechsf.syscredentialapp.ui.code_scanner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentCodeScannerBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment

class CodeScannerFragment : BaseFragment<FragmentCodeScannerBinding, CodeScannerViewModel>() {

    override val viewModel: CodeScannerViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCodeScannerBinding =
        FragmentCodeScannerBinding.inflate(inflater, container, false)
}