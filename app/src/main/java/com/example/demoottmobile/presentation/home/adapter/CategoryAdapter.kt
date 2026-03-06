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

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "class CategoryAdapter(...) : ListAdapter<MediaCategory, ...>(DiffCallback())"
//   - ": ListAdapter<>" = extends ListAdapter trong Java
//   - "ListAdapter" tự quản lý list và animation khi data thay đổi.
//   - Kotlin constructor: truyền 2 lambda (onTitleClick, onItemClick).
//   Java tương đương:
//     interface OnTitleClickListener { void onTitleClick(MediaCategory cat); }
//     public CategoryAdapter(OnTitleClickListener onTitle, OnItemClickListener onItem) { ... }
//
// "(MediaCategory) -> Unit" = functional interface, nhận MediaCategory, không trả về gì.
//   Java tương đương: Consumer<MediaCategory> hoặc interface tự định nghĩa.
//
// "inner class CategoryViewHolder" → Inner class trong Java
//   - "inner class" có thể truy cập outer class (onTitleClick, onItemClick)
//   Java: public class CategoryViewHolder extends RecyclerView.ViewHolder { ... }
//
// "DiffCallback" → Giúp RecyclerView so sánh old/new list để animation đồng đều.
//   Java: giống hệt, cũng phải viết DiffUtil.ItemCallback
// ═══════════════════════════════════════════════════════

// Tham số là function type: Java tương đương Consumer<MediaCategory>
// "(MediaCategory) -> Unit" = nhận MediaCategory, không trả về giá trị
class CategoryAdapter(
    private val onTitleClick: (MediaCategory) -> Unit,
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaCategory, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    // "override fun onCreateViewHolder": @Override public CategoryViewHolder onCreateViewHolder(...)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // "getItem(position)" → lấy item từ ListAdapter (tương tự Java)
        holder.bind(getItem(position))
    }

    // "inner class" → có thể truy cập thành viên của CategoryAdapter (outer class)
    // Java: public class CategoryViewHolder extends RecyclerView.ViewHolder { ... }
    inner class CategoryViewHolder(
        private val binding: ItemCategoryRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Adapter nằm ở trong ViewHolder, mỗi row có 1 horizontal RecyclerView
        private val horizontalAdapter = HorizontalItemAdapter { item -> onItemClick(item) }

        // "init" = khối code chạy khi ViewHolder được khởi tạo (trong constructor)
        // Java: public CategoryViewHolder(ItemCategoryRowBinding binding) {
        //     super(binding.getRoot());
        //     binding.rvItems.setAdapter(horizontalAdapter);
        //     ...
        // }
        init {
            binding.rvItems.apply {
                adapter = horizontalAdapter
                // HORIZONTAL → scroll theo chiều ngang
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }
        }

        fun bind(category: MediaCategory) {
            binding.tvCategoryTitle.text = category.title
            // Lambda ngắn gọn: { onTitleClick(category) }
            // Java: tvSeeAll.setOnClickListener(v -> onTitleClick.invoke(category));
            binding.tvSeeAll.setOnClickListener { onTitleClick(category) }
            binding.tvCategoryTitle.setOnClickListener { onTitleClick(category) }
            // ".take(16)" → lấy tối đa 16 phần tỮd đầu
            // Java: category.getItems().subList(0, Math.min(16, category.getItems().size()))
            horizontalAdapter.submitList(category.items.take(16))
        }
    }

    // DiffCallback giúp RecyclerView biết item nào thay đổi để animation mượt
    class DiffCallback : DiffUtil.ItemCallback<MediaCategory>() {
        // So sánh ID: Nếu ID giống → cùng item
        override fun areItemsTheSame(old: MediaCategory, new: MediaCategory) = old.id == new.id
        // So sánh nội dung: Nếu nội dung giống → không cần vẽ lại
        // "data class" tự động tạo equals() → "old == new" so sánh tất cả field
        override fun areContentsTheSame(old: MediaCategory, new: MediaCategory) = old == new
    }
}
