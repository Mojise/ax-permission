package com.ax.library.ax_permission.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionsWithResources

internal object PermissionChecker {

    private const val TAG = "PermissionChecker"

    /**
     * 권한이 허용되었는지 확인 (PermissionsWithResources)
     */
    @JvmSynthetic
    internal fun check(activity: Activity, permissionsWithResources: PermissionsWithResources): Result {
        return when (permissionsWithResources) {
            is PermissionsWithResources.Special -> checkSpecialPermission2(activity, permissionsWithResources.permission)
            is PermissionsWithResources.Runtime -> checkRuntimePermission2(activity, permissionsWithResources.permissions)
        }
    }

    /**
     * 특별 권한 체크 (Permission2)
     */
    @JvmSynthetic
    internal fun checkSpecialPermission2(context: Context, permission: Permission.Special): Result.Special {
        val isGranted = when (permission) {
            Permission.Special.ACTION_MANAGE_OVERLAY_PERMISSION ->
                Settings.canDrawOverlays(context)

            Permission.Special.ACTION_NOTIFICATION_LISTENER_SETTINGS ->
                NotificationManagerCompat.getEnabledListenerPackages(context)
                    .contains(context.packageName)

            Permission.Special.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS ->
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
                    .isIgnoringBatteryOptimizations(context.packageName)
        }
        return if (isGranted) Result.Special.Granted else Result.Special.Denied
    }

    /**
     * 런타임 권한 체크 (Permission2)
     */
    @JvmSynthetic
    internal fun checkRuntimePermission2(activity: Activity, permissions: List<Permission>): Result.Runtime {
        if (permissions.isEmpty()) {
            return Result.Runtime.Granted
        }

        Log.e(TAG, buildString {
            appendLine("checkRuntimePermission() :: $permissions")

            permissions.forEach { permission ->
                val permissionConstant = permission.constant
                val granted = ContextCompat.checkSelfPermission(activity, permissionConstant) == PackageManager.PERMISSION_GRANTED
                val showRational = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionConstant)
                appendLine("    [$permission]")
                appendLine("      - granted=$granted")
                appendLine("      - showRational=$showRational")
            }
        })

        // 모든 권한이 허용되어야 true
        val isAllGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission.constant) == PackageManager.PERMISSION_GRANTED
        }
        return if (isAllGranted) Result.Runtime.Granted else Result.Runtime.DeniedCanTryOnce
    }
    /**
     * 권한 요청 결과
     */
    internal sealed interface Result {

        val isGranted: Boolean

        val isDenied: Boolean
            get() = isGranted.not()

        /**
         * 특별 권한 요청 결과
         */
        enum class Special : Result {

            /** 승인됨 */
            Granted,

            /** 거부됨 */
            Denied;

            override val isGranted: Boolean
                get() = this == Granted
        }

        /**
         * 런타임 권한 요청 결과
         */
        enum class Runtime : Result {

            /** 아직 한 번도 권한 요청을 하지 않은 상태 (최초의 상태) */
            Initial,

            /** 거부됨 (한 번 더 요청 가능) */
            DeniedCanTryOnce,

            /** 영구 거부됨 */
            DeniedPermanently,

            /** 승인됨 */
            Granted;

            override val isGranted: Boolean
                get() = this == Granted
        }
    }
}