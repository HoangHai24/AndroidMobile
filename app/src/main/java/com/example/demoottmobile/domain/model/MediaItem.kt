package com.example.demoottmobile.domain.model

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "data class" trong Kotlin tương đương với một POJO (Plain Old Java Object)
// trong Java nhưng Kotlin tự động tạo ra:
//   - equals() và hashCode()  → so sánh 2 object theo giá trị các field
//   - toString()              → in ra dạng "MediaItem(id=..., title=..., ...)"
//   - copy()                  → tạo bản sao với một số field thay đổi
//
// Kotlin: data class MediaItem(...)
// ─────────────────────────────────────
// Java tương đương (viết tay phải 50+ dòng):
//
//   public class MediaItem {
//       private final String id;
//       private final String title;
//       private final String thumbnailUrl;
//       private final String streamUrl;
//       private final MediaType type;
//
//       public MediaItem(String id, String title, String thumbnailUrl,
//                        String streamUrl, MediaType type) {
//           this.id = id; this.title = title;
//           this.thumbnailUrl = thumbnailUrl;
//           this.streamUrl = streamUrl;
//           this.type = type;
//       }
//       public String getId() { return id; }
//       // ... getter cho từng field ...
//       // ... equals(), hashCode(), toString() ...
//   }
//
// Kotlin: val id: String      → field final (chỉ đọc, không thể thay đổi)
// Kotlin: val type: MediaType = MediaType.VOD
//   → Giá trị mặc định: nếu không truyền type thì mặc định là VOD.
//   → Java không có mặc định trong constructor, phải dùng builder hoặc overload.
// ═══════════════════════════════════════════════════════

data class MediaItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val streamUrl: String,
    // "= MediaType.VOD" là giá trị mặc định - Java không có cú pháp này
    val type: MediaType = MediaType.VOD
)

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG cho "enum class"
// ═══════════════════════════════════════════════════════
//
// Kotlin: enum class MediaType { VOD, LIVE, CHANNEL }
// ─────────────────────────────────────
// Java tương đương:
//
//   public enum MediaType {
//       VOD,    // Video On Demand - phim, series xem theo yêu cầu
//       LIVE,   // Livestream trực tiếp
//       CHANNEL // Kênh TV trực tiếp
//   }
// ═══════════════════════════════════════════════════════

enum class MediaType {
    VOD,     // Video On Demand - phim xem theo yêu cầu
    LIVE,    // Livestream trực tiếp
    CHANNEL  // Kênh TV
}
