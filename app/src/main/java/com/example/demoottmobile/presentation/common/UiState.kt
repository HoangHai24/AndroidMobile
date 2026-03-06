package com.example.demoottmobile.presentation.common

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "sealed class UiState<out T>" → Lớp trạng thái UI có giới hạn sub-class.
//
// Khác nhau với Java:
//   - Java không có "sealed class". Tương đương dùng abstract class + private constructor.
//   - "out T" = covariance: chỉ "xuất" T, không nhận T vào.
//     Java tương đương: "? extends T" (upper-bounded wildcard)
//
// Kotlin:
//   sealed class UiState<out T> { ... }
//
// Java tương đương (mỏ phỏng):
//   public abstract class UiState<T> {                    // abstract class gốc
//       private UiState() {}                              // constructor private: ngăn sub-class ngoài
//
//       public static final class Loading extends UiState<Object> {}  // singleton object
//
//       public static final class Success<T> extends UiState<T> {     // data class với field
//           public final T data;
//           public Success(T data) { this.data = data; }
//       }
//
//       public static final class Error extends UiState<Object> {     // data class với message
//           public final String message;
//           public Error(String message) { this.message = message; }
//       }
//   }
//
// Cách dùng trong khi check trạng thái:
//   Kotlin: when (state) {
//               is UiState.Loading  -> ...
//               is UiState.Success  -> println(state.data)
//               is UiState.Error    -> ...
//           }
//   Java:   if (state instanceof UiState.Loading) { ... }
//           else if (state instanceof UiState.Success) { T data = ((UiState.Success<T>) state).data; }
// ═══════════════════════════════════════════════════════

sealed class UiState<out T> {
    // "object" → Kotlin singleton (Java: static final instance duy nhất)
    // ': UiState<Nothing>()' → kế thừa UiState, Nothing = không có giá trị
    object Loading : UiState<Nothing>()

    // "data class" → tự động sinh equals/hashCode/toString (Java: viết tay)
    // "val data: T" → field chứa kết quả trả về khi thành công
    data class Success<T>(val data: T) : UiState<T>()

    // "val message: String" → thông báo lỗi dạng String
    data class Error(val message: String) : UiState<Nothing>()
}
