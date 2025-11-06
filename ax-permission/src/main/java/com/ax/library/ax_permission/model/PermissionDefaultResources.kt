package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.R

internal data class PermissionDefaultResources(
    @field:DrawableRes val iconResId: Int,
    @field:StringRes val titleResId: Int,
    @field:StringRes val descriptionResId: Int,
)

@JvmSynthetic
internal fun Permission.getDefaultResources(): PermissionDefaultResources = when (this) {
    // Camera & Microphone
    Permission.Runtime.CAMERA ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_camera,
            titleResId = R.string.ax_permission_camera_name,
            descriptionResId = R.string.ax_permission_camera_description,
        )
    Permission.Runtime.RECORD_AUDIO ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_microphone,
            titleResId = R.string.ax_permission_microphone_name,
            descriptionResId = R.string.ax_permission_microphone_description,
        )

    // Location
    Permission.Runtime.ACCESS_FINE_LOCATION ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_fine_name,
            descriptionResId = R.string.ax_permission_location_fine_description,
        )
    Permission.Runtime.ACCESS_COARSE_LOCATION ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_coarse_name,
            descriptionResId = R.string.ax_permission_location_coarse_description,
        )
    Permission.Runtime.ACCESS_BACKGROUND_LOCATION ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_location,
            titleResId = R.string.ax_permission_location_background_name,
            descriptionResId = R.string.ax_permission_location_background_description,
        )

    // Media (Android 13+)
    Permission.Runtime.READ_MEDIA_IMAGES ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        )
    Permission.Runtime.READ_MEDIA_VIDEO ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        )
    Permission.Runtime.READ_MEDIA_AUDIO ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        )

    // Storage (Legacy)
    Permission.Runtime.READ_EXTERNAL_STORAGE ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_read_name,
            descriptionResId = R.string.ax_permission_storage_read_description,
        )
    Permission.Runtime.WRITE_EXTERNAL_STORAGE ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_storage,
            titleResId = R.string.ax_permission_storage_write_name,
            descriptionResId = R.string.ax_permission_storage_write_description,
        )

    // Notifications
    Permission.Runtime.POST_NOTIFICATIONS ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_notification,
            titleResId = R.string.ax_permission_notification_name,
            descriptionResId = R.string.ax_permission_notification_description,
        )

    // Contacts
    Permission.Runtime.READ_CONTACTS ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_contacts,
            titleResId = R.string.ax_permission_contacts_read_name,
            descriptionResId = R.string.ax_permission_contacts_read_description,
        )
    Permission.Runtime.WRITE_CONTACTS ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_contacts,
            titleResId = R.string.ax_permission_contacts_write_name,
            descriptionResId = R.string.ax_permission_contacts_write_description,
        )

    // Phone
    Permission.Runtime.READ_PHONE_STATE ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_phone,
            titleResId = R.string.ax_permission_phone_name,
            descriptionResId = R.string.ax_permission_phone_description,
        )
    Permission.Runtime.CALL_PHONE ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_call,
            titleResId = R.string.ax_permission_call_phone_name,
            descriptionResId = R.string.ax_permission_call_phone_description,
        )

    // Calendar
    Permission.Runtime.READ_CALENDAR ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_calendar,
            titleResId = R.string.ax_permission_calendar_read_name,
            descriptionResId = R.string.ax_permission_calendar_read_description,
        )
    Permission.Runtime.WRITE_CALENDAR ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_calendar,
            titleResId = R.string.ax_permission_calendar_write_name,
            descriptionResId = R.string.ax_permission_calendar_write_description,
        )

    // Special Permissions
    Permission.Special.ACTION_MANAGE_OVERLAY_PERMISSION ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_draw_overlays,
            titleResId = R.string.ax_permission_draw_overlays_name,
            descriptionResId = R.string.ax_permission_draw_overlays_description,
        )
    Permission.Special.ACTION_NOTIFICATION_LISTENER_SETTINGS ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_alarm,
            titleResId = R.string.ax_permission_access_notification_name,
            descriptionResId = R.string.ax_permission_access_notification_description,
        )
    Permission.Special.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS ->
        PermissionDefaultResources(
            iconResId = R.drawable.ic_ax_permission_battery,
            titleResId = R.string.ax_permission_ignore_battery_optimization_name,
            descriptionResId = R.string.ax_permission_ignore_battery_optimization_description,
        )
}

@JvmSynthetic
internal fun PermissionRuntimeGroup.getDefaultResourcesOrThrow(): PermissionDefaultResources {
    return when {
        // Location permissions group (FINE + COARSE)
        permissions.all { it == Permission.Runtime.ACCESS_FINE_LOCATION || it == Permission.Runtime.ACCESS_COARSE_LOCATION } ->
            PermissionDefaultResources(
                iconResId = R.drawable.ic_ax_permission_location,
                titleResId = R.string.ax_permission_location_fine_name,
                descriptionResId = R.string.ax_permission_location_fine_description,
            )

        // Visual media permissions group (IMAGES + VIDEO)
        permissions.all { it == Permission.Runtime.READ_MEDIA_IMAGES || it == Permission.Runtime.READ_MEDIA_VIDEO } ->
            PermissionDefaultResources(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_read_name,
                descriptionResId = R.string.ax_permission_storage_read_description,
            )

        // All media permissions group (IMAGES + VIDEO + AUDIO)
        permissions.all { it == Permission.Runtime.READ_MEDIA_IMAGES || it == Permission.Runtime.READ_MEDIA_VIDEO || it == Permission.Runtime.READ_MEDIA_AUDIO } ->
            PermissionDefaultResources(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_read_name,
                descriptionResId = R.string.ax_permission_storage_read_description,
            )

        else ->
            throw IllegalArgumentException("Default resources not found for the given permission group: $permissions. Please provide all resource IDs manually using withResources().")
    }
}