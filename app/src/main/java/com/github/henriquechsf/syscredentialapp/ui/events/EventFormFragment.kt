package com.github.henriquechsf.syscredentialapp.ui.events

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.henriquechsf.syscredentialapp.databinding.FragmentEventFormBinding
import com.github.henriquechsf.syscredentialapp.ui.base.BaseFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EventFormFragment : BaseFragment<FragmentEventFormBinding, EventsListViewModel>() {

    override val viewModel: EventsListViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
            val formattedDate = dateFormatter.format(Date(it))
            binding.edtDate.setText(formattedDate)
        }

        binding.edtDate.setOnClickListener {
            datePicker.show(childFragmentManager, "DATE_PICKER")
        }

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val pickedHour = timePicker.hour
            val pickedMinute = timePicker.minute

            val formattedTime: String = when {
                pickedHour > 12 -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour - 12}:0${timePicker.minute} pm"
                    } else {
                        "${timePicker.hour - 12}:${timePicker.minute} pm"
                    }
                }
                pickedHour == 12 -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour}:0${timePicker.minute} pm"
                    } else {
                        "${timePicker.hour}:${timePicker.minute} pm"
                    }
                }
                pickedHour == 0 -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour + 12}:0${timePicker.minute} am"
                    } else {
                        "${timePicker.hour + 12}:${timePicker.minute} am"
                    }
                }
                else -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour}:0${timePicker.minute} am"
                    } else {
                        "${timePicker.hour}:${timePicker.minute} am"
                    }
                }
            }

            binding.edtHour.setText(formattedTime)
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