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
     * 코너 반경 (기본 값: 12dp)
     */
    val cornerRadius: Float,

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
     * 제목 텍스트 컬러 리소스 ID
     */
    @field:ColorRes
    val textTitleColorResId: Int,

    /**
     * 설명 텍스트 컬러 리소스 ID
     */
    @field:ColorRes
    val textDescriptionColorResId: Int,

    /**
     * 배경 컬러 리소스 ID
     */
    @field:ColorRes
    val backgroundColorResId: Int,
) : Parcelable {
    companion object {

        /**
         * 기본 구성 값
         */
        internal val Default = AxPermissionGlobalConfigurations(
            appNameResId = 0,
            cornerRadius = 8f.dp,
            iconPaddings = 10.dp,
            primaryColorResId = R.color.ax_permission_primary_color,
            textTitleColorResId = R.color.ax_permission_text_color_dark,
            textDescriptionColorResId = R.color.ax_permission_text_color_light,
            backgroundColorResId = R.color.ax_permission_background_color,
        )
    }
}