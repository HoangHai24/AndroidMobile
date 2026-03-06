package com.example.demoottmobile.presentation.common.toast

import android.content.Context
import android.widget.Toast

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "object GlobalToast" → Singleton hiện Toast đơn giản
//
// Java tương đương:
//   public class GlobalToast {
//       private GlobalToast() {}
//
//       // Java không có default param → phải overload:
//       public static void show(Context context, String message) {
//           show(context, message, Toast.LENGTH_SHORT);
//       }
//       public static void show(Context context, String message, int duration) {
//           Toast.makeText(context, message, duration).show();
//       }
//       public static void showLong(Context context, String message) {
//           show(context, message, Toast.LENGTH_LONG);
//       }
//       public static void showError(Context context, String message) {
//           show(context, "⚠️ " + message, Toast.LENGTH_LONG);
//       }
//   }
//
// "duration: Int = Toast.LENGTH_SHORT" → tham số mặc định;
//   nếu gọi show(ctx, "msg") thì duration tự động = Toast.LENGTH_SHORT
// Java không có khái niệm này, phải viết thêm hàm overload.
//
// "\u26a0\ufe0f $message" → String template: chèn biến message vào chuỗi
//   Java: "\u26a0\ufe0f " + message
// ═══════════════════════════════════════════════════════

// "object" → singleton, mọi lời gọi từ bất kỳ nơi nào đều dùng chung 1 instance
object GlobalToast {

    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun showLong(context: Context, message: String) {
        show(context, message, Toast.LENGTH_LONG)
    }

    fun showError(context: Context, message: String) {
        show(context, "⚠️ $message", Toast.LENGTH_LONG)
    }
}
