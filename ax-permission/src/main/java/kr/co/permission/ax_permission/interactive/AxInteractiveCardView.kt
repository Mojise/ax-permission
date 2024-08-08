package kr.co.simplebestapp.defaultpack.ax_designsystem.interactive

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.cardview.widget.CardView
import kr.co.permission.ax_permission.interactive.AxInteractiveViewDispatcher

class AxInteractiveCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val helper: AxInteractiveViewDispatcher = AxInteractiveViewDispatcher(this, context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled.not()) {
            return super.dispatchTouchEvent(event)
        }
        helper.onTouchEvent(this, event)
        return super.dispatchTouchEvent(event)
    }
}