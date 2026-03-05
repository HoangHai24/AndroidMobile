package com.example.demoottmobile.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.demoottmobile.databinding.ItemCategoryRowBinding
import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem

class CategoryAdapter(
    private val onTitleClick: (MediaCategory) -> Unit,
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaCategory, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val horizontalAdapter = HorizontalItemAdapter { item -> onItemClick(item) }

        init {
            binding.rvItems.apply {
                adapter = horizontalAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }
        }

        fun bind(category: MediaCategory) {
            binding.tvCategoryTitle.text = category.title
            binding.tvSeeAll.setOnClickListener { onTitleClick(category) }
            binding.tvCategoryTitle.setOnClickListener { onTitleClick(category) }
            // Limit to max 16 items
            horizontalAdapter.submitList(category.items.take(16))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MediaCategory>() {
        override fun areItemsTheSame(old: MediaCategory, new: MediaCategory) = old.id == new.id
        override fun areContentsTheSame(old: MediaCategory, new: MediaCategory) = old == new
    }
}
