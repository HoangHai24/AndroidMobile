package com.example.demoottmobile.domain.usecase

import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.repository.MediaRepository
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// Use Case = class chứa DUY NHẤT một logic nghiệp vụ.
//
// Kotlin: class GetCategoriesUseCase @Inject constructor(...)
//   - "@Inject constructor" → Hilt tự động tạo và truyền dependency vào constructor.
//   - Java tương đương: cần @Inject trên constructor riêng.
//
// Kotlin: class X @Inject constructor(private val repo: MediaRepository)
// ─────────────────────────────────────
// Java tương đương:
//
//   public class GetCategoriesUseCase {
//       private final MediaRepository repository;
//
//       @Inject  // Hilt biết cần gọi constructor này
//       public GetCategoriesUseCase(MediaRepository repository) {
//           this.repository = repository;
//       }
//
//       // Java không có "operator invoke" nên phải đặt tên hàm rõ ràng
//       public List<MediaCategory> execute() throws Exception {
//           return repository.getCategories();
//       }
//   }
//
// "suspend operator fun invoke()"
//   - "operator fun invoke" → Cho phép gọi: getCategoriesUseCase() thay vì
//     getCategoriesUseCase.invoke(). Trong Java không có khái niệm này.
//   - "suspend" → Hàm bất đồng bộ, có thể chạy trên background thread.
// ═══════════════════════════════════════════════════════

class GetCategoriesUseCase @Inject constructor(
    // "private val" trong constructor = field private final trong Java
    // Hilt tự động "tiêm" (inject) MediaRepository vào đây
    private val repository: MediaRepository
) {
    // "operator fun invoke()" cho phép gọi: val result = getCategoriesUseCase()
    // Java không có - phải gọi: val result = getCategoriesUseCase.execute()
    // "= repository.getCategories()" → single-expression function (viết ngắn, không cần {})
    // Java: return repository.getCategories();
    suspend operator fun invoke(): List<MediaCategory> = repository.getCategories()
}
