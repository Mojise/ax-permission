package com.ax.library.ax_permission.model

import android.Manifest
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.R

/**
 * 권한별 기본 리소스 데이터
 */
internal data class DefaultResources(
    @field:DrawableRes val iconResId: Int,
    @field:StringRes val titleResId: Int,
    @field:StringRes val descriptionResId: Int,
) {
    companion object {
        /**
         * 기본 리소스가 없을 때 사용되는 기본값
         */
        val Unknown = DefaultResources(
            iconResId = R.drawable.ic_ax_permission_default,
            titleResId = R.string.ax_permission_unknown_name,
            descriptionResId = R.string.ax_permission_unknown_description,
        )
    }
}

/**
 * 권한 문자열을 키로 하는 기본 리소스 매핑
 */
internal object PermissionDefaultResources {

    /**
     * 런타임 권한 기본 리소스 맵
     */
    private val runtimeResources: Map<String, DefaultResources> = mapOf(
        // Camera & Microphone
        Manifest.permission.CAMERA to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_camera,
            titleResId = R.string.ax_permission_camera_name,
            descriptionResId = R.string.ax_permission_camera_description,
        ),
        Manifest.permission.RECORD_AUDIO to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_microphone,
            titleResId = R.string.ax_permission_microphone_name,
            descriptionResId = R.string.ax_permission_microphone_description,
        ),

        // Location
        Manifest.permission.ACCESS_FINE_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_fine_name,
            descriptionResId = R.string.ax_permission_location_fine_description,
        ),
        Manifest.permission.ACCESS_COARSE_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_coarse_name,
            descriptionResId = R.string.ax_permission_location_coarse_description,
        ),
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_background_name,
            descriptionResId = R.string.ax_permission_location_background_description,
        ),

        // Media (Android 13+)
        Manifest.permission.READ_MEDIA_IMAGES to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),
        Manifest.permission.READ_MEDIA_VIDEO to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),
        Manifest.permission.READ_MEDIA_AUDIO to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),

        // Storage (Legacy)
        Manifest.permission.READ_EXTERNAL_STORAGE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),
        Manifest.permission.WRITE_EXTERNAL_STORAGE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_write_name,
            descriptionResId = R.string.ax_permission_storage_write_description,
        ),

        // Notifications
        Manifest.permission.POST_NOTIFICATIONS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_notification,
            titleResId = R.string.ax_permission_notification_name,
            descriptionResId = R.string.ax_permission_notification_description,
        ),

        // Contacts
        Manifest.permission.READ_CONTACTS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_contacts,
            titleResId = R.string.ax_permission_contacts_read_name,
            descriptionResId = R.string.ax_permission_contacts_read_description,
        ),
        Manifest.permission.WRITE_CONTACTS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_contacts,
            titleResId = R.string.ax_permission_contacts_write_name,
            descriptionResId = R.string.ax_permission_contacts_write_description,
        ),

        // Phone
        Manifest.permission.READ_PHONE_STATE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_phone_name,
            descriptionResId = R.string.ax_permission_phone_description,
        ),
        Manifest.permission.CALL_PHONE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_call,
            titleResId = R.string.ax_permission_call_phone_name,
            descriptionResId = R.string.ax_permission_call_phone_description,
        ),

        // Calendar
        Manifest.permission.READ_CALENDAR to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_calendar,
            titleResId = R.string.ax_permission_calendar_read_name,
            descriptionResId = R.string.ax_permission_calendar_read_description,
        ),
        Manifest.permission.WRITE_CALENDAR to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_calendar,
            titleResId = R.string.ax_permission_calendar_write_name,
            descriptionResId = R.string.ax_permission_calendar_write_description,
        ),
    )

    /**
     * 특별 권한 기본 리소스 맵
     */
    private val specialResources: Map<String, DefaultResources> = mapOf(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_draw_overlays,
            titleResId = R.string.ax_permission_draw_overlays_name,
            descriptionResId = R.string.ax_permission_draw_overlays_description,
        ),
        Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_alarm,
            titleResId = R.string.ax_permission_access_notification_name,
            descriptionResId = R.string.ax_permission_access_notification_description,
        ),
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_battery,
            titleResId = R.string.ax_permission_ignore_battery_optimization_name,
            descriptionResId = R.string.ax_permission_ignore_battery_optimization_description,
        ),
    )

    /**
     * 런타임 권한 그룹 기본 리소스 맵
     * 키는 정렬된 권한 리스트를 조인한 문자열
     */
    private val runtimeGroupResources: Map<Set<String>, DefaultResources> = mapOf(
        // Location permissions group (FINE + COARSE)
        setOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_fine_name,
            descriptionResId = R.string.ax_permission_location_fine_description,
        ),

        // Visual media permissions group (IMAGES + VIDEO)
        setOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),

        // All media permissions group (IMAGES + VIDEO + VISUAL_USER_SELECTED)
        setOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        ),

        // Contacts permissions group (READ + WRITE)
        setOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_contacts,
            titleResId = R.string.ax_permission_contacts_read_name,
            descriptionResId = R.string.ax_permission_contacts_read_description,
        ),

        // Calendar permissions group (READ + WRITE)
        setOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_calendar,
            titleResId = R.string.ax_permission_calendar_read_name,
            descriptionResId = R.string.ax_permission_calendar_read_description,
        ),
    )

    /**
     * 단일 런타임 권한에 대한 기본 리소스 조회
     *
     * @param permission 권한 문자열 (예: Manifest.permission.CAMERA)
     * @return 기본 리소스 또는 null
     */
    fun getForRuntime(permission: String): DefaultResources? {
        return runtimeResources[permission]
    }

    /**
     * 런타임 권한 그룹에 대한 기본 리소스 조회
     *
     * @param permissions 권한 문자열 리스트
     * @return 기본 리소스 또는 null
     */
    fun getForRuntimeGroup(permissions: List<String>): DefaultResources? {
        val permissionSet = permissions.toSet()
        return runtimeGroupResources[permissionSet]
    }

    /**
     * 특별 권한에 대한 기본 리소스 조회
     *
     * @param action Settings 액션 문자열 (예: Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
     * @return 기본 리소스 또는 null
     */
    fun getForSpecial(action: String): DefaultResources? {
        return specialResources[action]
    }
}
