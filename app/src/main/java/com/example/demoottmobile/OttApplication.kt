package com.example.demoottmobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// Đây là entry point (cổng vào) của toàn bộ ứng dụng Android.
// Được tạo ra TRƯỚC bất kỳ Activity hay Fragment nào.
//
// "@HiltAndroidApp"
//   → Bắt buộc khi dùng Hilt. Annotation này ra lệnh cho Hilt tự sinh
//     code khởi tạo hệ thống Dependency Injection cho toàn app.
//   → Java cũng cần annotation này khi dùng Hilt.
//
// "class OttApplication : Application()"
//   - ":" → "extends" trong Java
//   - "Application()" → gọi constructor của lớp cha (super())
//
// Java tương đương:
//
//   @HiltAndroidApp
//   public class OttApplication extends Application {
//       // Không cần viết thêm gì - Hilt tự sinh code
//   }
// ═══════════════════════════════════════════════════════

@HiltAndroidApp
class OttApplication : Application()
