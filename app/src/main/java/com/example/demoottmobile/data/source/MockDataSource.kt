package com.example.demoottmobile.data.source

import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.model.MediaType
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "private const val" → Hằng số cấp file (không thuộc class nào).
//   Kotlin:  private const val DEFAULT_STREAM_URL = "..."
//   Java:    private static final String DEFAULT_STREAM_URL = "...";
//
// "private val PLACEHOLDER_THUMBNAILS = listOf(...)"
//   → Biến cấp file, read-only, khởi tạo ngay lúc khai báo.
//   → "listOf(...)" tạo List bất biến (không thể add/remove).
//   Java tương đương:
//     private static final List<String> PLACEHOLDER_THUMBNAILS =
//         Collections.unmodifiableList(Arrays.asList("url1", "url2", ...));
//
// "@Singleton" → Hilt chỉ tạo DUY NHẤT 1 instance trong suốt quá trình app chạy.
//   Java: không có nào tương đương trực tiếp, phải viết Singleton pattern thủ công.
// ═══════════════════════════════════════════════════════

// Hằng số cấp file: nằm ngoài class, chỉ dùng trong file này
// Java: private static final String DEFAULT_STREAM_URL = "...";
private const val DEFAULT_STREAM_URL =
    "https://cdn-demo-sigma-livestreaming.sigma.video/data/vod/sigma-vod/" +
    "168b85fe-3184-41e6-a85b-f491c302a92e/hls-BM/master.m3u8"

// Danh sách URL ảnh giả lập (placeholder thumbnails)
// "private val" → private static final trong Java
// "listOf(...)" → Arrays.asList(...) trong Java (nhưng Kotlin List là immutable)
private val PLACEHOLDER_THUMBNAILS = listOf(
    "https://picsum.photos/seed/ott1/320/180",
    "https://picsum.photos/seed/ott2/320/180",
    "https://picsum.photos/seed/ott3/320/180",
    "https://picsum.photos/seed/ott4/320/180",
    "https://picsum.photos/seed/ott5/320/180",
    "https://picsum.photos/seed/ott6/320/180",
    "https://picsum.photos/seed/ott7/320/180",
    "https://picsum.photos/seed/ott8/320/180",
    "https://picsum.photos/seed/ott9/320/180",
    "https://picsum.photos/seed/ott10/320/180",
    "https://picsum.photos/seed/ott11/320/180",
    "https://picsum.photos/seed/ott12/320/180",
    "https://picsum.photos/seed/ott13/320/180",
    "https://picsum.photos/seed/ott14/320/180",
    "https://picsum.photos/seed/ott15/320/180",
    "https://picsum.photos/seed/ott16/320/180"
)

// "@Singleton" → Hilt chỉ tạo 1 instance duy nhất (Singleton pattern)
// "@Inject constructor()" → Constructor không nhận tham số nào, Hilt tự tạo
// Java tương đương: public class MockDataSource { @Inject public MockDataSource() {} }
@Singleton
class MockDataSource @Inject constructor() {

    // Hàm trả về danh sách category một cách ngắn gọn (single-expression)
    // "= listOf(...)" thay vì "{ return listOf(...) }"
    // Java: public List<MediaCategory> getCategories() { return Arrays.asList(...); }
    fun getCategories(): List<MediaCategory> = listOf(
        MediaCategory(
            // Named arguments: truyền theo tên tham số → code dễ đọc hơn
            // Java không có, phải nhớ thứ tự các tham số
            id = "cat_trending",
            title = "Trending Now",
            items = generateItems("trending", 16)
        ),
        MediaCategory(
            id = "cat_movies",
            title = "Movies",
            items = generateItems("movies", 14)
        ),
        MediaCategory(
            id = "cat_series",
            title = "TV Series",
            items = generateItems("series", 16)
        ),
        MediaCategory(
            id = "cat_sports",
            title = "Sports",
            items = generateItems("sports", 12)
        ),
        MediaCategory(
            id = "cat_kids",
            title = "Kids & Family",
            items = generateItems("kids", 10)
        ),
        MediaCategory(
            id = "cat_news",
            title = "News",
            items = generateItems("news", 8)
        )
    )

    // ═══════════════════════════════════════════════════════
    // Kotlin: (1..24).map { i -> ... }
    //   - "(1..24)" → tạo ra dãy số từ 1 đến 24 (IntRange)
    //   - ".map { i -> ... }" → biến mỗi số thành 1 MediaItem và trả về List
    //
    // Java tương đương:
    //   List<MediaItem> result = new ArrayList<>();
    //   for (int i = 1; i <= 24; i++) {
    //       result.add(new MediaItem("ch_" + i, "Channel " + i, ...));
    //   }
    //   return result;
    //
    // (i - 1) % PLACEHOLDER_THUMBNAILS.size()
    //   → Lấy ảnh xoay vòng trong list 16 ảnh (modulo)
    // ═══════════════════════════════════════════════════════
    fun getChannels(): List<MediaItem> = (1..24).map { i ->
        MediaItem(
            id = "ch_$i",           // String template: "ch_$i" = "ch_" + i trong Java
            title = "Channel $i",   // "$i" chèn giá trị biến i vào string
            thumbnailUrl = PLACEHOLDER_THUMBNAILS[(i - 1) % PLACEHOLDER_THUMBNAILS.size],
            streamUrl = DEFAULT_STREAM_URL,
            type = MediaType.CHANNEL
        )
    }

    // Tạo danh sách items giả lập cho một category cụ thể
    // Java: public List<MediaItem> getListingItems(String categoryId) { ... }
    fun getListingItems(categoryId: String): List<MediaItem> =
        generateItems(categoryId, 20)

    // ═══════════════════════════════════════════════════════
    // Hàm hỗ trợ (private) - chỉ dùng trong class này
    // Kotlin: private fun generateItems(prefix: String, count: Int): List<MediaItem>
    // Java:   private List<MediaItem> generateItems(String prefix, int count) { ... }
    //
    // "(1..count).map { i -> ... }" → tương đương for-loop trong Java
    //
    // "prefix.replaceFirstChar { it.uppercase() }"
    //   → Viết hoa chữ cái đầu tiên của chuỗi prefix.
    //   → "it" là tham số ẩn của lambda khi chỉ có 1 tham số.
    //   Java tương đương:
    //     Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1)
    // ═══════════════════════════════════════════════════════
    private fun generateItems(prefix: String, count: Int): List<MediaItem> =
        (1..count).map { i ->
            MediaItem(
                id = "${prefix}_$i",          // String template với biếu thức: "${prefix}_$i"
                // "${...}" bao biếu thức phức tạp, Java: prefix + "_" + i
                title = "${prefix.replaceFirstChar { it.uppercase() }} Item $i",
                thumbnailUrl = PLACEHOLDER_THUMBNAILS[(i - 1) % PLACEHOLDER_THUMBNAILS.size],
                streamUrl = DEFAULT_STREAM_URL,
                type = MediaType.VOD
            )
        }
}
