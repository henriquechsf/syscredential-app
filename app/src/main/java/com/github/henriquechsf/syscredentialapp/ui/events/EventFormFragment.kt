package com.github.henriquechsf.syscredentialapp.ui.events

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventFormBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class EventFormFragment : BaseFragment<FragmentEventFormBinding, EventsListViewModel>() {

    override val viewModel: EventsListViewModel by viewModels()
    private lateinit var eventDateTime: LocalDateTime

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDatePickerDialog()
        initTimePickerDialog()
        initListeners()
    }

    private fun initListeners() = with(binding) {

        btnSave.setOnClickListener {
            val title = edtTitle.text.toString()
            val description = edtDescription.text.toString()
            val local = edtLocal.text.toString()

            val event = Event(
                title = title,
                description = description,
                local = local,
                datetime = eventDateTime.toString()
            )

            viewModel.insertEvent(event)
            Toast.makeText(requireContext(), "Cadastrado com sucesso", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
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

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEventFormBinding = FragmentEventFormBinding.inflate(inflater, container, false)
}