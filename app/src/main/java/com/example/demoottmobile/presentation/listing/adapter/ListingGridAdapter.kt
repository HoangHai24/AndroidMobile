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
 *
 * [Java tương đương]
 * public class ListingGridAdapter extends ListAdapter<MediaItem, ListingGridAdapter.ListingViewHolder> {
 *     ... (giống ChannelGridAdapter nhưng dùng tỉ lệ 16:9 cho item)
 * }
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
            // ".post { ... }" chạy sau khi layout xong, biết được width thực tế
            // Java: binding.getRoot().post(() -> { ... });
            binding.root.post {
                val width = binding.root.width
                val params = binding.root.layoutParams
                // Tốc độ hiển thị: tỉ lệ chiều cao = width * 70/162 + 40px cho nhãn
                // Java: params.height = (width * 70 / 162) + 40;
                params.height = (width * 70 / 162) + 40
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
