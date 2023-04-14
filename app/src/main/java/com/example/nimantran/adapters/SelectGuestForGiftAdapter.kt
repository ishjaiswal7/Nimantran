package com.example.nimantran.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemSelectMyGuestBinding
import com.example.nimantran.models.user.Guest

// will contain a checkbox and a textview for each guest
class SelectGuestAdapter(
    onGuestChecked: (Guest, Boolean) -> Unit,
) : ListAdapter<Guest, SelectGuestAdapter.ViewHolder>(GuestDiffCallback()) {
    private val _onGuestChecked = onGuestChecked
    var selectedGuestList = mutableListOf<Guest>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guest = getItem(position)
        holder.bind(guest, _onGuestChecked, selectedGuestList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        val binding: ItemSelectMyGuestBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            guest: Guest,
            onGuestChecked: (Guest, Boolean) -> Unit,
            selectedGuests: MutableList<Guest>
        ) {
            binding.guest = guest
            binding.checkBoxMyGuest.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedGuests.add(guest)
                } else {
                    selectedGuests.remove(guest)
                }
                onGuestChecked(guest,isChecked)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSelectMyGuestBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun getSelectedGuests(): List<Guest> {
        return selectedGuestList
    }
}

class GuestDiffCallback : DiffUtil.ItemCallback<Guest>() {
    override fun areItemsTheSame(oldItem: Guest, newItem: Guest): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Guest, newItem: Guest): Boolean {
        return oldItem == newItem
    }
}
