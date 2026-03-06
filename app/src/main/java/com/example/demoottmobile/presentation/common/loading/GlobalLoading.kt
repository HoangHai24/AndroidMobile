package com.example.demoottmobile.presentation.common.loading

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.demoottmobile.R

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "object GlobalLoading" → Singleton điều khiển hiện/ẩn loading overlay
//
// Java tương đương:
//   public class GlobalLoading {
//       private GlobalLoading() {}
//       public static void show(FragmentActivity activity) {
//           activity.runOnUiThread(() -> {
//               View overlay = activity.findViewById(R.id.loading_overlay);
//               if (overlay != null) overlay.setVisibility(View.VISIBLE);
//           });
//       }
//       public static void hide(FragmentActivity activity) {
//           activity.runOnUiThread(() -> {
//               View overlay = activity.findViewById(R.id.loading_overlay);
//               if (overlay != null) overlay.setVisibility(View.GONE);
//           });
//       }
//   }
//
// "activity.runOnUiThread { ... }" → chạy code trên UI thread (tương tự Java)
// "activity.findViewById<View>(...)?.visibility" →
//   • "<View>" = kiểu generic, Java không cần (tự cast)
//   • "?." = safe call: chỉ thực thi nếu không null
//   Java: View v = activity.findViewById(R.id.loading_overlay);
//         if (v != null) v.setVisibility(View.VISIBLE);
// ═══════════════════════════════════════════════════════

// "object" → singleton, không thể khởi tạo bằng "new"
object GlobalLoading {

    fun show(activity: FragmentActivity) {
        activity.runOnUiThread {
            activity.findViewById<View>(R.id.loading_overlay)?.visibility = View.VISIBLE
        }
    }

    fun hide(activity: FragmentActivity) {
        activity.runOnUiThread {
            activity.findViewById<View>(R.id.loading_overlay)?.visibility = View.GONE
        }
    }
}
