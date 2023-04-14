package com.example.nimantran.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.R
import com.example.nimantran.databinding.ItemNotificationListBinding
import com.example.nimantran.models.admin.Notification

class NotificationAdapter(
    private val context: Context,
    private val cardListener: (Notification) -> Unit,
    private val deleteListener: (Notification) -> Unit,

    ) : ListAdapter<Notification, NotificationAdapter.ViewHolder>(NotificationDiffUtil()) {
    class ViewHolder(
        private val binding: ItemNotificationListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            notification: Notification,
            cardListener: (Notification) -> Unit,
            deleteListener: (Notification) -> Unit
        ) {
            binding.notification = notification
            Log.d("NotificationAdapter", "Binding notification ${notification.id}")
            binding.cardViewNotification.setOnClickListener {
                cardListener(notification)
            }
            binding.imageViewDeleteNotification.setOnClickListener {
                deleteListener(notification)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationListBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), cardListener, deleteListener)
}


class NotificationDiffUtil : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }

    //
    //
    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.date == newItem.date
    }
}
