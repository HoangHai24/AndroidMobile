package com.example.demoottmobile.data.repository

import com.example.demoottmobile.data.source.MockDataSource
import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "class MediaRepositoryImpl ... : MediaRepository"
//   - ": MediaRepository" = "implements MediaRepository" trong Java
//   - Kotlin dùng dấu ":" cho cả "extends" và "implements"
//
// "override suspend fun getCategories()"
//   - "override" = @Override trong Java
//   - "suspend" = hàm bất đồng bộ (coroutine)
//
// "withContext(Dispatchers.IO) { ... }"
//   - Chuyển sang IO thread để xử lý tác vụ nặng (network, database).
//   - Không block UI thread → app không bị giật.
//   Java tương đương:
//     ExecutorService executor = Executors.newCachedThreadPool();
//     executor.submit(() -> { /* lấy data từ background */ });
//
// "delay(500)" → giả lập network delay 500ms mà không block thread.
//   Java tương đương: Thread.sleep(500) nhưng sẽ BLOCK thread!
//
// Java tương đương tổng thể:
//
//   @Singleton
//   public class MediaRepositoryImpl implements MediaRepository {
//       private final MockDataSource mockDataSource;
//
//       @Inject
//       public MediaRepositoryImpl(MockDataSource mockDataSource) {
//           this.mockDataSource = mockDataSource;
//       }
//
//       @Override
//       public List<MediaCategory> getCategories() {
//           // Java: phải tự quản lý thread thủ công
//           Thread.sleep(500); // block thread (tệ!)
//           return mockDataSource.getCategories();
//       }
//   }
// ═══════════════════════════════════════════════════════

// "@Singleton" = chỉ tạo 1 instance duy nhất trong toàn bộ app
@Singleton
// "@Inject constructor(...)" = Hilt tự truyền MockDataSource vào constructor
// ": MediaRepository" = implements MediaRepository (Java: ... implements MediaRepository)
class MediaRepositoryImpl @Inject constructor(
    private val mockDataSource: MockDataSource
) : MediaRepository {

    // "override" = @Override trong Java
    // "= withContext(...)" → single-expression (không cần {})
    override suspend fun getCategories(): List<MediaCategory> = withContext(Dispatchers.IO) {
        delay(500) // Giả lập network delay 500ms (không block UI thread)
        // Khối code cuối cùng trong lambda là giá trị trả về - không cần "return"
        mockDataSource.getCategories()
    }

    override suspend fun getChannels(): List<MediaItem> = withContext(Dispatchers.IO) {
        delay(400)
        mockDataSource.getChannels()
    }

    override suspend fun getListingItems(categoryId: String): List<MediaItem> = withContext(Dispatchers.IO) {
        delay(400)
        mockDataSource.getListingItems(categoryId)
    }
}
