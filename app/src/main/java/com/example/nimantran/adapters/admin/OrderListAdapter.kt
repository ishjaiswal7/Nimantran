package com.example.nimantran.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemOrderListBinding

import com.example.nimantran.models.user.MyOrder

class OrderListAdapter(
    private val context: Context,
    private val listener: (MyOrder) -> Unit
) : ListAdapter<MyOrder, OrderListAdapter.ViewHolder>(OrderListDiffUtil()) {
    class ViewHolder(
        private val binding: ItemOrderListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: MyOrder) {
            binding.order = order
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderListBinding.inflate(
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
class OrderListDiffUtil : DiffUtil.ItemCallback<MyOrder>() {
    override fun areItemsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
        return oldItem.id == newItem.id
    }
}