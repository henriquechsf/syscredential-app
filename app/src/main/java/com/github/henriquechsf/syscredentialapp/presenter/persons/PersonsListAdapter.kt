package com.github.henriquechsf.syscredentialapp.presenter.persons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.databinding.ItemPersonListBinding

class PersonsListAdapter : RecyclerView.Adapter<PersonsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPersonListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.info1 == newItem.info1 &&
                    oldItem.info2 == newItem.info2
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var persons: List<Person>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPersonListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = persons.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = persons[position]
        holder.binding.apply {
            tvPersonName.text = person.name
            tvPersonInfo1.text = person.info1
            tvPersonInfo2.text = person.info2
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { clicked ->
                clicked(person)
            }
        }
    }

    private var onItemClickListener: ((Person) -> Unit)? = null
    fun setOnClickListener(listener: (Person) -> Unit) {
        onItemClickListener = listener
    }
}