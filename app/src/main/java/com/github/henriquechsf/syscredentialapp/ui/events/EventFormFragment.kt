package com.github.henriquechsf.syscredentialapp.ui.events

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventFormBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.ui.validation.FormFieldText
import com.github.henriquechsf.syscredentialapp.ui.validation.validate
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class EventFormFragment : BaseFragment<FragmentEventFormBinding, EventsListViewModel>() {

    override val viewModel: EventsListViewModel by viewModels()
    private lateinit var eventDateTime: LocalDateTime

    // Fields
    private val fieldTitle by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilTitle,
            textInputEditText = binding.edtTitle,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(R.string.required_field)
                    else -> null
                }
            }
        )
    }
    private val fieldLocal by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilLocal,
            textInputEditText = binding.edtLocal,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(R.string.required_field)
                    value.length < 4 -> "Minimum 4 characters"
                    else -> null
                }
            }
        )
    }
    private val fieldDate by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilDate,
            textInputEditText = binding.edtDate,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(R.string.required_field)
                    else -> null
                }
            }
        )
    }
    private val fieldHour by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilHour,
            textInputEditText = binding.edtHour,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(R.string.required_field)
                    else -> null
                }
            }
        )
    }

    private val formFields by lazy {
        listOf(
            fieldTitle,
            fieldLocal,
            fieldDate,
            fieldHour
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDatePickerDialog()
        initTimePickerDialog()
        initClicks()
    }

    private fun initClicks() = with(binding) {

        btnSave.clicks().onEach {
            submit()
        }.launchIn(lifecycleScope)

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun submit() = lifecycleScope.launch {
        binding.btnSave.isEnabled = false

        fieldTitle.disable()
        if (formFields.validate(validateAll = true)) {
            fieldTitle.value
            fieldLocal.value

            val event = Event(
                title = fieldTitle.value!!,
                local = fieldLocal.value!!,
                datetime = eventDateTime.toString()
            )
            viewModel.insertEvent(event)

            findNavController().popBackStack()
            toast(getString(R.string.event_saved_successfully))
        }
        fieldTitle.enable()

        binding.btnSave.isEnabled = true
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

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventFormBinding = FragmentEventFormBinding.inflate(inflater, container, false)
}