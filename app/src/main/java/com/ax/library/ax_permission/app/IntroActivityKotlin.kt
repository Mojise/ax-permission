package com.ax.library.ax_permission.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ax.library.ax_permission.ax.AxPermission
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

        Log.e(TAG, """
            READ_MEDIA_IMAGES.groupName()=${Manifest.permission.READ_MEDIA_IMAGES.groupName()}
            READ_MEDIA_VIDEO.groupName()=${Manifest.permission.READ_MEDIA_VIDEO.groupName()}
            READ_MEDIA_VISUAL_USER_SELECTED.groupName()=${Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED.groupName()}
            ACCESS_FINE_LOCATION.groupName()=${Manifest.permission.ACCESS_FINE_LOCATION.groupName()}
            ACCESS_COARSE_LOCATION.groupName()=${Manifest.permission.ACCESS_COARSE_LOCATION.groupName()}
        """.trimIndent())

        lifecycleScope.launch {
            var time = 3
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (time > 0) {
                    delay(1500)
                    time--
                    if (time == 0) {
                        checkPermission()
                    }
                }
            }
        }
    }

    private fun String.groupName(): String? {
        return try {
            val permissionInfo = packageManager.getPermissionInfo(
                this,
                PackageManager.GET_META_DATA
            )
            permissionInfo.group
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }


    private fun checkPermission() {
        //Manifest.permission.READ_MEDIA_VIDEO.foo()

        AxPermission.from(this)
            .setDayNightTheme()
//            .setAppName(R.string.app_name_soomtalk)
            //.setAppName(R.string.app_name_media_sleep_timer)
            .setAppName(R.string.app_name_sleep_timer)
            .setIconPaddingsDp(10)
            .setPrimaryColor(com.ax.library.ax_permission.R.color.ax_permission_primary_color)
            .setPrimaryColor(com.ax.library.ax_permission.R.color.ax_permission_black)
            // 필수 권한 목록
            .setRequiredPermissions {
                // 다른 앱 위에 표시 권한
                add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)

                // 알림 접근 권한
                add(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)

                // 접근성 권한
                add(Settings.ACTION_ACCESSIBILITY_SETTINGS)

                // 위치 권한
                add(
                    iconResId = com.ax.library.ax_permission.R.drawable.ic_ax_permission_storage,
                    permissions = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )

                add(
                    permissions = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                        )
                        Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                        )
                        else -> arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                        )
                    }
                )

                // 미디어 권한
                add(
                    iconResId = com.ax.library.ax_permission.R.drawable.ic_ax_permission_storage,
                    titleResId = R.string.test_permission_manage_overlay_permission_title,
                    descriptionResId = R.string.test_permission_manage_overlay_permission_description,
                    permissions = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                        )
                        Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                        )
                        else -> arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                        )
                    }
                )
            }
            // 선택 권한 목록
            .setOptionalPermissions {
                // 카메라 권한
                add(Manifest.permission.CAMERA)
            }

//            .setRequiredPermissions(
////                Permission.Special.ACTION_MANAGE_OVERLAY_PERMISSION,
////                Permission.Special.ACTION_NOTIFICATION_LISTENER_SETTINGS,
////                Permission.Special.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
//
//                Permission.Runtime.CAMERA,
//
////                Permission.runtimeGroup(
////                    iconResId = com.ax.library.ax_permission.R.drawable.ic_ax_permission_location,
////                    titleResId = R.string.app_name_sleep_timer,
////                    descriptionResId = R.string.app_name_sleep_timer,
////                    Permission.Runtime.ACCESS_FINE_LOCATION,
////                    Permission.Runtime.ACCESS_COARSE_LOCATION,
////                ),
//                Permission.Runtime.ACCESS_FINE_LOCATION,
////                Permission.Runtime.ACCESS_COARSE_LOCATION,
//
////                Permission.Runtime.READ_MEDIA_IMAGES,
////                Permission.Runtime.READ_MEDIA_VIDEO,
////                Permission.Runtime.READ_MEDIA_VISUAL_USER_SELECTED,
//
//                //Permission.Runtime.READ_MEDIA_IMAGES + Permission.Runtime.READ_MEDIA_VIDEO + Permission.Runtime.READ_MEDIA_VISUAL_USER_SELECTED,
//
////                Permission.runtimeGroup(
////                    Permission.Runtime.READ_MEDIA_IMAGES,
////                    Permission.Runtime.READ_MEDIA_VIDEO,
////                    Permission.Runtime.READ_MEDIA_VISUAL_USER_SELECTED,
////                ),
//
////                Permission.runtimeGroup(
////                    Permission.Runtime.ACCESS_FINE_LOCATION,
////                    Permission.Runtime.ACCESS_COARSE_LOCATION,
////                ),
//
////                Permission.Runtime.READ_CALENDAR,
//            )

//            .setRequiredPermissions(
//                // 다른 앱 위에 표시 권한
//                Permission.Special.ActionManageOverlayPermission()
//                    .copy(iconResId = R.drawable.ic_stacks, titleResId = R.string.test_permission_manage_overlay_permission_title, descriptionResId = R.string.test_permission_manage_overlay_permission_description),
//                // 알림 접근 권한
//                Permission.Special.ActionNotificationListenerSettings()
//                    .copy(iconResId = R.drawable.ic_notifications, titleResId = R.string.test_permission_notification_listener_settings_title, descriptionResId = R.string.test_permission_notification_listener_settings_description),
//
//                // 배터리 최적화 제외 권한
//                Permission.Special.ActionRequestIgnoreBatteryOptimizations(),
//
//                // 카메라 권한
//                Permission.Runtime.Camera(),
//                // 위치 권한
////                Permission.Runtime.AccessFineAndCoarseLocation(),
//                Permission.Runtime.AccessFineLocation(),
//                Permission.Runtime.AccessCoarseLocation(),
//                // 캘린더 읽기 권한
//                Permission.Runtime.ReadCalendar(),
//            )
//            .setOptionalPermissions(
//                // 연락처 읽기 권한
//                Permission.Runtime.ReadContacts(),
//                // 전화 걸기 권한
//                Permission.Runtime.CallPhone(),
////                // 카메라 권한
////                Permission.Runtime.Camera(),
////                // 위치 권한
////                Permission.Runtime.AccessFineAndCoarseLocation(),
////                // 캘린더 읽기 권한
////                Permission.Runtime.ReadCalendar(),
//            )
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