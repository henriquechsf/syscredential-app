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
import com.github.henriquechsf.syscredentialapp.data.model.Credential
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.BottomSheetCredentialBinding
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventDetailBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.loadImage
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import net.glxn.qrgen.android.QRCode
import java.time.LocalDateTime

@AndroidEntryPoint
class EventDetailFragment : BaseFragment<FragmentEventDetailBinding>() {

    private val args: EventDetailFragmentArgs by navArgs()
    private lateinit var event: Event

    private val eventDetailViewModel: EventDetailViewModel by viewModels()

    private lateinit var user: User

    private var credential: Credential? = null

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

        initObservers()
        getCredential()
        initListeners()
        configData()
    }

    private fun initListeners() {
        binding.btnShowCredential.setOnClickListener {
            credential?.id?.let {
                showBottomSheetCredential(credentialId = it)
            }
        }
        binding.btnConfirmPresence.setOnClickListener {
            event.id?.let { eventId ->
                val credential = Credential(
                    id = FirebaseHelper.getUserId(),
                    eventId = eventId,
                    userId = user.id,
                    userName = user.name,
                    userDepartment = user.department,
                    userImage = user.image,
                    createdAt = LocalDateTime.now().toString()
                )
                saveCredential(credential)
            }
        }
    }

    private fun initObservers() {
        eventDetailViewModel.userLogged.observe(viewLifecycleOwner) {
            user = it
        }
    }

    private fun configData() = with(binding) {
        if (event.image.isNotEmpty()) {
            loadImage(imgEvent, event.image)
        } else {
            imgEvent.setImageResource(R.drawable.image_placeholder)
        }
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

    private fun saveCredential(credential: Credential) {
        eventDetailViewModel.saveCredential(credential).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is ResultState.Loading -> {
                    binding.progressBar.show()
                }
                is ResultState.Success -> {
                    binding.progressBar.hide()
                    binding.layout.snackBar(getString(R.string.saved_successfully))
                    getCredential()
                }
                is ResultState.Error -> {
                    binding.progressBar.hide()
                    toast(message = stateView.message ?: "")
                }
                else -> {}
            }
        }
    }

    private fun getCredential() {
        event.id?.let { eventId ->

            eventDetailViewModel.getCredential(eventId, FirebaseHelper.getUserId())
                .observe(viewLifecycleOwner) { stateView ->
                    when (stateView) {
                        is ResultState.Loading -> {
                            binding.progressBar.show()
                        }
                        is ResultState.Success -> {
                            binding.progressBar.hide()

                            stateView.data?.let {
                                credential = it
                                binding.btnShowCredential.show()
                                binding.btnConfirmPresence.hide()
                            } ?: run {
                                credential = null
                                binding.btnConfirmPresence.show()
                                binding.btnShowCredential.hide()
                            }
                        }
                        is ResultState.Error -> {
                            binding.progressBar.hide()
                            binding.btnConfirmPresence.show()
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun showBottomSheetCredential(credentialId: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        val bottomSheetBinding: BottomSheetCredentialBinding =
            BottomSheetCredentialBinding.inflate(layoutInflater, null, false)

        val credentialCodeBitmap = QRCode.from(credentialId).bitmap()
        bottomSheetBinding.imgCredential.setImageBitmap(credentialCodeBitmap)

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }
}