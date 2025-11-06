package com.ax.library.ax_permission.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.ax.library.ax_permission.common.TAG
import com.ax.library.ax_permission.model.Permission

internal object PermissionRequestHelper {

    /**
     * 특별 권한 요청 (Settings 화면으로 이동)
     * 런타임 권한은 이 메서드를 사용하지 않음 (PermissionActivity에서 ActivityResultLauncher 사용)
     */
    fun requestSpecialPermission(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        permission: Permission.Special,
    ) {
        val settingsAction = permission.constant
        val intent = Intent(settingsAction).apply {
            // 일부 설정 액션은 패키지 URI가 필요
            if (needsPackageUri(settingsAction)) {
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

        // 애니메이션 적용 코드 (되돌아 올 때, 애니메이션 적용이 안되서 주석 처리함)
//        if (needsActivityAnimation(settingsAction)) {
//            val options = ActivityOptionsCompat
//                .makeCustomAnimation(context, R.anim.ax_permission_move_right_in_activity_for_starting, R.anim.ax_permission_move_left_out_activity_for_starting)
//
//            launcher.launch(intent, options)
//        } else {
//            launcher.launch(intent)
//        }
    }

    /**
     * 특별 권한 요청 (Settings 화면으로 이동) - Deprecated
     */
    @Deprecated("Use requestSpecialPermission with Permission2.Special instead")
    fun requestSpecialPermissionLegacy(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        permission: Permission.Special,
    ) {
        throw UnsupportedOperationException("Legacy Permission.Special is no longer supported. Use Permission2.Special.")
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

    private fun needsActivityAnimation(settingsAction: String): Boolean {
        return when {
            // SDK 34 이상에서는 애니메이션 미적용
            Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                false
            }
            // SDK 34 미만, 애니메이션이 필요 없는 설정 액션들
            else -> when (settingsAction) {
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> false
                else -> true
            }
        }
    }
}