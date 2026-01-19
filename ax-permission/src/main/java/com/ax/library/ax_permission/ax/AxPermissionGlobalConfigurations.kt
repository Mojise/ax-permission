package com.ax.library.ax_permission.ax

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.util.dp
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AxPermissionGlobalConfigurations(

    @field:StringRes
    val appNameResId: Int,

    /**
     * 코너 반경 (기본 값: 8dp)
     */
    val cornerRadius: Float,

    /**
     * 바텀시트 코너 반경 (기본 값: 16dp)
     */
    val bottomSheetCornerRadius: Float,

    /**
     * 권한 아이템 아이콘 패딩 (기본 값: 10dp)
     */
    val iconPaddings: Int,

    /**
     * Primary Color 리소스 ID
     */
    @field:ColorRes
    val primaryColorResId: Int,

    /**
     * 허용된 권한 아이템 배경색 리소스 ID
     */
    @field:ColorRes
    val grantedItemBackgroundColorResId: Int,

    /**
     * 진행 중인 권한 아이템 하이라이트 색상 리소스 ID
     */
    @field:ColorRes
    val highlightColorResId: Int,
) : Parcelable {
    companion object {

        /**
         * 기본 구성 값
         */
        internal val Default = AxPermissionGlobalConfigurations(
            appNameResId = 0,
            cornerRadius = 8f.dp,
            bottomSheetCornerRadius = 16f.dp,
            iconPaddings = 10.dp,
            primaryColorResId = R.color.ax_permission_primary_color,
            grantedItemBackgroundColorResId = 0, // 0이면 primaryColorResId의 30% alpha 사용
            highlightColorResId = R.color.ax_permission_item_highlight_color,
        )
    }
}