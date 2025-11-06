package com.ax.library.ax_permission.util

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import androidx.annotation.ColorInt

internal object DrawableUtil {

    fun createGradientDrawable(
        cornerRadius: Float,
        backgroundColor: Int,
        backgroundSelectedColor: Int? = null,
    ): Drawable = GradientDrawable().also {
        it.shape = GradientDrawable.RECTANGLE
        it.cornerRadius = cornerRadius
        it.color = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf()
            ),
            intArrayOf(
                backgroundSelectedColor ?: backgroundColor,
                backgroundColor
            )
        )
    }

    fun createRippleDrawable(
        cornerRadius: Float,
        @ColorInt rippleColor: Int,
    ): RippleDrawable = RippleDrawable(
        ColorStateList.valueOf(rippleColor),
        null,
        GradientDrawable().also {
            it.shape = GradientDrawable.RECTANGLE
            it.cornerRadius = cornerRadius
            it.setColor(rippleColor)
        }
    )
}