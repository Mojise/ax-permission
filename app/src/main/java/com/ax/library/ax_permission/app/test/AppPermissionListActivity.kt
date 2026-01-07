package com.ax.library.ax_permission.app.test

import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.ax.library.ax_permission.app.R

class AppPermissionListActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AppPermissionList"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppPermissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_permission_list)

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_container)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        adapter = AppPermissionAdapter(packageManager)
        recyclerView.adapter = adapter

        // Load app's permissions
        loadAppPermissions()
    }

    private fun loadAppPermissions() {
        try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )

            val requestedPermissions = packageInfo.requestedPermissions

            if (requestedPermissions.isNullOrEmpty()) {
                Log.d(TAG, "================================================================================")
                Log.d(TAG, "App has NO permissions declared in Manifest")
                Log.d(TAG, "================================================================================")
                return
            }

            // Get PermissionInfo for each permission
            val permissionInfoList = mutableListOf<PermissionInfo>()

            Log.d(TAG, "================================================================================")
            Log.d(TAG, "App's Declared Permissions (${requestedPermissions.size} total)")
            Log.d(TAG, "================================================================================")

            requestedPermissions.forEach { permissionName ->
                try {
                    val permissionInfo = packageManager.getPermissionInfo(permissionName, 0)
                    permissionInfoList.add(permissionInfo)

                    Log.d(TAG, "")
                    Log.d(TAG, "Permission: $permissionName")
                    Log.d(TAG, "  Label: ${permissionInfo.loadLabel(packageManager)}")
                    Log.d(TAG, "  Description: ${permissionInfo.loadDescription(packageManager) ?: "No description"}")
                    Log.d(TAG, "  Group: ${permissionInfo.group ?: "No group"}")
                    Log.d(TAG, "  Protection: ${getProtectionLevelString(permissionInfo.protection)} (raw: ${permissionInfo.protection})")
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(TAG, "Permission not found: $permissionName")
                }
            }

            Log.d(TAG, "")
            Log.d(TAG, "================================================================================")
            Log.d(TAG, "Successfully loaded ${permissionInfoList.size} permissions")
            Log.d(TAG, "================================================================================")

            adapter.submitList(permissionInfoList)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading app permissions: ${e.message}", e)
        }
    }

    private fun getProtectionLevelString(protection: Int): String {
        val baseProtection = protection and PermissionInfo.PROTECTION_MASK_BASE
        return when (baseProtection) {
            PermissionInfo.PROTECTION_NORMAL -> "normal"
            PermissionInfo.PROTECTION_DANGEROUS -> "dangerous"
            PermissionInfo.PROTECTION_SIGNATURE -> "signature"
            PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM -> "signatureOrSystem"
            else -> "unknown($protection)"
        }
    }
}
