package com.ax.library.ax_permission.util

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

@SuppressLint("ClickableViewAccessibility")
internal fun ViewPager2.disableUserInputAndTouch() {
    isUserInputEnabled = false
    setOnTouchListener { _, _ -> true }

    (getChildAt(0) as? RecyclerView)?.let {
        it.isNestedScrollingEnabled = false
        it.setOnTouchListener { _, _ -> true }
    }
}