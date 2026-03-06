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
 *
 * [Java tương đương]
 * public class ChannelGridAdapter extends ListAdapter<MediaItem, ChannelGridAdapter.ChannelViewHolder> {
 *     interface OnClickListener { void onClick(MediaItem item); }
 *     public ChannelGridAdapter(OnClickListener onItemClick) { super(new DiffCallback()); }
 * }
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
            // "binding.root.post { ... }" → chạy sau khi View được vẽ xong và có kích thước.
            // Java: binding.getRoot().post(() -> { ... });
            // Mục đích: lấy width và gán height = width → item hình vuông
            binding.root.post {
                val width = binding.root.width
                val params = binding.root.layoutParams
                params.height = width // chiều cao = chiều rộng → hình vuông
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
