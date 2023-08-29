package com.github.henriquechsf.syscredentialapp.presenter.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.ItemUserListBinding

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
            tvUserName.text = user.name
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
}