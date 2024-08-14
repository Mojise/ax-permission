package kr.co.permission.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap.Config
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.co.permission.ax_permission.AxPermission
import kr.co.permission.ax_permission.AxPermission.Companion.create
import kr.co.permission.ax_permission.listener.AxPermissionListener
import kr.co.permission.ax_permission.util.AxPermissionList
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {


    private lateinit var testButton: Button

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*필수 권한 리스트*/
        val requiredPermissions = AxPermissionList()
        /*선택 권한 리스트*/
        val optionalPermissions = AxPermissionList()

        requiredPermissions.add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "TEST  권한 타이틀 입니다. ","TEST 권한 내용입니다")
        requiredPermissions.add(Settings.ACTION_ACCESSIBILITY_SETTINGS ,"타이틀만 변경")
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION ,"" , "내용만 변경")
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        optionalPermissions.add(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

        requiredPermissions.add(Manifest.permission.CAMERA)

        // 버전별 권한
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        AxPermission.create(this)
            .setPermissionListener(permissionListener)
            .setRequiredPermissions(requiredPermissions)
            .setOptionalPermissions(optionalPermissions)
            .setSubmitButtonColors(
                buttonBackgroundColor = R.color.purple_200 ,
                textColor = R.color.black
            )
            .check()

        /*config 화면 이동*/
        testButton = findViewById(R.id.testButton)
        testButton.setOnClickListener {
            startActivity(Intent(this , ConfigActivity::class.java))
        }
    }


    private var permissionListener: AxPermissionListener = object : AxPermissionListener {
        override fun onPermissionGranted() {
            /*성공 콜백 리스너*/
        }

        override fun onPermissionDenied() {
            /*실패 콜백 리스너*/
            finishAffinity()
            exitProcess(0)
        }
    }
}