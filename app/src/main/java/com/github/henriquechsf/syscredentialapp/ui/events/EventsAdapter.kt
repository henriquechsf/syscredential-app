package com.github.henriquechsf.syscredentialapp.ui.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.databinding.ItemEventListBinding
import com.github.henriquechsf.syscredentialapp.util.formatDateString

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.description == newItem.description &&
                    oldItem.local == newItem.local &&
                    oldItem.datetime == newItem.datetime
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var events: List<Event>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.binding.apply {
            tvEventTitle.text = event.title
            tvEventDescription.text = event.description
            tvEventLocation.text = event.local
            tvEventDate.text = formatDateString(date = event.datetime)

            btnRegister.setOnClickListener {
                onRegisterClickListener?.let { registerClicked ->
                    registerClicked(event)
                }
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { clicked ->
                clicked(event)
            }
        }
    }

    private var onItemClickListener: ((Event) -> Unit)? = null
    fun setOnClickListener(listener: (Event) -> Unit) {
        onItemClickListener = listener
    }

    private var onRegisterClickListener: ((Event) -> Unit)? = null
    fun setRegisterClickListener(listener: (Event) -> Unit) {
        onRegisterClickListener = listener
    }
}