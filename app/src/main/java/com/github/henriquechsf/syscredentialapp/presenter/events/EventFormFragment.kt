package com.github.henriquechsf.syscredentialapp.presenter.events

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.BottomSheetImageBinding
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventFormBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.Constants
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import com.github.henriquechsf.syscredentialapp.util.alertRemove
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.formatTime
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.loadImage
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class EventFormFragment : BaseFragment<FragmentEventFormBinding>() {

    private val args: EventFormFragmentArgs by navArgs()
    private var event: Event? = null

    private var imageEvent: String? = null
    private var currentPhotoPath: String? = null

    private val viewModel: EventsListViewModel by viewModels()
    private lateinit var eventDateTime: LocalDateTime

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEventFormBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        args.event?.let {
            setHasOptionsMenu(true)
            event = it
            eventDateTime = LocalDateTime.parse(it.datetime)
            bindUpdateFormData(it)
        }

        initDatePickerDialog()
        initTimePickerDialog()
        initListeners()
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
                    event?.let {
                        safeRemoveEvent(it)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUpdateFormData(event: Event) = with(binding) {
        if (event.image.isNotEmpty()) {
            loadImage(imgEvent, event.image)
        } else {
            imgEvent.setImageResource(R.drawable.image_placeholder)
        }
        edtTitle.setText(event.title)
        edtLocal.setText(event.local)
        edtDate.setText(formatDateString(event.datetime, withTime = false))
        edtHour.setText(formatTime(event.datetime))
    }


    private fun initListeners() = with(binding) {
        imgEvent.setOnClickListener { showBottomSheetPickImage() }

        btnSave.setOnClickListener {
            imageEvent?.let {
                saveEventWithImage(it)
            } ?: run {
                submit()
            }
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initFieldListeners() = with(binding) {
        edtTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateField(edtTitle, tilTitle)
            }
        }
        edtTitle.addTextChangedListener {
            if (binding.tilTitle.error != null) {
                validateField(edtTitle, tilTitle)
            }
        }

        edtLocal.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateField(edtLocal, tilLocal)
            }
        }
        edtLocal.addTextChangedListener {
            if (binding.tilLocal.error != null) {
                validateField(edtLocal, tilLocal)
            }
        }

        edtDate.addTextChangedListener {
            if (binding.tilDate.error != null) {
                validateField(edtDate, tilDate)
            }
        }

        edtHour.addTextChangedListener {
            if (binding.tilHour.error != null) {
                validateField(edtHour, tilHour)
            }
        }
    }

    private fun submit() = with(binding) {

        val isValid = listOf(
            validateField(edtTitle, tilTitle),
            validateField(edtLocal, tilLocal),
            validateField(edtDate, tilDate),
            validateField(edtHour, tilHour),
        ).all { it }

        if (isValid) {
            val eventToSave = Event(
                id = event?.id ?: FirebaseHelper.getUUID(),
                title = edtTitle.text.toString().trim(),
                local = edtLocal.text.toString().trim(),
                datetime = eventDateTime.toString().trim(),
                createdAt = event?.createdAt ?: LocalDateTime.now().toString(),
                image = event?.image ?: ""
            )
            saveEvent(eventToSave)
        }
    }

    private fun saveEventWithImage(image: String) {
        event?.id?.let { eventId ->
            viewModel.saveImageEvent(eventId, image).observe(viewLifecycleOwner) { stateView ->
                when (stateView) {
                    is ResultState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is ResultState.Success -> {
                        stateView.data?.let { event?.image = it }
                        submit()
                    }
                    is ResultState.Error -> {
                        binding.progressBar.isVisible = false
                        toast(message = stateView.message ?: "")
                    }
                }
            }
        }
    }

    private fun saveEvent(event: Event) {
        viewModel.saveEvent(event).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is ResultState.Loading -> {
                    binding.progressBar.show()
                }
                is ResultState.Success -> {
                    binding.progressBar.hide()
                    binding.layout.snackBar(getString(R.string.saved_successfully))
                    val action =
                        EventFormFragmentDirections.actionEventFormFragmentToEventDetailFragment(
                            event,
                            event.title
                        )
                    findNavController().navigate(action)
                }
                is ResultState.Error -> {
                    binding.progressBar.hide()
                    toast(message = stateView.message ?: "")
                }
                else -> {}
            }
        }
    }

    private fun safeRemoveEvent(event: Event) {
        event.deletedAt = LocalDateTime.now().toString()

        viewModel.saveEvent(event).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is ResultState.Loading -> {
                    binding.progressBar.show()
                }
                is ResultState.Success -> {
                    binding.progressBar.hide()
                    binding.layout.snackBar(getString(R.string.removed_successfully))
                    findNavController().popBackStack(R.id.eventsListFragment, false)
                }
                is ResultState.Error -> {
                    binding.progressBar.hide()
                    toast(message = stateView.message ?: "")
                }
                else -> {}
            }
        }
    }

    private fun initDatePickerDialog() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date_event))
            .setSelection(today)
            .setCalendarConstraints(constraintBuilder.build())
            .build()

        binding.edtDate.setOnClickListener {
            datePicker.show(childFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            eventDateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(dateInMillis), ZoneId.of("UTC"))
            binding.edtDate.setText(eventDateTime.format(dateFormatter))
        }
    }

    private fun initTimePickerDialog() {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val pickedHour = timePicker.hour
            val pickedMinute = timePicker.minute

            eventDateTime.with(LocalTime.of(pickedHour, pickedMinute))
            eventDateTime = LocalDateTime.of(
                eventDateTime.toLocalDate(),
                LocalTime.of(pickedHour, pickedMinute)
            )

            binding.edtHour.setText(LocalTime.of(pickedHour, pickedMinute).toString())
        }

        binding.edtHour.setOnClickListener {
            timePicker.show(childFragmentManager, "TIME_PICKER")
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

    // IMAGE PICKER
    // TODO: refactor implementation to own file

    private fun showBottomSheetPickImage() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        val bottomSheetBinding: BottomSheetImageBinding =
            BottomSheetImageBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.btnCamera.setOnClickListener {
            checkPermissionCamera()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnGallery.setOnClickListener {
            openGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun checkPermissionCamera() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openCamera()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                toast(message = "Permissao Negada")
            }
        }
        showDialogPermissionDenied(
            permissionlistener = permissionlistener,
            permission = Manifest.permission.CAMERA,
            message = R.string.text_message_camera_denied_profile_fragment
        )
    }

    private fun showDialogPermissionDenied(
        permissionlistener: PermissionListener,
        permission: String,
        message: Int
    ) {
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedTitle("Permissao negada")
            .setDeniedMessage(message)
            .setDeniedCloseButtonText("Nao")
            .setGotoSettingButtonText("Sim")
            .setPermissions(permission)
            .check();
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageSelected = result.data!!.data
            imageEvent = imageSelected.toString()

            if (imageSelected != null) {
                binding.imgEvent.setImageBitmap(getBitmap(imageSelected))
            }
        }
    }

    private fun getBitmap(pathUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, pathUri)
            } else {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, pathUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            toast(message = "Não foi possível abrir a câmera do dispositivo")
        }

        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                Constants.APP_FILE_PROVIDER,
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale("pt", "BR")).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        currentPhotoPath = image.absolutePath
        return image
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath!!)
            binding.imgEvent.setImageURI(Uri.fromFile(file))

            imageEvent = file.toURI().toString()
        }
    }
}