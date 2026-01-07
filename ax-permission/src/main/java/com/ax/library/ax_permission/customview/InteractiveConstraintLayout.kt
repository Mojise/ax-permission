package com.ax.library.ax_permission.customview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnCancel

internal class InteractiveConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val scaleAnimator: ValueAnimator = ValueAnimator.ofFloat()

    @SuppressLint("ClickableViewAccessibility")
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || isEnabled.not() || isClickable.not()) {
            return false
        }

        //Log.w(TAG, "onTouchEvent: event.action=${MotionEvent.actionToString(event.action)}")

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                animatePressEffect(isPressed = true)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                animatePressEffect(isPressed = false)
            }
        }

        return super.dispatchTouchEvent(event)
    }

    private fun animatePressEffect(isPressed: Boolean) {
        if (Looper.myLooper() == null) {
            return
        }
        if (scaleAnimator.isRunning) {
            scaleAnimator.cancel()
        }

        scaleAnimator.let {
            val scaleFrom = if (isPressed) 1.0f else 0.97f
            val scaleTo = if (isPressed) 0.97f else 1.0f
            it.setFloatValues(scaleFrom, scaleTo)

            it.interpolator = DecelerateInterpolator()
            it.duration = 150
            it.addUpdateListener { anim ->
                val animatedValue = anim.animatedValue as Float
                scaleX = animatedValue
                scaleY = animatedValue
            }
            it.doOnCancel {
                scaleX = 1.0f
                scaleY = 1.0f
            }
        }
        scaleAnimator.start()
    }
}