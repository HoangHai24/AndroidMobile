package com.example.demoottmobile.di

import com.example.demoottmobile.data.repository.MediaRepositoryImpl
import com.example.demoottmobile.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// DI Module = File cấu hình dạy Hilt cách tạo và cấp phát các dependency.
//
// "@Module" → Đánh dấu class này là DI Module (cấu hình Hilt)
// "@InstallIn(SingletonComponent::class)" → Các binding trong module này sống
//   cùng với Application (suốt quá trình app chạy).
//
// "abstract class RepositoryModule"
//   - Phải là abstract class vì chứa hàm @Binds.
//   Java: public abstract class RepositoryModule { ... }
//
// "@Binds" → Dạy Hilt: "Khi ai yêu cầu MediaRepository, hãy cấp MediaRepositoryImpl".
//   Đây là cách map interface → implementation.
//
// "@Singleton" → Chỉ tạo 1 instance duy nhất cho toàn app.
//
// "abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaRepository"
//   - Khai báo: khi cần MediaRepository, loại trả về là MediaRepository
//     nhưng thực tế là MediaRepositoryImpl.
//   - Hàm abstract → Hilt tự sinh code, không cần viết thân hàm.
//
// Java tương đương:
//
//   @Module
//   @InstallIn(SingletonComponent.class)
//   public abstract class RepositoryModule {
//       @Binds
//       @Singleton
//       public abstract MediaRepository bindMediaRepository(MediaRepositoryImpl impl);
//   }
// ═══════════════════════════════════════════════════════

// "@Module" = cấu hình DI
// "@InstallIn(SingletonComponent::class)" = sống suốt vòng đời của Application
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // "@Binds" = map interface → implementation
    // "@Singleton" = chỉ tạo 1 instance
    // Hàm này là abstract → Hilt tự sinh code, lập trình viên không viết thân hàm
    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        // Hilt sẽ tạo MediaRepositoryImpl và truyền vào đây
        impl: MediaRepositoryImpl
    // Return type "MediaRepository" (interface) → gọi code sẽ nhận interface
    ): MediaRepository
}
