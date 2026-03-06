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
 *
 * [Java tương đương]
 * public class HorizontalItemAdapter extends ListAdapter<MediaItem, HorizontalItemAdapter.ItemViewHolder> {
 *     interface OnClickListener { void onClick(MediaItem item); }
 *     private final OnClickListener onItemClick;
 *     public HorizontalItemAdapter(OnClickListener onItemClick) {
 *         super(new DiffCallback());
 *         this.onItemClick = onItemClick;
 *     }
 * }
 */
class HorizontalItemAdapter(
    // "(MediaItem) -> Unit" = lambda nhận MediaItem, không trả về gì
    // Java: Consumer<MediaItem> hoặc interface OnItemClickListener
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
            // Click listener đặt trong init (constructor)
            binding.root.setOnClickListener {
                // "bindingAdapterPosition" → vị trí hiện tại của item trong adapter
                val item = getItem(bindingAdapterPosition)
                onItemClick(item) // gọi lambda được truyền vào
            }
        }

        fun bind(item: MediaItem) {
            binding.tvName.text = item.title
            // Glide: thư viện load ảnh bất đồng bộ từ URL
            // Java: Glide.with(binding.ivThumbnail).load(item.getThumbnailUrl()).centerCrop().into(binding.ivThumbnail);
            Glide.with(binding.ivThumbnail)
                .load(item.thumbnailUrl)    // URL ảnh
                .centerCrop()               // Cắt ảnh vừa khưng
                .into(binding.ivThumbnail)  // Đổ vào ImageView
        }
    }

    // So sánh item theo ID và nội dung (xem giải thích ở CategoryAdapter.kt)
    class DiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(old: MediaItem, new: MediaItem) = old.id == new.id
        override fun areContentsTheSame(old: MediaItem, new: MediaItem) = old == new
    }
}
