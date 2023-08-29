package com.github.henriquechsf.syscredentialapp.presenter.events

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
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
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventFormBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.util.alertRemove
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.formatTime
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.snackBar
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class EventFormFragment : BaseFragment<FragmentEventFormBinding>() {

    private val args: EventFormFragmentArgs by navArgs()
    private var event: Event? = null

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
                event?.let {
                    alertRemove {
                        viewModel.removeEvent(it)
                        findNavController().popBackStack()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUpdateFormData(event: Event) = with(binding) {
        edtTitle.setText(event.title)
        edtLocal.setText(event.local)
        edtDate.setText(formatDateString(event.datetime, withTime = false))
        edtHour.setText(formatTime(event.datetime))
    }


    private fun initClicks() = with(binding) {

        btnSave.setOnClickListener {
            submit()
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
            val event = Event(
                title = edtTitle.text.toString().trim(),
                local = edtLocal.text.toString().trim(),
                datetime = eventDateTime.toString().trim()
            )

            saveProfile(event)

            layout.snackBar(getString(R.string.saved_successfully))
            findNavController().popBackStack()
        }
    }

    private fun saveProfile(event: Event) {
        viewModel.saveEvent(event).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is ResultState.Loading -> {
                    binding.progressBar.show()
                }
                is ResultState.Success -> {
                    binding.progressBar.hide()
                    toast(message = "Perfil atualizado com sucesso")
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
}