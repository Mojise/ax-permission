package kr.co.permission.ax_permission.interactive

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import kr.co.permission.ax_permission.R
import kr.co.permission.ax_permission.ext.dp

class AxInteractiveViewDispatcher @JvmOverloads constructor(
    view: View, context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) {

    private var interactiveScaleRatio: Float = SCALE_RATIO_NORMAL

    init {
        view.isClickable = true

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.interactiveCommonAttributes)

        try {
            interactiveScaleRatio = typedArray.getFloat(R.styleable.interactiveCommonAttributes_interactiveScaleRatio, SCALE_RATIO_NORMAL)

            val rippleColor = typedArray.getColor(
                R.styleable.interactiveCommonAttributes_interactiveRippleColor,
                ContextCompat.getColor(context, R.color.ax_ripple_color),
            )
            val cornerRadiusSize = typedArray.getDimension(
                R.styleable.interactiveCommonAttributes_interactiveCornerRadius,
                CORNER_RADIUS_NORMAL.dp,
            )
            val applyBackgroundOrForeground = typedArray.getInt(
                R.styleable.interactiveCommonAttributes_interactiveApplyBackgroundOrForeground,
                BACKGROUND,
            )

            when (applyBackgroundOrForeground) {
                BACKGROUND -> {
                    view.background = generateRippleDrawable(
                        context = context,
                        rippleColor = rippleColor,
                        cornerRadiusSize = cornerRadiusSize,
                    )
                }
                FOREGROUND -> {
                    view.foreground = generateRippleDrawable(
                        context = context,
                        rippleColor = rippleColor,
                        cornerRadiusSize = cornerRadiusSize,
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    fun onTouchEvent(
        view: View,
        event: MotionEvent?,
    ) {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> animateScale(view, true, interactiveScaleRatio)
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> animateScale(view, false, interactiveScaleRatio)
        }
    }

    private fun animateScale(
        view: View,
        isDown: Boolean,
        scaleRatio: Float
    ) {
        view.animate()
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(100)
            .scaleX(if (isDown) scaleRatio else 1f)
            .scaleY(if (isDown) scaleRatio else 1f)
            .withEndAction {
                if (isDown) view.scaleX = scaleRatio else view.scaleX = 1f
                if (isDown) view.scaleY = scaleRatio else view.scaleY = 1f
            }
            .start()
    }

    private fun generateRippleDrawable(
        context: Context,
        @ColorInt rippleColor: Int,
        @Px cornerRadiusSize: Float,
    ): RippleDrawable {
        val maskDrawable = GradientDrawable().apply {
            setColor(rippleColor)
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusSize
        }

        return RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            null,
            maskDrawable
        )
    }

    private fun dpToPixels(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    companion object {
        const val SCALE_RATIO_MUCH = 0.94f
        const val SCALE_RATIO_NORMAL = 0.95f
        const val SCALE_RATIO_LITTLE = 0.96f

        const val BACKGROUND = 0
        const val FOREGROUND = 1

        private const val CORNER_RADIUS_NORMAL = 12f
    }
}