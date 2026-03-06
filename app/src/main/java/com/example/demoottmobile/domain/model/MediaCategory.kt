package com.example.demoottmobile.domain.model

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "data class" → POJO trong Java (xem giải thích đầy đủ ở MediaItem.kt)
//
// Kotlin: data class MediaCategory(...)
// ─────────────────────────────────────
// Java tương đương:
//
//   public class MediaCategory {
//       private final String id;
//       private final String title;
//       private final List<MediaItem> items;
//
//       public MediaCategory(String id, String title, List<MediaItem> items) {
//           this.id = id;
//           this.title = title;
//           this.items = items;
//       }
//       public String getId() { return id; }
//       public String getTitle() { return title; }
//       public List<MediaItem> getItems() { return items; }
//       // ... equals(), hashCode(), toString() tự động ...
//   }
//
// Kotlin: List<MediaItem>
//   → Là List bất biến (read-only). Không thể add/remove sau khi tạo.
//   → Java tương đương: List<MediaItem> nhưng phải tự đảm bảo không sửa.
// ═══════════════════════════════════════════════════════

data class MediaCategory(
    val id: String,           // Mã định danh duy nhất, vd: "cat_trending"
    val title: String,        // Tên hiển thị, vd: "Trending Now"
    val items: List<MediaItem> // Danh sách nội dung trong category này
)
