package com.github.henriquechsf.syscredentialapp.presenter.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.EventStatus
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.data.model.UserRole
import com.github.henriquechsf.syscredentialapp.data.model.UserStatus
import com.github.henriquechsf.syscredentialapp.databinding.ItemUserListBinding
import com.github.henriquechsf.syscredentialapp.util.loadImage
import java.time.LocalDate
import java.time.LocalDateTime

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.apply {
            if (user.image.isNotEmpty()) loadImage(
                imgUser,
                user.image
            ) else imgUser.setImageResource(
                R.drawable.ic_image_profile
            )
            tvUserName.text = user.name
            tvDepartment.text = user.department

            val userStatus = handleUserStatus(user.status.name)
            val userRole = handleUserRole(user.role.name)

            tvUserStatus.text = holder.itemView.context.getString(userStatus.translation)
            cardUserStatus.setCardBackgroundColor(holder.itemView.context.getColor(userStatus.color))
            tvUserRole.text = holder.itemView.context.getString(userRole.translation)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { clicked ->
                clicked(user)
            }
        }
    }

    private var onItemClickListener: ((User) -> Unit)? = null
    fun setOnClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    private fun handleUserStatus(status: String): UserStatus {
        return when (status) {
            UserStatus.BLOCKED.name -> UserStatus.BLOCKED
            UserStatus.INACTIVE.name -> UserStatus.INACTIVE
            UserStatus.ACTIVE.name -> UserStatus.ACTIVE
            else -> UserStatus.INACTIVE
        }
    }

    private fun handleUserRole(role: String): UserRole {
        return when (role) {
            UserRole.ADMIN.name -> UserRole.ADMIN
            UserRole.PARTICIPANT.name -> UserRole.PARTICIPANT
            else -> UserRole.PARTICIPANT
        }
    }
}