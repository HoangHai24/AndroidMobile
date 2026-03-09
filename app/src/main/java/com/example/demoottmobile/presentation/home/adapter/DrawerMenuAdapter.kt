package com.example.demoottmobile.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.demoottmobile.databinding.ItemDrawerMenuBinding
import com.example.demoottmobile.domain.model.MediaCategory

class DrawerMenuAdapter(
    private val onItemClick: (MediaCategory) -> Unit
) : ListAdapter<MediaCategory, DrawerMenuAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemDrawerMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: MediaCategory) {
            binding.tvLabel.text = category.title
            binding.root.setOnClickListener { onItemClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDrawerMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<MediaCategory>() {
        override fun areItemsTheSame(oldItem: MediaCategory, newItem: MediaCategory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MediaCategory, newItem: MediaCategory) =
            oldItem == newItem
    }
}
