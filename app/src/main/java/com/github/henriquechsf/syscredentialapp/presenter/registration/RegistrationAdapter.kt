package com.github.henriquechsf.syscredentialapp.presenter.registration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.databinding.ItemEventRegistrationBinding
import com.github.henriquechsf.syscredentialapp.util.formatDateString
import com.github.henriquechsf.syscredentialapp.util.formatTime
import com.github.henriquechsf.syscredentialapp.util.loadImage

class RegistrationAdapter : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventRegistrationBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Registration>() {
        override fun areItemsTheSame(oldItem: Registration, newItem: Registration): Boolean {
            return oldItem.id == newItem.id
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
            if (registration.userImage.isNotEmpty()) loadImage(
                imgUser,
                registration.userImage
            ) else imgUser.setImageResource(
                R.drawable.ic_image_profile
            )
            tvUserName.text = registration.userName
            tvUserDepartment.text = registration.userDepartment
            tvRegistrationTime.text = formatTime(registration.createdAt)
            tvRegistrationDate.text = formatDateString(registration.createdAt, false)
        }
    }
}