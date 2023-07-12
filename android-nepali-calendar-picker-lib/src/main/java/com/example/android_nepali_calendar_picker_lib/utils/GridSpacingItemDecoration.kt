package com.example.android_nepali_calendar_picker_lib.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class GridSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        view.setPadding(0, spacing, 0, spacing)
    }
}
