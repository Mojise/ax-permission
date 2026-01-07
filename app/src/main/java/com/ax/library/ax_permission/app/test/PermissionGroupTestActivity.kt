package com.ax.library.ax_permission.app.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ax.library.ax_permission.app.databinding.ActivityPermissionGroupTestBinding

/**
 * 실제 권한 요청을 통해 어떤 권한들이 함께 묶여서 동작하는지 테스트하는 액티비티
 */
class PermissionGroupTestActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PermissionGroupTest"

        // 테스트할 권한 그룹들
        val PHONE_PERMISSIONS = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS
        )

        val CONTACTS_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )

        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val CALENDAR_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )

        val STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )

        val CALL_LOG_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
        )
    }

    private lateinit var binding: ActivityPermissionGroupTestBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d(TAG, "========================================")
        Log.d(TAG, "Permission Request Result:")
        permissions.forEach { (permission, granted) ->
            Log.d(TAG, "  $permission: ${if (granted) "GRANTED" else "DENIED"}")
        }
        Log.d(TAG, "========================================")

        checkAllPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionGroupTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        checkAllPermissions()
    }

    private fun setupButtons() {
        binding.btnTestPhone.setOnClickListener {
            testPermissionGroup("PHONE", PHONE_PERMISSIONS)
        }

        binding.btnTestContacts.setOnClickListener {
            testPermissionGroup("CONTACTS", CONTACTS_PERMISSIONS)
        }

        binding.btnTestLocation.setOnClickListener {
            testPermissionGroup("LOCATION", LOCATION_PERMISSIONS)
        }

        binding.btnTestCalendar.setOnClickListener {
            testPermissionGroup("CALENDAR", CALENDAR_PERMISSIONS)
        }

        binding.btnTestStorage.setOnClickListener {
            testPermissionGroup("STORAGE", STORAGE_PERMISSIONS)
        }

        binding.btnTestCallLog.setOnClickListener {
            testPermissionGroup("CALL_LOG", CALL_LOG_PERMISSIONS)
        }

        binding.btnCheckAll.setOnClickListener {
            checkAllPermissions()
        }
    }

    private fun testPermissionGroup(groupName: String, permissions: Array<String>) {
        Log.d(TAG, "")
        Log.d(TAG, "========================================")
        Log.d(TAG, "Testing $groupName permissions")
        Log.d(TAG, "========================================")

        // Check current status
        Log.d(TAG, "Before request:")
        permissions.forEach { permission ->
            val granted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "  $permission: ${if (granted) "GRANTED" else "NOT GRANTED"}")
        }

        // Request permissions
        Log.d(TAG, "Requesting permissions...")
        permissionLauncher.launch(permissions)
    }

    private fun checkAllPermissions() {
        val resultBuilder = StringBuilder()
        resultBuilder.append("Current Permission Status:\n\n")

        checkGroupPermissions("PHONE", PHONE_PERMISSIONS, resultBuilder)
        checkGroupPermissions("CONTACTS", CONTACTS_PERMISSIONS, resultBuilder)
        checkGroupPermissions("LOCATION", LOCATION_PERMISSIONS, resultBuilder)
        checkGroupPermissions("CALENDAR", CALENDAR_PERMISSIONS, resultBuilder)
        checkGroupPermissions("STORAGE", STORAGE_PERMISSIONS, resultBuilder)
        checkGroupPermissions("CALL_LOG", CALL_LOG_PERMISSIONS, resultBuilder)

        binding.tvResult.text = resultBuilder.toString()

        Log.d(TAG, "")
        Log.d(TAG, "================================================================================")
        Log.d(TAG, "Full Permission Status Check")
        Log.d(TAG, "================================================================================")
        Log.d(TAG, resultBuilder.toString())
        Log.d(TAG, "================================================================================")
    }

    private fun checkGroupPermissions(
        groupName: String,
        permissions: Array<String>,
        resultBuilder: StringBuilder
    ) {
        resultBuilder.append("[$groupName]\n")

        val statuses = permissions.map { permission ->
            val granted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            val shortName = permission.substringAfterLast('.')
            "$shortName: ${if (granted) "✓" else "✗"}"
        }

        statuses.forEach { status ->
            resultBuilder.append("  $status\n")
        }

        // Check if all are same
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        val allDenied = permissions.all {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            resultBuilder.append("  → All GRANTED\n")
        } else if (allDenied) {
            resultBuilder.append("  → All DENIED\n")
        } else {
            resultBuilder.append("  → MIXED (different statuses!)\n")
        }

        resultBuilder.append("\n")
    }
}
