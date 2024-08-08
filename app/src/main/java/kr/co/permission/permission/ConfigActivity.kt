package kr.co.permission.permission

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.Manifest
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.co.permission.ax_permission.AxOptionalPermissionsPopUp
import kr.co.permission.ax_permission.AxPermission
import kr.co.permission.ax_permission.AxPermission.Companion.create
import kr.co.permission.ax_permission.listener.AxPermissionListener
import kotlin.system.exitProcess

class ConfigActivity : AppCompatActivity() {
    private lateinit var configButton:Button
    private lateinit var popupButton:Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        configButton = findViewById(R.id.configButton)
        popupButton = findViewById(R.id.popupButton)

        popupButton.setOnClickListener {
            AxOptionalPermissionsPopUp.getInstance(this)
                .optionalPermissionsPopUp(
                    listOf(
                        Manifest.permission.CAMERA
                    ),
                    onOptionalPermissionGranted = {
                        //권한 허용 콜백
                        Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                    },
                    onOptionalPermissionDenied = {
                        //권한 거부 콜백
                        Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                    }
                )
        }


        configButton.setOnClickListener {
            AxPermission.create(this)
                .setPermissionListener(configPermissionListener)
                .onReStart()
        }
    }
    private var configPermissionListener: AxPermissionListener = object : AxPermissionListener {
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