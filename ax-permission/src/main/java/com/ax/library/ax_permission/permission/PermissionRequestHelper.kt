package com.ax.library.ax_permission.permission

import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ax.library.ax_permission.model.Permission

internal object PermissionRequestHelper {

    /**
     * 특별 권한 요청 (Settings 화면으로 이동)
     * 런타임 권한은 이 메서드를 사용하지 않음 (PermissionActivity에서 ActivityResultLauncher 사용)
     */
    fun requestSpecialPermission(
        activity: AppCompatActivity,
        permission: Permission.Special,
    ) {
        val intent = Intent(permission.settingsAction).apply {
            // 일부 설정 액션은 패키지 URI가 필요
            if (needsPackageUri(permission.settingsAction)) {
                data = "package:${activity.applicationContext.packageName}".toUri()
            }
        }

        activity.startActivity(intent)
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
}