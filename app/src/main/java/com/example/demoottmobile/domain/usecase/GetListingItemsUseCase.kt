package com.example.demoottmobile.domain.usecase

import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.repository.MediaRepository
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG (xem giải thích đầy đủ ở GetCategoriesUseCase.kt)
// ═══════════════════════════════════════════════════════
//
// Java tương đương:
//
//   public class GetListingItemsUseCase {
//       private final MediaRepository repository;
//
//       @Inject
//       public GetListingItemsUseCase(MediaRepository repository) {
//           this.repository = repository;
//       }
//
//       // Nhận tham số categoryId để lọc items
//       public List<MediaItem> execute(String categoryId) throws Exception {
//           return repository.getListingItems(categoryId);
//       }
//   }
//
// "operator fun invoke(categoryId: String)" → Gọi: getListingItemsUseCase("cat_trending")
//   Java không có tính năng này, phải gọi: getListingItemsUseCase.execute("cat_trending")
// ═══════════════════════════════════════════════════════

class GetListingItemsUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    // Nhận tham số có tên rõ ràng: getListingItemsUseCase(categoryId = "cat_trending")
    // hoặc gọi tắt: getListingItemsUseCase("cat_trending")
    suspend operator fun invoke(categoryId: String): List<MediaItem> =
        repository.getListingItems(categoryId)
}
