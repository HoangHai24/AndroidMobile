package com.example.demoottmobile.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoottmobile.databinding.ItemRectangleBinding
import com.example.demoottmobile.domain.model.MediaItem

/**
 * Horizontal list adapter — displays rectangle items (162x70 dp) in a single horizontal row.
 */
class HorizontalItemAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, HorizontalItemAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRectangleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val binding: ItemRectangleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onItemClick(item)
            }
        }

        fun bind(item: MediaItem) {
            binding.tvName.text = item.title
            Glide.with(binding.ivThumbnail)
                .load(item.thumbnailUrl)
                .centerCrop()
                .into(binding.ivThumbnail)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(old: MediaItem, new: MediaItem) = old.id == new.id
        override fun areContentsTheSame(old: MediaItem, new: MediaItem) = old == new
    }
}
