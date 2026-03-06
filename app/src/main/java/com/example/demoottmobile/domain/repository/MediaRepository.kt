package com.example.demoottmobile.domain.repository

import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "interface" trong Kotlin hoạt động gần giống Java.
// Đây là "hợp đồng" (contract): ai muốn cung cấp data phải implement interface này.
//
// Kotlin: interface MediaRepository { ... }
// ─────────────────────────────────────
// Java tương đương:
//
//   public interface MediaRepository {
//       List<MediaCategory> getCategories() throws Exception;
//       List<MediaItem> getChannels() throws Exception;
//       List<MediaItem> getListingItems(String categoryId) throws Exception;
//   }
//
// "suspend fun" trong Kotlin:
//   → Là hàm có thể tạm dừng (coroutine) mà không block thread.
//   → Giống như hàm bất đồng bộ (async) trong Java nhưng cú pháp đơn giản hơn.
//   → Trong Java, bạn dùng: Future<T>, Callable<T>, hoặc RxJava Observable<T>.
//   → suspend fun phải được gọi bên trong coroutine scope (ví dụ: viewModelScope).
//
// "List<MediaCategory>" (return type) → ghi sau dấu ":" trong Kotlin
//   Kotlin: suspend fun getCategories(): List<MediaCategory>
//   Java:   List<MediaCategory> getCategories();
// ═══════════════════════════════════════════════════════

interface MediaRepository {
    // "suspend" = hàm bất đồng bộ, có thể tạm dừng (không block UI thread)
    // Java gần tương đương: CompletableFuture<List<MediaCategory>> getCategories();
    suspend fun getCategories(): List<MediaCategory>

    suspend fun getChannels(): List<MediaItem>

    // Nhận tham số categoryId để lọc items theo category
    suspend fun getListingItems(categoryId: String): List<MediaItem>
}
