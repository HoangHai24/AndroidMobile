package com.example.demoottmobile.presentation.channel

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// ": RecyclerView.ItemDecoration()" = extends RecyclerView.ItemDecoration trong Java
//
// Class này tính toán khoảng cách (đệm) đều cho mỗi item trong lưới
// (grid). Giúp các item không chung ta nhau mà có khoảng cách đều đẹp.
//
// Thông số:
//   - spanCount   : số cột trong grid (vd: 4 cột cho kênh, 2 cột cho listing)
//   - spacing     : khoảng cách (pixel) giữa các item
//   - includeEdge : có thêm khoảng cách ở cạnh ngoài cùng không
//
// Java tương đương:
//
//   public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//       private final int spanCount, spacing;
//       private final boolean includeEdge;
//
//       public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//           this.spanCount = spanCount;
//           this.spacing = spacing;
//           this.includeEdge = includeEdge;
//       }
//
//       @Override
//       public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
//                                  RecyclerView.State state) {
//           int position = parent.getChildAdapterPosition(view);
//           int column = position % spanCount;
//           // ... tính left, right, top, bottom ...
//       }
//   }
// ═══════════════════════════════════════════════════════

// Constructor rút gọn: Kotlin tự tạo các field private final và ghi nớp vào thành viên
class GridSpacingItemDecoration(
    private val spanCount: Int,   // Số cột (vd: 4)
    private val spacing: Int,     // Khoảng cách tính bằng pixel
    private val includeEdge: Boolean  // Có thêm cạnh ngoài không?
) : RecyclerView.ItemDecoration() {

    // "override fun" = @Override public void trong Java
    override fun getItemOffsets(
        outRect: Rect,       // Đối tượng chứa giá trị offset (left/top/right/bottom)
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Vị trí item trong danh sách
        val position = parent.getChildAdapterPosition(view)
        // Vị trí cột: position % spanCount (vd: 0,1,2,3 cho 4 cột)
        val column = position % spanCount

        if (includeEdge) {
            // Tính left/right sao cho khoảng cách chia đều cả 2 bên
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            // Hàng đầu tiên: thêm spacing phía trên
            if (position < spanCount) outRect.top = spacing
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            // Không phải hàng đầu: thêm top spacing
            if (position >= spanCount) outRect.top = spacing
        }
    }
}
