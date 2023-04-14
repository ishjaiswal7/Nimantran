package com.example.nimantran.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemMyNotificationBinding
import com.example.nimantran.databinding.ItemNotificationListBinding
import com.example.nimantran.models.admin.Notification

class MyNotificationAdapter(
    private val context: Context,
    private val cardListener: (Notification) -> Unit
) : ListAdapter<Notification, MyNotificationAdapter.ViewHolder>(MyNotificationDiffUtil()) {
    class ViewHolder(
        private val binding: ItemMyNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            notification: Notification,
            cardListener: (Notification) -> Unit
        ) {
            binding.notification = notification
            binding.cardViewNotification.setOnClickListener {
                cardListener(notification)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyNotificationBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), cardListener)
}
class MyNotificationDiffUtil : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.date == newItem.date
    }
}