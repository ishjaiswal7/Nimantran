package com.example.nimantran.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemMyOrdersBinding
import com.example.nimantran.models.user.MyOrder

class MyOrdersAdapter (
    private val context: Context,
    private val listener: (MyOrder) -> Unit
        ) : ListAdapter<MyOrder, MyOrdersAdapter.ViewHolder>(MyOrdersDiffUtil()) {
            class ViewHolder(
                private val binding: ItemMyOrdersBinding
            ) : RecyclerView.ViewHolder(binding.root) {
                fun bind(myorder: MyOrder) {
                    binding.myOrder = myorder
                    binding.executePendingBindings()
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val binding = ItemMyOrdersBinding.inflate(
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
        class MyOrdersDiffUtil : DiffUtil.ItemCallback<MyOrder>() {
            override fun areItemsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
                return oldItem.id == newItem.id
            }
}