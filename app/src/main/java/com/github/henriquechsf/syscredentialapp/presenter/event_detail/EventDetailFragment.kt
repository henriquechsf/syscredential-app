package com.github.henriquechsf.syscredentialapp.presenter.event_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.BottomSheetCredentialBinding
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import net.glxn.qrgen.android.QRCode

@AndroidEntryPoint
class EventDetailFragment : BaseFragment<FragmentEventDetailBinding>() {

    private val args: EventDetailFragmentArgs by navArgs()
    private lateinit var event: Event

    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEventDetailBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        event = args.event

        configData()
        initListeners()
    }

    private fun initListeners() {
        binding.btnShowCredential.setOnClickListener { showBottomSheetCredential(credential = "1234") }
    }

    private fun configData() = with(binding) {
        tvEventTitle.text = event.title
        tvLocation.text = event.local
        tvDatetime.text = formatDateString(date = event.datetime)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_event_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_register -> {
                val action =
                    EventDetailFragmentDirections.actionEventDetailFragmentToRegistrationListFragment(
                        event,
                        event.title
                    )
                findNavController().navigate(action)
                true
            }
            R.id.menu_edit_event -> {
                val action =
                    EventDetailFragmentDirections.actionEventDetailFragmentToEventFormFragment(event)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showBottomSheetCredential(credential: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        val bottomSheetBinding: BottomSheetCredentialBinding =
            BottomSheetCredentialBinding.inflate(layoutInflater, null, false)

        val credentialCodeBitmap = QRCode.from(credential).bitmap()
        bottomSheetBinding.imgCredential.setImageBitmap(credentialCodeBitmap)

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }
}