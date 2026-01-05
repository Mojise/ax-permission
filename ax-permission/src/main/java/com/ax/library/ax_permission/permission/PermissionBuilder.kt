package com.ax.library.ax_permission.permission

import android.content.Context
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.model.DefaultResources
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionDefaultResources

@DslMarker
internal annotation class AxPermissionBuilderDsl

/**
 * 권한 빌더
 *
 * DSL을 통해 권한을 추가하고 빌드합니다.
 */
@AxPermissionBuilderDsl
public class PermissionBuilder internal constructor() {

    private val permissionList = mutableListOf<Permission>()

    /**
     * 권한을 추가합니다.
     *
     * 리소스 ID를 제공하지 않으면 기본 리소스가 사용됩니다.
     * 기본 리소스가 없는 권한에 대해 리소스 ID를 제공하지 않으면 예외가 발생합니다.
     *
     * @param permissions 권한 문자열 (Manifest.permission.* 또는 Settings.ACTION_*)
     * @param iconResId 아이콘 리소스 ID (null이면 기본 리소스 사용)
     * @param titleResId 제목 리소스 ID (null이면 기본 리소스 사용)
     * @param descriptionResId 설명 리소스 ID (null이면 기본 리소스 사용)
     */
    public fun add(
        vararg permissions: String,
        @DrawableRes iconResId: Int? = null,
        @StringRes titleResId: Int? = null,
        @StringRes descriptionResId: Int? = null,
    ): PermissionBuilder = apply {
        if (permissions.isEmpty()) {
            return@apply // 권한이 없으면 무시
        }

        val permissionStrings = permissions.toList()

        // 특별 권한 / 런타임 권한 구분
        val isSpecial = isSpecialPermission(permissionStrings.first())

        if (isSpecial) {
            // 특별 권한은 한 번에 하나만 추가 가능
            require(permissionStrings.size == 1) { "특별 권한은 한 번에 하나만 추가할 수 있습니다: ${permissionStrings.first()}" }

            val action = permissionStrings.first()
            val defaultResources = PermissionDefaultResources.getForSpecial(action)
                ?: DefaultResources.Unknown

            permissionList.add(
                Permission.Special(
                    action = action,
                    iconResId = iconResId ?: defaultResources.iconResId,
                    titleResId = titleResId ?: defaultResources.titleResId,
                    descriptionResId = descriptionResId ?: defaultResources.descriptionResId,
                )
            )
        } else {
            // 런타임 권한
            val defaultResources: DefaultResources = if (permissionStrings.size == 1) {
                PermissionDefaultResources.getForRuntime(permissionStrings.first())
            } else {
                PermissionDefaultResources.getForRuntimeGroup(permissionStrings)
            } ?: DefaultResources.Unknown

            val permission: Permission.Runtime = if (permissionStrings.size == 1) {
                Permission.Runtime.Single(
                    permission = permissionStrings.first(),
                    iconResId = iconResId ?: defaultResources.iconResId,
                    titleResId = titleResId ?: defaultResources.titleResId,
                    descriptionResId = descriptionResId ?: defaultResources.descriptionResId,
                )
            } else {
                Permission.Runtime.Group(
                    permissions = permissionStrings,
                    iconResId = iconResId ?: defaultResources.iconResId,
                    titleResId = titleResId ?: defaultResources.titleResId,
                    descriptionResId = descriptionResId ?: defaultResources.descriptionResId,
                )
            }

            permissionList.add(permission)
        }
    }

    /**
     * 권한 리스트를 빌드합니다.
     */
    internal fun build(): List<Permission> = permissionList.toList()

    private companion object {

        /**
         * 알려진 특별 권한 목록
         */
        private val knownSpecialPermissions = setOf(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS,
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        )

        /**
         * 특별 권한인지 확인합니다.
         *
         * @param permission 권한 문자열
         * @return 특별 권한이면 true
         */
        private fun isSpecialPermission(permission: String): Boolean {
            return permission in knownSpecialPermissions ||
                    permission.startsWith("android.settings.")
        }
    }
}
