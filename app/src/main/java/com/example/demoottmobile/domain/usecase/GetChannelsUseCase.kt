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
//   public class GetChannelsUseCase {
//       private final MediaRepository repository;
//
//       @Inject
//       public GetChannelsUseCase(MediaRepository repository) {
//           this.repository = repository;
//       }
//
//       public List<MediaItem> execute() throws Exception {
//           return repository.getChannels();
//       }
//   }
// ═══════════════════════════════════════════════════════

class GetChannelsUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    // Gọi repository.getChannels() và trả về kết quả → danh sách kênh TV
    suspend operator fun invoke(): List<MediaItem> = repository.getChannels()
}
