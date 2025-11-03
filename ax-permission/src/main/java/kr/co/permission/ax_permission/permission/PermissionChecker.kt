package kr.co.permission.ax_permission.permission

import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import kr.co.permission.ax_permission.model.PermissionType

internal object PermissionChecker {

    fun check(context: Context, permissionType: PermissionType): Boolean {
        return when (permissionType) {
            PermissionType.DrawOverlays ->
                Settings.canDrawOverlays(context)
            PermissionType.AccessNotifications ->
                NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
            PermissionType.IgnoreBatteryOptimizations ->
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(context.packageName)
        }
    }
}