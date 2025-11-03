package com.ax.library.ax_permission.permission

import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ax.library.ax_permission.model.PermissionType

internal object PermissionRequestHelper {

    fun request(
        activity: AppCompatActivity,
        permissionType: PermissionType,
    ) {
        when (permissionType) {
            PermissionType.DrawOverlays -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = "package:${activity.applicationContext.packageName}".toUri()
                }
                activity.startActivity(intent)
            }
            PermissionType.AccessNotifications -> {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                activity.startActivity(intent)
            }
            PermissionType.IgnoreBatteryOptimizations -> {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:${activity.applicationContext.packageName}".toUri()
                }
                activity.startActivity(intent)
            }
        }
    }
}