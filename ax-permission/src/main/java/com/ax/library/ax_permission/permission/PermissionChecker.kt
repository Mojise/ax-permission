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

internal object PermissionChecker {

    private const val TAG = "PermissionChecker"

    /**
     * Permission의 권한이 허용되었는지 확인
     */
    @JvmSynthetic
    internal fun check(activity: Activity, permission: Permission): Result {
        return when (permission) {
            is Permission.Special -> checkSpecialPermission(activity, permission.action)
            is Permission.Runtime.Single -> checkRuntimePermission(activity, listOf(permission.permission))
            is Permission.Runtime.Group -> checkRuntimePermission(activity, permission.permissions)
        }
    }

    /**
     * 특별 권한 체크
     *
     * @param action Settings 액션 문자열 (예: Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
     */
    @JvmSynthetic
    internal fun checkSpecialPermission(context: Context, action: String): Result.Special {
        val isGranted = when (action) {
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
        return if (isGranted) Result.Special.Granted else Result.Special.Denied
    }

    /**
     * 런타임 권한 체크
     *
     * @param permissions 권한 문자열 리스트 (예: [Manifest.permission.CAMERA])
     */
    @JvmSynthetic
    internal fun checkRuntimePermission(activity: Activity, permissions: List<String>): Result.Runtime {
        if (permissions.isEmpty()) {
            return Result.Runtime.Granted
        }

        Log.e(TAG, buildString {
            appendLine("checkRuntimePermission() :: $permissions")

            permissions.forEach { permission ->
                val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                val showRational = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                appendLine("    [$permission]")
                appendLine("      - granted=$granted")
                appendLine("      - showRational=$showRational")
            }
        })

        // 모든 권한이 허용되어야 true
        val isAllGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
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
