package com.github.epfl.meili.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopSpacingItemDecoration() : RecyclerView.ItemDecoration() {
    companion object {
        const val CARD_PADDING = 30
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = CARD_PADDING
    }
}
