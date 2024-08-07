package kr.co.simplebestapp.defaultpack.ax_designsystem.interactive

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.imageview.ShapeableImageView
import kr.co.permission.ax_permission.interactive.AxInteractiveViewDispatcher

class AxInteractiveShapeableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ShapeableImageView(context, attrs, defStyleAttr) {

    private val dispatcher = AxInteractiveViewDispatcher(this, context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled.not()) {
            return super.dispatchTouchEvent(event)
        }
        dispatcher.onTouchEvent(this, event)
        return super.dispatchTouchEvent(event)
    }
}