package com.example.demoottmobile.presentation.channel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoottmobile.databinding.ItemSquareGridBinding
import com.example.demoottmobile.domain.model.MediaItem

/**
 * 4-column grid adapter for Channel screen.
 * Item height = item width (square). Width is determined by GridLayoutManager.
 */
class ChannelGridAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, ChannelGridAdapter.ChannelViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemSquareGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChannelViewHolder(
        private val binding: ItemSquareGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onItemClick(item)
            }
            // Make height equal width (square)
            binding.root.post {
                val width = binding.root.width
                val params = binding.root.layoutParams
                params.height = width
                binding.root.layoutParams = params
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
