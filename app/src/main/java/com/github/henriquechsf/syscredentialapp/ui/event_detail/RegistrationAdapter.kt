package com.github.henriquechsf.syscredentialapp.ui.event_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.databinding.ItemEventRegistrationBinding

class RegistrationAdapter : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventRegistrationBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Registration>() {
        override fun areItemsTheSame(oldItem: Registration, newItem: Registration): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.eventId == newItem.eventId &&
                    oldItem.personId == newItem.personId &&
                    oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: Registration, newItem: Registration): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var registrations: List<Registration>
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
            tvPersonName.text = registration.personId.toString()
            chipTime.text = registration.createdAt
        }
    }


}