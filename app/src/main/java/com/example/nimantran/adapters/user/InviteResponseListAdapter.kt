package com.example.nimantran.adapters.user

import com.example.nimantran.models.user.Invite
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemMyInviteResponsesBinding


class InviteResponseListAdapter (
    private val context: Context,
    private val listener: (Invite) -> Unit
        ): ListAdapter<Invite, InviteResponseListAdapter.ViewHolder>(InviteResponseDiffUtil()) {
        class ViewHolder(
            private val binding: ItemMyInviteResponsesBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(invite: Invite) {
                binding.invite = invite
                binding.executePendingBindings()
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyInviteResponsesBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { listener(getItem(position)) }
    }
}
class InviteResponseDiffUtil : DiffUtil.ItemCallback<Invite>() {
    override fun areItemsTheSame(oldItem: Invite, newItem: Invite): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: Invite, newItem: Invite): Boolean {
        return oldItem.guestId == newItem.guestId
    }
}