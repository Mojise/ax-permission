package com.ax.library.ax_permission.customview

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.core.content.withStyledAttributes
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.util.DrawableUtil
import com.ax.library.ax_permission.util.dp

internal class BoxButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InteractiveTextView(context, attrs, defStyleAttr) {

    init {
        var boxButtonType: Type = Type.Primary
        context.withStyledAttributes(attrs, R.styleable.BoxButton) {
            boxButtonType = getInt(R.styleable.BoxButton_box_buttonType, Type.Primary.ordinal)
                .let(Type.entries::get)
        }

        val configurations = AxPermission.configurations

        // 기본 스타일 설정
        setPadding(0, 14.dp, 0, 14.dp)

        // 포그라운드 리플 효과
        foreground = DrawableUtil.createRippleDrawable(
            cornerRadius = configurations.cornerRadius,
            rippleColor = when (boxButtonType) {
                Type.Primary   -> context.getColor(R.color.ax_permission_primary_button_ripple_color)
                Type.Secondary -> context.getColor(R.color.ax_permission_ripple_color)
            }
        )

        // 배경 색상
        background = DrawableUtil.createGradientDrawable(
            cornerRadius = configurations.cornerRadius,
            backgroundColor = when (boxButtonType) {
                Type.Primary   -> context.getColor(configurations.primaryColorResId)
                Type.Secondary -> context.getColor(R.color.ax_permission_secondary_button_background_color)
//                Type.Tertiary  -> context.getColor(R.color.ax_permission_tertiary_button_background_color)
            },
        )

        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setTextColor(
            when (boxButtonType) {
                Type.Primary   -> context.getColor(R.color.ax_permission_white)
                Type.Secondary -> context.getColor(configurations.textTitleColorResId)
//                Type.Tertiary  -> context.getColor(configurations.textTitleColorResId)
            }
        )
        typeface = resources.getFont(R.font.ax_permission_pretendard_700_bold)

        includeFontPadding = false
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END

        isClickable = true
        isFocusable = true
    }

    private enum class Type {
        Primary, Secondary,
//        Tertiary
    }
}