package com.example.nimantran.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemMyGuestListBinding
import com.example.nimantran.models.user.Guest

class MyGuestListAdapter(
    private val context: Context,
    private val cardListener: (Guest) -> Unit,
) : ListAdapter<Guest, MyGuestListAdapter.ViewHolder>(MyGuestListDiffUtil()
) {
 class ViewHolder(
        private val binding: ItemMyGuestListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            guest: Guest,
            cardListener: (Guest) -> Unit,
        ) {
            binding.guest = guest
            binding.cardViewMyGuest.setOnClickListener {
                cardListener(guest)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyGuestListBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), cardListener)
}
class MyGuestListDiffUtil : DiffUtil.ItemCallback<Guest>() {
    override fun areItemsTheSame(oldItem: Guest, newItem: Guest): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Guest, newItem: Guest): Boolean {
        return oldItem.phone == newItem.phone
    }
}