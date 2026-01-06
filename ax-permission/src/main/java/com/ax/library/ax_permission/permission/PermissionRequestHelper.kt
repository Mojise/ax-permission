package com.ax.library.ax_permission.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.ax.library.ax_permission.common.TAG

internal object PermissionRequestHelper {

    /**
     * 특별 권한 요청 (Settings 화면으로 이동)
     * 런타임 권한은 이 메서드를 사용하지 않음 (PermissionActivity에서 ActivityResultLauncher 사용)
     *
     * @param action Settings 액션 문자열 (예: Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
     */
    fun requestSpecialPermission(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        action: String,
    ) {
        val intent = Intent(action).apply {
            // 일부 설정 액션은 패키지 URI가 필요
            if (needsPackageUri(action)) {
                data = "package:${context.applicationContext.packageName}".toUri()
            }
        }

        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            // 설정 액션을 처리할 수 없는 경우 예외 발생할 수 있음
            Log.w(TAG, "requestSpecialPermission()", e)

            if (intent.data == null) {
                intent.data = "package:${context.applicationContext.packageName}".toUri()
            } else {
                intent.data = null
            }

            try {
                launcher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "requestSpecialPermission() - 재시도도 실패", e)
            }
        }
    }

    /**
     * 해당 설정 액션이 패키지 URI를 필요로 하는지 확인
     */
    private fun needsPackageUri(settingsAction: String): Boolean {
        return when (settingsAction) {
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> true
            else -> false
        }
    }

    /**
     * 앱 설정 화면으로 이동 (영구 거부된 런타임 권한용)
     *
     * 사용자가 런타임 권한을 영구 거부한 경우,
     * 앱 설정 화면으로 이동하여 수동으로 권한을 허용하도록 안내합니다.
     */
    fun openAppSettings(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
    ) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.applicationContext.packageName}".toUri()
        }

        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "openAppSettings() - 앱 설정 화면을 열 수 없음", e)
        }
    }
}
