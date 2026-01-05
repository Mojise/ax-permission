package com.ax.library.ax_permission.model

import android.Manifest
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes


public sealed interface Permission {

    public val constant: String

    public enum class Special(
        override val constant: String,
    ) : Permission {

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
        override val constant: String,
    ) : Permission {

        // Camera & Microphone
        CAMERA(Manifest.permission.CAMERA),
        RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),

        // Location
        ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
        ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
        ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),

        // Media (Android 13+)
        READ_MEDIA_IMAGES(Manifest.permission.READ_MEDIA_IMAGES),
        READ_MEDIA_VIDEO(Manifest.permission.READ_MEDIA_VIDEO),
        READ_MEDIA_AUDIO(Manifest.permission.READ_MEDIA_AUDIO),
        READ_MEDIA_VISUAL_USER_SELECTED(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED),

        // Storage (Legacy)
        READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
        WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),

        // Notifications
        POST_NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),

        // Contacts
        READ_CONTACTS(Manifest.permission.READ_CONTACTS),
        WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS),

        // Phone
        READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
        CALL_PHONE(Manifest.permission.CALL_PHONE),

        // Calendar
        READ_CALENDAR(Manifest.permission.READ_CALENDAR),
        WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR);

        public operator fun plus(other: Runtime): PermissionRuntimeGroup {
            return PermissionRuntimeGroup(listOf(this, other))
        }

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
        @JvmStatic
        public fun runtimeGroup(
            iconResId: Int,
            titleResId: Int,
            descriptionResId: Int,
            vararg permissions: Runtime,
        ): PermissionRuntimeGroup {
            return PermissionRuntimeGroup(permissions.toList())
        }
    }
}

public class PermissionRuntimeGroup internal constructor(
    public val permissions: List<Permission.Runtime>,
) {

    public operator fun plus(other: Permission.Runtime): PermissionRuntimeGroup {
        val newPermissions = this.permissions.toMutableList()
        newPermissions.add(other)
        return PermissionRuntimeGroup(newPermissions)
    }

    @JvmOverloads
    public fun withResources(
        @DrawableRes iconResId: Int? = null,
        @StringRes titleResId: Int? = null,
        @StringRes descriptionResId: Int? = null,
    ): PermissionsWithResources = withResourcesInternal(iconResId, titleResId, descriptionResId)


    public companion object {

        @JvmStatic
        public operator fun invoke(
            vararg permissions: Permission.Runtime,
        ): PermissionRuntimeGroup {
            return PermissionRuntimeGroup(permissions.toList())
        }
    }
}