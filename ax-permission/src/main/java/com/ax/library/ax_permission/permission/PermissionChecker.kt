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
     * 권한이 허용되었는지 확인
     */
    @JvmSynthetic
    fun check(activity: Activity, permission: Permission): Result {
        return when (permission) {
            is Permission.Special -> checkSpecialPermission(activity, permission)
            is Permission.Runtime -> checkRuntimePermission(activity, permission)
        }
    }

    /**
     * 특별 권한 체크
     */
    @JvmSynthetic
    fun checkSpecialPermission(context: Context, permission: Permission.Special): Result.Special {
        val isGranted = when (permission.settingsAction) {
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
     * manifestPermissions에 포함된 모든 권한이 허용되어야 true 반환
     */
    @JvmSynthetic
    fun checkRuntimePermission(activity: Activity, permission: Permission.Runtime): Result.Runtime {
        val manifestPermissions = permission.manifestPermissions

        // manifestPermissions가 비어있으면 (해당 OS 버전에서 불필요) true 반환
        if (manifestPermissions.isEmpty()) {
            return Result.Runtime.Granted
        }


        Log.e(TAG, buildString {
            appendLine("checkRuntimePermission() :: $permission")

            manifestPermissions.forEach {
                val granted = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
                val showRational = ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
                appendLine("    [${it.split(".").last()}]")
                appendLine("      - granted=$granted")
                appendLine("      - showRational=$showRational")
            }
        })

        // 모든 권한이 허용되어야 true
        val isAllGranted = manifestPermissions.all { manifestPermission ->
            ContextCompat.checkSelfPermission(activity, manifestPermission) == PackageManager.PERMISSION_GRANTED
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