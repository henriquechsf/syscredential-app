package com.github.henriquechsf.syscredentialapp.presenter.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.EventStatus
import com.github.henriquechsf.syscredentialapp.databinding.ItemEventListBinding
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.loadImage
import java.time.LocalDate
import java.time.LocalDateTime

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
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
            if (event.image.isNotEmpty()) {
                loadImage(imgEvent, event.image)
            } else {
                imgEvent.setImageResource(R.drawable.image_placeholder)
            }
            tvEventTitle.text = event.title
            tvEventLocation.text = event.local
            tvEventDate.text = formatDateString(date = event.datetime)

            val eventStatus = handleEventStatus(LocalDateTime.parse(event.datetime).toLocalDate())
            tvEventStatus.text = holder.itemView.context.getString(eventStatus.translation)
            cardEventStatus.setCardBackgroundColor(holder.itemView.context.getColor(eventStatus.color))
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { clicked ->
                clicked(event)
            }
        }
    }

    private fun handleEventStatus(eventDate: LocalDate): EventStatus {
        val today = LocalDateTime.now().toLocalDate()
        return when {
            eventDate.isBefore(today) -> EventStatus.FINISHED
            eventDate.isAfter(today) -> EventStatus.SCHEDULED
            else -> EventStatus.TODAY
        }
    }

    private var onItemClickListener: ((Event) -> Unit)? = null
    fun setOnClickListener(listener: (Event) -> Unit) {
        onItemClickListener = listener
    }
}