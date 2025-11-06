package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.R

internal data class PermissionDefaultResources(
    @field:DrawableRes val iconResId: Int?,
    @field:StringRes val titleResId: Int,
    @field:StringRes val descriptionResId: Int,
)

@JvmSynthetic
internal fun Permission2.getDefaultResources(): PermissionDefaultResources = when (this) {
    Permission2.Runtime.ACCESS_FINE_LOCATION ->
        PermissionDefaultResources(
            iconResId = TODO(),
            titleResId = TODO(),
            descriptionResId = TODO(),
        )
    Permission2.Runtime.ACCESS_COARSE_LOCATION ->
        PermissionDefaultResources(
            iconResId = TODO(),
            titleResId = TODO(),
            descriptionResId = TODO(),
        )
    Permission2.Runtime.READ_MEDIA_AUDIO ->
        PermissionDefaultResources(
            iconResId = TODO(),
            titleResId = TODO(),
            descriptionResId = TODO(),
        )
}

@JvmSynthetic
internal fun PermissionGroup.getDefaultResourcesOrThrow(): PermissionDefaultResources {
    return when {
        permissions.all { it == Permission2.Runtime.ACCESS_FINE_LOCATION || it == Permission2.Runtime.ACCESS_COARSE_LOCATION } ->
            PermissionDefaultResources(
                iconResId = TODO(),
                titleResId = TODO(),
                descriptionResId = TODO(),
            )

        // TODO: Add more groups with default resources as needed

        else ->
            throw IllegalArgumentException("Default resources not found for the given permissions. Please provide all resource IDs.")
    }
}