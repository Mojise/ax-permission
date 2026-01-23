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
            iconResId = 0,
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
            titleResId = R.string.ax_permission_record_audio_name,
            descriptionResId = R.string.ax_permission_record_audio_description,
        ),

        // Location
        Manifest.permission.ACCESS_FINE_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_access_fine_location_name,
            descriptionResId = R.string.ax_permission_access_fine_location_description,
        ),
        Manifest.permission.ACCESS_COARSE_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_access_coarse_location_name,
            descriptionResId = R.string.ax_permission_access_coarse_location_description,
        ),
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_access_background_location_name,
            descriptionResId = R.string.ax_permission_access_background_location_description,
        ),
        Manifest.permission.ACCESS_MEDIA_LOCATION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_access_media_location_name,
            descriptionResId = R.string.ax_permission_access_media_location_description,
        ),

        // Media (Android 13+)
        Manifest.permission.READ_MEDIA_IMAGES to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_read_media_images_name,
            descriptionResId = R.string.ax_permission_read_media_images_description,
        ),
        Manifest.permission.READ_MEDIA_VIDEO to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_read_media_video_name,
            descriptionResId = R.string.ax_permission_read_media_video_description,
        ),
        Manifest.permission.READ_MEDIA_AUDIO to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_read_media_audio_name,
            descriptionResId = R.string.ax_permission_read_media_audio_description,
        ),
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_group_photos_and_videos_name,
            descriptionResId = R.string.ax_permission_group_photos_and_videos_description,
        ),

        // Storage (Legacy)
        Manifest.permission.READ_EXTERNAL_STORAGE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_read_external_storage_name,
            descriptionResId = R.string.ax_permission_read_external_storage_description,
        ),
        Manifest.permission.WRITE_EXTERNAL_STORAGE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_write_external_storage_name,
            descriptionResId = R.string.ax_permission_write_external_storage_description,
        ),

        // Notifications
        Manifest.permission.POST_NOTIFICATIONS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_notification,
            titleResId = R.string.ax_permission_post_notifications_name,
            descriptionResId = R.string.ax_permission_post_notifications_description,
        ),

        // Contacts
        Manifest.permission.READ_CONTACTS to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_read_contacts_name,
            descriptionResId = R.string.ax_permission_read_contacts_description,
        ),
        Manifest.permission.WRITE_CONTACTS to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_write_contacts_name,
            descriptionResId = R.string.ax_permission_write_contacts_description,
        ),

        // Phone
        Manifest.permission.READ_PHONE_STATE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_read_phone_state_name,
            descriptionResId = R.string.ax_permission_read_phone_state_description,
        ),
        Manifest.permission.CALL_PHONE to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_call,
            titleResId = R.string.ax_permission_call_phone_name,
            descriptionResId = R.string.ax_permission_call_phone_description,
        ),
        Manifest.permission.READ_PHONE_NUMBERS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_read_phone_numbers_name,
            descriptionResId = R.string.ax_permission_read_phone_numbers_description,
        ),

        // SMS
        Manifest.permission.SEND_SMS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_send_sms_name,
            descriptionResId = R.string.ax_permission_send_sms_description,
        ),

        // Calendar
        Manifest.permission.READ_CALENDAR to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_read_calendar_name,
            descriptionResId = R.string.ax_permission_read_calendar_description,
        ),
        Manifest.permission.WRITE_CALENDAR to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_write_calendar_name,
            descriptionResId = R.string.ax_permission_write_calendar_description,
        ),

        // Bluetooth (Android 12+)
        Manifest.permission.BLUETOOTH_CONNECT to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_bluetooth_connect_name,
            descriptionResId = R.string.ax_permission_bluetooth_connect_description,
        ),
        Manifest.permission.BLUETOOTH_SCAN to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_bluetooth_scan_name,
            descriptionResId = R.string.ax_permission_bluetooth_scan_description,
        ),

        // Nearby Devices (Android 13+)
        Manifest.permission.NEARBY_WIFI_DEVICES to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_nearby_wifi_devices_name,
            descriptionResId = R.string.ax_permission_nearby_wifi_devices_description,
        ),

        // Activity Recognition
        Manifest.permission.ACTIVITY_RECOGNITION to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_activity_recognition_name,
            descriptionResId = R.string.ax_permission_activity_recognition_description,
        ),
    )

    /**
     * 특별 권한 기본 리소스 맵
     */
    private val specialResources: Map<String, DefaultResources> = mapOf(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_draw_overlays,
            titleResId = R.string.ax_permission_action_manage_overlay_permission_name,
            descriptionResId = R.string.ax_permission_action_manage_overlay_permission_description,
        ),
        Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_notification,
            titleResId = R.string.ax_permission_action_notification_listener_settings_name,
            descriptionResId = R.string.ax_permission_action_notification_listener_settings_description,
        ),
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_action_request_ignore_battery_optimizations_name,
            descriptionResId = R.string.ax_permission_action_request_ignore_battery_optimizations_description,
        ),
        Settings.ACTION_ACCESSIBILITY_SETTINGS to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_accessibility_service,
            titleResId = R.string.ax_permission_action_accessibility_settings_name,
            descriptionResId = R.string.ax_permission_action_accessibility_settings_description,
        ),
        Settings.ACTION_NFC_SETTINGS to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_action_nfc_settings_name,
            descriptionResId = R.string.ax_permission_action_nfc_settings_description,
        ),
        Settings.ACTION_USAGE_ACCESS_SETTINGS to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_action_usage_access_settings_name,
            descriptionResId = R.string.ax_permission_action_usage_access_settings_description,
        ),
        Settings.ACTION_MANAGE_WRITE_SETTINGS to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_action_manage_write_settings_name,
            descriptionResId = R.string.ax_permission_action_manage_write_settings_description,
        ),
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_action_request_schedule_exact_alarm_name,
            descriptionResId = R.string.ax_permission_action_request_schedule_exact_alarm_description,
        ),
    )

    /**
     * 런타임 권한 그룹 기본 리소스 맵
     * 키는 정렬된 권한 리스트를 조인한 문자열
     */
    private val runtimeGroupResources: Map<Set<String>, DefaultResources> = mapOf(
        // ========== 위치 그룹 (Location Group) ==========

        // FINE + COARSE
        setOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_group_location_name,
            descriptionResId = R.string.ax_permission_group_location_description,
        ),

        // ========== 사진 및 동영상 그룹 (Photos and Videos Group) ==========

        // IMAGES + VIDEO
        setOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_group_photos_and_videos_name,
            descriptionResId = R.string.ax_permission_group_photos_and_videos_description,
        ),

        // IMAGES + VIDEO + VISUAL_USER_SELECTED
        setOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_group_photos_and_videos_name,
            descriptionResId = R.string.ax_permission_group_photos_and_videos_description,
        ),

        // ========== 연락처 그룹 (Contacts Group) ==========

        // READ + WRITE
        setOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
        ) to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_group_contacts_name,
            descriptionResId = R.string.ax_permission_group_contacts_description,
        ),

        // ========== 캘린더 그룹 (Calendar Group) ==========

        // READ + WRITE
        setOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
        ) to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_group_calendar_name,
            descriptionResId = R.string.ax_permission_group_calendar_description,
        ),

        // ========== 저장공간 그룹 (Storage Group - Legacy) ==========

        // READ + WRITE (Android 12 이하)
        setOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_group_storage_name,
            descriptionResId = R.string.ax_permission_group_storage_description,
        ),

        // ========== 근처 기기 그룹 (Nearby Devices Group) ==========

        // BLUETOOTH_CONNECT + BLUETOOTH_SCAN
        setOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        ) to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_group_nearby_devices_name,
            descriptionResId = R.string.ax_permission_group_nearby_devices_description,
        ),

        // BLUETOOTH_CONNECT + BLUETOOTH_SCAN + NEARBY_WIFI_DEVICES
        setOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.NEARBY_WIFI_DEVICES,
        ) to DefaultResources(
            iconResId = 0,
            titleResId = R.string.ax_permission_group_nearby_devices_name,
            descriptionResId = R.string.ax_permission_group_nearby_devices_description,
        ),

        // ========== 전화 그룹 (Phone Group) ==========

        // READ_PHONE_STATE + CALL_PHONE
        setOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_group_phone_name,
            descriptionResId = R.string.ax_permission_group_phone_description,
        ),

        // READ_PHONE_STATE + READ_PHONE_NUMBERS
        setOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_group_phone_name,
            descriptionResId = R.string.ax_permission_group_phone_description,
        ),

        // CALL_PHONE + READ_PHONE_NUMBERS
        setOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_NUMBERS,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_group_phone_name,
            descriptionResId = R.string.ax_permission_group_phone_description,
        ),

        // READ_PHONE_STATE + CALL_PHONE + READ_PHONE_NUMBERS
        setOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_NUMBERS,
        ) to DefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_group_phone_name,
            descriptionResId = R.string.ax_permission_group_phone_description,
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
