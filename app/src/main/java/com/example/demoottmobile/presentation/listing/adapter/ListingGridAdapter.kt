package com.example.demoottmobile.presentation.listing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoottmobile.databinding.ItemRectangleGridBinding
import com.example.demoottmobile.domain.model.MediaItem

/**
 * 2-column grid adapter for ListingGrid screen.
 * Item uses rectangle proportions. Width is determined by GridLayoutManager.
 */
class ListingGridAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, ListingGridAdapter.ListingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemRectangleGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ListingViewHolder(
        private val binding: ItemRectangleGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onItemClick(item)
            }
            // Set height to 16:9-ish aspect ratio for rectangle: width*(70/162)
            binding.root.post {
                val width = binding.root.width
                val params = binding.root.layoutParams
                // 162:70 ≈ 2.31:1 ratio → height = width * 70/162
                params.height = (width * 70 / 162) + 40 // +40dp for label area
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
