package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.Serializable

/**
 * 권한을 나타내는 sealed interface
 *
 * 특별 권한(Settings 이동 필요)과 런타임 권한을 구분합니다.
 * 각 권한은 UI 표시를 위한 리소스 정보를 포함합니다.
 */
internal sealed interface Permission : Serializable {

    @get:DrawableRes
    val iconResId: Int

    @get:StringRes
    val titleResId: Int

    @get:StringRes
    val descriptionResId: Int

    /**
     * 특별 권한 (Settings 이동 필요)
     *
     * @param action Settings 액션 문자열 (예: Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
     */
    data class Special(
        val action: String,
        @field:DrawableRes override val iconResId: Int,
        @field:StringRes override val titleResId: Int,
        @field:StringRes override val descriptionResId: Int,
    ) : Permission

    /**
     * 런타임 권한
     */
    sealed interface Runtime : Permission {

        /**
         * 단일 런타임 권한
         *
         * @param permission 권한 문자열 (예: Manifest.permission.CAMERA)
         */
        data class Single(
            val permission: String,
            @field:DrawableRes override val iconResId: Int,
            @field:StringRes override val titleResId: Int,
            @field:StringRes override val descriptionResId: Int,
        ) : Runtime

        /**
         * 런타임 권한 그룹
         *
         * @param permissions 권한 문자열 리스트 (예: [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
         */
        data class Group(
            val permissions: List<String>,
            @field:DrawableRes override val iconResId: Int,
            @field:StringRes override val titleResId: Int,
            @field:StringRes override val descriptionResId: Int,
        ) : Runtime
    }
}
