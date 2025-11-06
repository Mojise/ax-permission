package com.ax.library.ax_permission.model

import android.Manifest
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


public sealed interface Permission2 : PermissionFoo {

    public enum class Special(
        public val constant: String,
    ) : Permission2 {

        ACTION_MANAGE_OVERLAY_PERMISSION(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),

        ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),

        ACTION_NOTIFICATION_LISTENER_SETTINGS(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);

        /**
         * ### 권한에 리소스 아이디들을 추가하여 [PermissionsWithResources] 객체로 변환합니다.
         */
        @JvmOverloads
        public fun withResources(
            @DrawableRes iconResId: Int? = null,
            @StringRes titleResId: Int? = null,
            @StringRes descriptionResId: Int? = null,
        ): PermissionsWithResources = withResourcesInternal(iconResId, titleResId, descriptionResId)
    }

    public enum class Runtime(
        public val constant: String,
    ) : Permission2 {

        ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
        ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
        ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),

        READ_MEDIA_IMAGES(Manifest.permission.READ_MEDIA_IMAGES),
        READ_MEDIA_VIDEO(Manifest.permission.READ_MEDIA_VIDEO),

        READ_MEDIA_AUDIO(Manifest.permission.READ_MEDIA_AUDIO);

        @JvmOverloads
        public fun withResources(
            @DrawableRes iconResId: Int? = null,
            @StringRes titleResId: Int? = null,
            @StringRes descriptionResId: Int? = null,
        ): PermissionsWithResources = withResourcesInternal(iconResId, titleResId, descriptionResId)
    }

    public companion object {

        /**
         * ### 런타임 권한 그룹 생성
         *
         * ```kotlin
         * // 위치 권한 그룹
         * Permission2.runtimeGroup(
         *    Permission2.Runtime.ACCESS_FINE_LOCATION,
         *    Permission2.Runtime.ACCESS_COARSE_LOCATION,
         * )
         * ```
         *
         * @see <a href="https://developer.android.com/guide/topics/permissions/overview?hl=ko#groups">Android Permission Groups</a>
         */
//        @JvmStatic
//        public fun runtimeGroup(
//            vararg permissions: Runtime,
//        ): List<Runtime> {
//            return permissions.toList()
//        }
        @JvmStatic
        public fun runtimeGroup(
            vararg permissions: Runtime,
        ): PermissionGroup {
            return permissions.toList()
        }
    }
}

public class PermissionGroup private constructor(
    public val permissions: List<Permission2.Runtime>,
) : PermissionFoo {

    @JvmOverloads
    public fun withResources(
        @DrawableRes iconResId: Int? = null,
        @StringRes titleResId: Int? = null,
        @StringRes descriptionResId: Int? = null,
    ): PermissionsWithResources = withResourcesInternal(iconResId, titleResId, descriptionResId)


    public companion object {

        @JvmStatic
        public operator fun invoke(
            vararg permissions: Permission2.Runtime,
        ): PermissionGroup {
            return PermissionGroup(permissions.toList())
        }
    }
}