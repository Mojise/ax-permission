package com.ax.library.ax_permission.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.model.Permission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivityKotlin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_container)) { view, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            var time = 3
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (time > 0) {
                    delay(1000)
                    time--
                    if (time == 0) {
                        checkPermission()
                    }
                }
            }
        }
    }

    private fun checkPermission() {

        AxPermission.from(this)
            .setDayNightTheme()
//            .setAppName(R.string.app_name_soomtalk)
            //.setAppName(R.string.app_name_media_sleep_timer)
            .setAppName(R.string.app_name_sleep_timer)
            .setIconPaddingsDp(10)
            .setPrimaryColor(com.ax.library.ax_permission.R.color.ax_permission_primary_color)
            .setRequiredPermissions(
                // 다른 앱 위에 표시 권한
                Permission.Special.ActionManageOverlayPermission()
                    .copy(iconResId = R.drawable.ic_stacks, titleResId = R.string.test_permission_manage_overlay_permission_title, descriptionResId = R.string.test_permission_manage_overlay_permission_description),
                // 알림 접근 권한
                Permission.Special.ActionNotificationListenerSettings()
                    .copy(iconResId = R.drawable.ic_notifications, titleResId = R.string.test_permission_notification_listener_settings_title, descriptionResId = R.string.test_permission_notification_listener_settings_description),

                // 배터리 최적화 제외 권한
                Permission.Special.ActionRequestIgnoreBatteryOptimizations(),


            )
            .setOptionalPermissions(
                // 카메라 권한
                Permission.Runtime.Camera(),

                // 위치 권한
                Permission.Runtime.AccessFineAndCoarseLocation(),

                // 캘린더 읽기 권한
                Permission.Runtime.ReadCalendar(),
            )
            .setCallback(object : AxPermission.Callback {
                override fun onRequiredPermissionsAllGranted(context: Context) {
                    // Handle all required permissions granted
                    Log.d(TAG, "onRequiredPermissionsAllGranted()")
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                override fun onRequiredPermissionsAnyOneDenied() {
                    // Handle any required permission denied
                    Log.d(TAG, "onRequiredPermissionsAnyOneDenied()")
                    finishAffinity()
                }
            })
            .checkAndShow()

        finish()
    }
}