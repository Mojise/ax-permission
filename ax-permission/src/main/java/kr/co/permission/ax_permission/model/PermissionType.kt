package kr.co.permission.ax_permission.model

import java.io.Serializable

internal enum class PermissionType(
    val isAction: Boolean,
) : Serializable {

    DrawOverlays(true),
    AccessNotifications(true),
    IgnoreBatteryOptimizations(false),
}