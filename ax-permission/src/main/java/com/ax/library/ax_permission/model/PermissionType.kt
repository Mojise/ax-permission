package com.ax.library.ax_permission.model

import java.io.Serializable

internal enum class PermissionType(
    val isAction: Boolean,
) : Serializable {

    DrawOverlays(true),
    AccessNotifications(true),
    IgnoreBatteryOptimizations(false),
}