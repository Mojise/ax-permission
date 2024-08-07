package kr.co.permission.permission

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
            create(this)
                .setPermissionListener(configPermissionListener)
                .onReStart()
        }
    }


    private var configPermissionListener: AxPermissionListener = object : AxPermissionListener {
        override fun onPermissionGranted() {
            println("@@ Config 여기를 탑니다 @@@ onPermissionGranted")
        }

        override fun onPermissionDenied() {
            println("@@@ Config onPermissionDenied @@@")
            finishAffinity()
            exitProcess(0)
            //System.exit(0)
        }
    }
}