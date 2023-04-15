package com.example.nimantran.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nimantran.databinding.ItemTemplateDesignBinding
import com.example.nimantran.models.user.Template

class TemplateAdapter(
    private val listener: (Template) -> Unit
) : ListAdapter<Template, TemplateAdapter.ViewHolder>(TemplateDiffUtil()) {
    class ViewHolder(
        private val binding: ItemTemplateDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            Template: Template,
            cardListener: (Template) -> Unit
        ) {
            binding.template = Template
            binding.root.setOnClickListener {
                cardListener(Template)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateDesignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), listener)
}

class TemplateDiffUtil : DiffUtil.ItemCallback<Template>() {
    override fun areItemsTheSame(oldItem: Template, newItem: Template): Boolean {
        return oldItem == newItem
    }

    //
    //
    override fun areContentsTheSame(oldItem: Template, newItem: Template): Boolean {
        return oldItem.name == newItem.name
    }
}