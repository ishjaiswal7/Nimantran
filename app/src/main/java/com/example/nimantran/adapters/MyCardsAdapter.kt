package com.example.nimantran.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.nimantran.databinding.ItemMyCardsBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.models.user.MyCards

class MyCardsAdapter(
    private val context: Context,
    private val listener: (MyCards) -> Unit
) : ListAdapter<MyCards, MyCardsAdapter.ViewHolder>(MyCardsDiffUtil()) {
    class ViewHolder(
        private val binding: ItemMyCardsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mycards: MyCards) {
            binding.mycards = mycards
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyCardsBinding.inflate(
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
class MyCardsDiffUtil : DiffUtil.ItemCallback<MyCards>() {
    override fun areItemsTheSame(oldItem: MyCards, newItem: MyCards): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MyCards, newItem: MyCards): Boolean {
        return oldItem.cardImage == newItem.cardImage
    }
}