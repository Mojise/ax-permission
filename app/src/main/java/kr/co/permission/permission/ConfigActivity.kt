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
import kr.co.permission.ax_permission.AxPermission.Companion.create
import kr.co.permission.ax_permission.listener.AxPermissionListener
import kotlin.system.exitProcess

class ConfigActivity : AppCompatActivity() {
    private lateinit var configButton:Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        configButton = findViewById(R.id.configButton)

        configButton.setOnClickListener {

            AxOptionalPermissionsPopUp.getInstance(this)
                .optionalPermissionsPopUp(
                listOf(
                    Manifest.permission.CAMERA
                ),
                onOptionalPermissionGranted = {
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                },
                onOptionalPermissionDenied = {
                    // Code to run if one or more permissions are denied
                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}