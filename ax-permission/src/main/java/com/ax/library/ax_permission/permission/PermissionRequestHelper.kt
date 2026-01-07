package com.ax.library.ax_permission.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.ax.library.ax_permission.common.TAG
import com.ax.library.ax_permission.util.showToast

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
        val packageManager = context.packageManager

        // 1차 시도: 패키지 URI 포함
        val intentWithPackage = Intent(action).apply {
            data = "package:${context.applicationContext.packageName}".toUri()
        }

        // Intent를 처리할 수 있는지 먼저 확인
        if (intentWithPackage.resolveActivity(packageManager) != null) {
            try {
                launcher.launch(intentWithPackage)
                return
            } catch (e: ActivityNotFoundException) {
                Log.w(TAG, "requestSpecialPermission() - 패키지 URI 포함 launch 실패", e)
            }
        } else {
            Log.w(TAG, "requestSpecialPermission() - 패키지 URI 포함 Intent 해석 불가, URI 제외 후 재시도")
        }

        // 2차 시도: 패키지 URI 제외
        val intentWithoutPackage = Intent(action)

        if (intentWithoutPackage.resolveActivity(packageManager) != null) {
            try {
                launcher.launch(intentWithoutPackage)
                return
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "requestSpecialPermission() - 패키지 URI 제외 launch 실패", e)
            }
        } else {
            Log.e(TAG, "requestSpecialPermission() - 패키지 URI 제외 Intent도 해석 불가")
        }

        // 모든 시도 실패
        context.showToast("권한 설정 화면을 열 수 없습니다. 관리자에게 문의해 주세요.")
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
