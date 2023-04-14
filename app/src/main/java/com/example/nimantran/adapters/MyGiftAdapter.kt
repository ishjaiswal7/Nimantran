package com.example.nimantran.adapters
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemMyGiftsBinding
import com.example.nimantran.models.admin.Gift

class MyGiftAdapter(
    private val context: Context,
    private val listener: (Gift) -> Unit,
    ): ListAdapter<Gift, MyGiftAdapter.ViewHolder>(MyGiftDiffUtil()) {
    class ViewHolder(
        private val binding: ItemMyGiftsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(
                gift: Gift,
                Listener: (Gift) -> Unit,) {
                binding.gift = gift
                Log.d("MyGiftAdapter", "Binding gift ${gift.item}")
                binding.cardViewMyGiftItem.setOnClickListener {
                    Listener(gift)
                }
                binding.executePendingBindings()
            }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyGiftsBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
    class MyGiftDiffUtil : DiffUtil.ItemCallback<Gift>() {
        override fun areItemsTheSame(oldItem: Gift, newItem: Gift): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Gift, newItem: Gift): Boolean {
            return oldItem.item == newItem.item
        }
    }
}