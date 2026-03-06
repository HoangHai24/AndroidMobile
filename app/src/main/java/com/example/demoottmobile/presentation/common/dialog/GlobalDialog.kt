package com.example.demoottmobile.presentation.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.demoottmobile.R
import com.example.demoottmobile.databinding.DialogGlobalBinding

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "object GlobalDialog" → Singleton trong Kotlin.
//   Java tương đương:
//     public class GlobalDialog {
//         private static GlobalDialog instance;
//         private GlobalDialog() {}
//         public static GlobalDialog getInstance() {
//             if (instance == null) instance = new GlobalDialog();
//             return instance;
//         }
//     }
//
// Các tham số có giá trị mặc định (default parameters):
//   Kotlin: positiveText: String = context.getString(R.string.ok)
//   Java không có khái niệm này → phải tạo nhiều hàm overload:
//     public static Dialog show(Context ctx, String title, String msg) {
//         return show(ctx, title, msg, ctx.getString(R.string.ok), null, null, null, true);
//     }
//     public static Dialog show(Context ctx, String title, String msg, String positive, ...) { ... }
//
// Kiểu "(() -> Unit)?":
//   - "() -> Unit" = lambda/Runnable không tham số, không trả về gì
//   - "?" = có thể null
//   Java tương đương: @Nullable Runnable onPositive
//
// "onPositive?.invoke()":
//   - Gọi lambda nếu không null
//   Java tương đương: if (onPositive != null) { onPositive.run(); }
// ═══════════════════════════════════════════════════════

// "object" → singleton, chỉ có 1 instance duy nhất trong vòng đời ứng dụng
object GlobalDialog {

    // Hàm có nhiều tham số mặc định → Java phải viết nhiều overload; Kotlin chỉ 1 hàm
    fun show(
        context: Context,
        title: String,
        message: String,
        positiveText: String = context.getString(R.string.ok), // mặc định = "OK"
        negativeText: String? = null,   // null = không hiện nút Cancel
        onPositive: (() -> Unit)? = null,  // lambda callback, có thể null
        onNegative: (() -> Unit)? = null,  // Java: @Nullable Runnable onNegative
        cancelable: Boolean = true         // mặc định có thể bấm ngoài để đóng
    ): Dialog {
        val dialog = Dialog(context, R.style.OttDialog)
        val binding = DialogGlobalBinding.inflate(LayoutInflater.from(context))

        binding.tvDialogTitle.text = title
        binding.tvDialogMessage.text = message
        binding.btnPositive.text = positiveText

        binding.btnPositive.setOnClickListener {
            // "onPositive?.invoke()" → gọi lambda nếu không null
            // Java: if (onPositive != null) { onPositive.run(); }
            onPositive?.invoke()
            dialog.dismiss()
        }

        if (negativeText != null) {
            binding.btnNegative.visibility = View.VISIBLE
            binding.btnNegative.text = negativeText
            binding.btnNegative.setOnClickListener {
                onNegative?.invoke()
                dialog.dismiss()
            }
        }

        dialog.setContentView(binding.root)
        dialog.setCancelable(cancelable)
        dialog.show()
        return dialog
    }
}
