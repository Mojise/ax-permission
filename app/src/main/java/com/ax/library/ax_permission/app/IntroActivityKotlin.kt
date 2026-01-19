package com.ax.library.ax_permission.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
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
import com.ax.library.ax_permission.app.util.overrideCloseActivityTransitionCompat
import com.ax.library.ax_permission.app.util.overrideOpenActivityTransitionCompat
import com.ax.library.ax_permission.ax.AxPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivityKotlin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overrideOpenActivityTransitionCompat()
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


        val permissionGroups = packageManager.getAllPermissionGroups(PackageManager.GET_META_DATA)
        for (group in permissionGroups) {
            Log.e(TAG, "permission group: ${group.name} - ${group.loadLabel(packageManager)}")
        }




        lifecycleScope.launch {
            var time = 2
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

    override fun finish() {
        super.finish()
        overrideCloseActivityTransitionCompat()
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
            .setAppName(R.string.app_name_test)
//            .setAppName(R.string.app_name_soomtalk)
            //.setAppName(R.string.app_name_media_sleep_timer)
            //.setAppName(R.string.app_name_sleep_timer)
//            .setAppName(R.string.app_name_habittracker)
//            .setIconPaddingsDp(10)
//            .setCornerRadiusDp(8)
//            .setBottomSheetCornerRadiusDp(16)
            //.setPrimaryColor(com.ax.library.ax_permission.R.color.ax_permission_primary_color)
            //.setGrantedItemBackgroundColor(R.color.teal_200_a30)
            .setPrimaryColor(R.color.purple_200)
            // 필수 권한 목록
            .setRequiredPermissions {



                // 다른 앱 위에 표시 권한
                add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)

                // 접근성 권한
                add(Settings.ACTION_ACCESSIBILITY_SETTINGS)

                // 알림 접근 권한
                add(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)

                // 배터리 최적화 제외 권한
                add(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

                // 시스템 설정 변경 권한
                add(Settings.ACTION_MANAGE_WRITE_SETTINGS)

//                add(Manifest.permission.ACCESS_FINE_LOCATION)
//                add(Manifest.permission.ACCESS_COARSE_LOCATION)
//
//                add(
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_VIDEO,
//                )
//                add(Manifest.permission.READ_MEDIA_IMAGES)
//                add(Manifest.permission.READ_MEDIA_VIDEO)

                add(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE,
                )

                add(Manifest.permission.READ_CALENDAR)
                add(Manifest.permission.WRITE_CALENDAR)



//                add(
//                    Manifest.permission.READ_CALENDAR,
//                    Manifest.permission.WRITE_CALENDAR,
//                )

                // 위치 권한
                add(
                    iconResId = com.ax.library.ax_permission.R.drawable.ic_ax_permission_location,
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
            }
//            .setRequiredPermissions {
//                // 다른 앱 위에 표시 권한
//                add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//
//                // 접근성 권한
//                add(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//
//                // 알림 접근 권한
//                add(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
//
//                // 위치 권한
//                add(
//                    iconResId = com.ax.library.ax_permission.R.drawable.ic_ax_permission_location,
//                    permissions = arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                    )
//                )
//
//                // 미디어 권한
//                add(
//                    permissions = when {
//                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
//                            Manifest.permission.READ_MEDIA_IMAGES,
//                            Manifest.permission.READ_MEDIA_VIDEO,
//                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//                        )
//                        Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> arrayOf(
//                            Manifest.permission.READ_MEDIA_IMAGES,
//                            Manifest.permission.READ_MEDIA_VIDEO,
//                        )
//                        else -> arrayOf(
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                        )
//                    }
//                )
//
//                // 오디오 권한
//                add(Manifest.permission.READ_MEDIA_AUDIO)
//            }
            // 선택 권한 목록
            .setOptionalPermissions {
                // 배터리 최적화 제외 권한
                add(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

                // 카메라 권한
                add(Manifest.permission.CAMERA)

                // 캘린더 권한
                add(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR,
                )
            }
            .setCallback(object : AxPermission.Callback {
                override fun onRequiredPermissionsAllGranted(context: Context) {
                    // Handle all required permissions granted
                    Log.d(TAG, "onRequiredPermissionsAllGranted()")
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                override fun onRequiredPermissionsAnyOneDenied() {
                    // Handle any required permission denied
                    Log.d(TAG, "onRequiredPermissionsAnyOneDenied()")
                }
            })
            .checkAndShow()

        finish()
    }
}