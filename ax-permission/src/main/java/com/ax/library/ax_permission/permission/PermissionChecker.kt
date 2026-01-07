package com.ax.library.ax_permission.permission

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.PowerManager
import android.os.Process
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

            Settings.ACTION_ACCESSIBILITY_SETTINGS ->
                checkAccessibilityServiceEnabled(context)

            Settings.ACTION_NFC_SETTINGS ->
                checkNfcEnabled(context)

            Settings.ACTION_USAGE_ACCESS_SETTINGS ->
                checkUsageStatsPermission(context)

            android.Manifest.permission.WRITE_SETTINGS ->
                Settings.System.canWrite(context)

            else -> false
        }
        return if (isGranted) Result.Special.Granted else Result.Special.Denied
    }

    /**
     * 접근성 서비스가 활성화되어 있는지 확인
     */
    private fun checkAccessibilityServiceEnabled(context: Context): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = ":"
            val packageName = context.packageName

            enabledServices.split(colonSplitter).any { componentName ->
                componentName.startsWith(packageName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking accessibility service", e)
            false
        }
    }

    /**
     * NFC가 활성화되어 있는지 확인
     */
    private fun checkNfcEnabled(context: Context): Boolean {
        return try {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
            nfcAdapter?.isEnabled == true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking NFC status", e)
            false
        }
    }

    /**
     * 사용 기록 접근 권한이 있는지 확인
     */
    private fun checkUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e(TAG, "Error checking usage stats permission", e)
            false
        }
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

        Log.d(TAG, buildString {
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

    // ========================================
    // 상세 상태 판별 (SharedPreferences 기반)
    // ========================================

    /**
     * 단일 런타임 권한의 상세 상태를 확인합니다.
     *
     * @param activity Activity 인스턴스
     * @param permission 권한 문자열
     * @return 권한 상태 (GRANTED, FIRST_TIME, DENIED_CAN_RETRY, DENIED_PERMANENTLY)
     */
    @JvmSynthetic
    internal fun getRuntimePermissionState(activity: Activity, permission: String): RuntimePermissionState {
        val isGranted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            return RuntimePermissionState.GRANTED
        }

        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        if (showRationale) {
            // showRationale=true → 1회 거부됨, 다시 요청 가능
            // 이 상태를 기록해 둡니다
            PermissionRequestTracker.markRationaleShown(activity, permission)
            return RuntimePermissionState.DENIED_CAN_RETRY
        }

        // showRationale=false인 경우
        val wasRationaleEverShown = PermissionRequestTracker.wasRationaleEverShown(activity, permission)

        return if (wasRationaleEverShown) {
            // 과거에 showRationale=true였는데 지금 false → 영구 거부
            RuntimePermissionState.DENIED_PERMANENTLY
        } else {
            // 한 번도 showRationale=true가 아니었음 → 최초 상태 (또는 뒤로가기로 취소)
            RuntimePermissionState.FIRST_TIME
        }
    }

    /**
     * 그룹 런타임 권한의 상세 상태를 확인합니다.
     *
     * - 모두 GRANTED → GRANTED
     * - 하나라도 DENIED_PERMANENTLY → DENIED_PERMANENTLY (Settings 이동 필요)
     * - 그 외 → FIRST_TIME 또는 DENIED_CAN_RETRY (시스템 다이얼로그 표시 가능)
     *
     * @param activity Activity 인스턴스
     * @param permissions 권한 문자열 리스트
     * @return 그룹 권한 상태
     */
    @JvmSynthetic
    internal fun getGroupRuntimePermissionState(activity: Activity, permissions: List<String>): RuntimePermissionState {
        if (permissions.isEmpty()) {
            return RuntimePermissionState.GRANTED
        }

        val states = permissions.map { getRuntimePermissionState(activity, it) }

        Log.d(TAG, buildString {
            appendLine("getGroupRuntimePermissionState() :: $permissions")
            permissions.forEachIndexed { index, permission ->
                appendLine("    [$permission] = ${states[index]}")
            }
        })

        // 모두 허용됨
        if (states.all { it == RuntimePermissionState.GRANTED }) {
            return RuntimePermissionState.GRANTED
        }

        // 하나라도 영구 거부됨 → Settings 이동 필요
        if (states.any { it == RuntimePermissionState.DENIED_PERMANENTLY }) {
            return RuntimePermissionState.DENIED_PERMANENTLY
        }

        // 시스템 다이얼로그 표시 가능
        return if (states.any { it == RuntimePermissionState.DENIED_CAN_RETRY }) {
            RuntimePermissionState.DENIED_CAN_RETRY
        } else {
            RuntimePermissionState.FIRST_TIME
        }
    }

    /**
     * Permission 객체의 상세 상태를 확인합니다.
     */
    @JvmSynthetic
    internal fun getPermissionState(activity: Activity, permission: Permission): RuntimePermissionState {
        return when (permission) {
            is Permission.Special -> {
                val result = checkSpecialPermission(activity, permission.action)
                if (result.isGranted) RuntimePermissionState.GRANTED else RuntimePermissionState.DENIED_CAN_RETRY
            }
            is Permission.Runtime.Single -> {
                getRuntimePermissionState(activity, permission.permission)
            }
            is Permission.Runtime.Group -> {
                getGroupRuntimePermissionState(activity, permission.permissions)
            }
        }
    }

    /**
     * 런타임 권한의 상세 상태
     */
    internal enum class RuntimePermissionState {
        /** 이미 허용됨 */
        GRANTED,

        /** 최초 상태 - 아직 요청한 적 없음 (또는 뒤로가기로 취소) */
        FIRST_TIME,

        /** 1회 거부됨 - 다시 요청 가능 (시스템 다이얼로그 표시 가능) */
        DENIED_CAN_RETRY,

        /** 영구 거부됨 - Settings 이동 필요 */
        DENIED_PERMANENTLY;

        /** 시스템 권한 요청 다이얼로그를 표시할 수 있는지 여부 */
        val canShowSystemDialog: Boolean
            get() = this == FIRST_TIME || this == DENIED_CAN_RETRY

        /** Settings로 이동해야 하는지 여부 */
        val needsSettingsNavigation: Boolean
            get() = this == DENIED_PERMANENTLY
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
