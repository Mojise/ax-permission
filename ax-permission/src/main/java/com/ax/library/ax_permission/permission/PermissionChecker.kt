package com.ax.library.ax_permission.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ax.library.ax_permission.model.Permission

internal object PermissionChecker {

    /**
     * 권한이 허용되었는지 확인
     */
    fun check(context: Context, permission: Permission): Boolean {
        return when (permission) {
            is Permission.Special -> checkSpecialPermission(context, permission)
            is Permission.Runtime -> checkRuntimePermission(context, permission)
        }
    }

    /**
     * 특별 권한 체크
     */
    private fun checkSpecialPermission(context: Context, permission: Permission.Special): Boolean {
        return when (permission.settingsAction) {
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION ->
                Settings.canDrawOverlays(context)

            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS ->
                NotificationManagerCompat.getEnabledListenerPackages(context)
                    .contains(context.packageName)

            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS ->
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
                    .isIgnoringBatteryOptimizations(context.packageName)

            else -> false
        }
    }

    /**
     * 런타임 권한 체크
     * manifestPermissions에 포함된 모든 권한이 허용되어야 true 반환
     */
    private fun checkRuntimePermission(context: Context, permission: Permission.Runtime): Boolean {
        val permissions = permission.manifestPermissions

        // manifestPermissions가 비어있으면 (해당 OS 버전에서 불필요) true 반환
        if (permissions.isEmpty()) {
            return true
        }

        // 모든 권한이 허용되어야 true
        return permissions.all { manifestPermission ->
            ContextCompat.checkSelfPermission(context, manifestPermission) == PackageManager.PERMISSION_GRANTED
        }
    }
}