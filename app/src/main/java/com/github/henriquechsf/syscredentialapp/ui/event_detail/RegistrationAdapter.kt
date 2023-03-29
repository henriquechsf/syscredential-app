package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.data.model.RegistrationUI
import com.github.henriquechsf.syscredentialapp.databinding.ItemEventRegistrationBinding
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.formatTime

class RegistrationAdapter : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventRegistrationBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<RegistrationUI>() {
        override fun areItemsTheSame(oldItem: RegistrationUI, newItem: RegistrationUI): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.eventId == newItem.eventId &&
                    oldItem.personId == newItem.personId &&
                    oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: RegistrationUI, newItem: RegistrationUI): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var registrations: List<RegistrationUI>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventRegistrationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = registrations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val registration = registrations[position]
        holder.binding.apply {
            tvPersonName.text = registration.personName
            tvPersonInfo1.text = registration.personInfo1
            tvRegistrationTime.text = formatTime(registration.createdAt)
            tvRegistrationDate.text = formatDateString(registration.createdAt, false)
        }
    }


}