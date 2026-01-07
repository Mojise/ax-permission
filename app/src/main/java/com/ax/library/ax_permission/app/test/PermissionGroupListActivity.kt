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

class PermissionGroupListActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PermissionGroupList"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PermissionGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_permission_group_list)

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_container)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        adapter = PermissionGroupAdapter(packageManager)
        recyclerView.adapter = adapter

        // Load permission groups
        loadPermissionGroups()
    }

    private fun loadPermissionGroups() {
        val permissionGroups = packageManager.getAllPermissionGroups(PackageManager.GET_META_DATA)

        Log.d(TAG, "================================================================================")
        Log.d(TAG, "Total Permission Groups: ${permissionGroups.size}")
        Log.d(TAG, "================================================================================")

        permissionGroups.forEachIndexed { index, group ->
            Log.d(TAG, "")
            Log.d(TAG, "[$index] Permission Group: ${group.name}")
            Log.d(TAG, "  Label: ${group.loadLabel(packageManager)}")
            Log.d(TAG, "  Description: ${group.loadDescription(packageManager) ?: "No description"}")
            Log.d(TAG, "  Package: ${group.packageName}")

            // Query permissions in this group
            val permissions = try {
                packageManager.queryPermissionsByGroup(group.name, 0)
            } catch (e: Exception) {
                Log.d(TAG, "  Error querying permissions: ${e.message}")
                null
            }

            if (permissions.isNullOrEmpty()) {
                Log.d(TAG, "  Permissions: None")
            } else {
                Log.d(TAG, "  Permissions in group: ${permissions.size}")
                permissions.forEach { permission ->
                    Log.d(TAG, "    - ${permission.name}")
                    Log.d(TAG, "      Label: ${permission.loadLabel(packageManager)}")
                    Log.d(TAG, "      Protection: ${getProtectionLevelString(permission.protection)} (raw: ${permission.protection})")
                }
            }

            Log.d(TAG, "----------------------------------------")
        }

        Log.d(TAG, "")
        Log.d(TAG, "================================================================================")
        Log.d(TAG, "End of Permission Groups List")
        Log.d(TAG, "================================================================================")

        // Additional check: Query individual permissions to see their actual groups
        checkIndividualPermissions()

        adapter.submitList(permissionGroups)
    }

    private fun checkIndividualPermissions() {
        Log.d(TAG, "")
        Log.d(TAG, "================================================================================")
        Log.d(TAG, "Checking Individual Permissions' Actual Groups")
        Log.d(TAG, "================================================================================")

        val testPermissions = listOf(
            "android.permission.CAMERA",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.READ_MEDIA_AUDIO",
            "android.permission.RECORD_AUDIO",
            "android.permission.READ_PHONE_STATE",
            "android.permission.CALL_PHONE",
            "android.permission.READ_CALL_LOG",
            "android.permission.POST_NOTIFICATIONS"
        )

        testPermissions.forEach { permissionName ->
            try {
                val permissionInfo = packageManager.getPermissionInfo(permissionName, 0)
                Log.d(TAG, "$permissionName")
                Log.d(TAG, "  Actual Group: ${permissionInfo.group ?: "null (no group)"}")
                Log.d(TAG, "  Protection: ${getProtectionLevelString(permissionInfo.protection)}")
            } catch (e: PackageManager.NameNotFoundException) {
                Log.d(TAG, "$permissionName - NOT FOUND")
            }
        }

        Log.d(TAG, "================================================================================")

        // Compare with app's declared permissions
        compareWithAppPermissions()
    }

    private fun compareWithAppPermissions() {
        Log.d(TAG, "")
        Log.d(TAG, "================================================================================")
        Log.d(TAG, "Comparing with App's Declared Permissions")
        Log.d(TAG, "================================================================================")

        try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )

            val requestedPermissions = packageInfo.requestedPermissions
            if (requestedPermissions.isNullOrEmpty()) {
                Log.d(TAG, "App has NO permissions declared in Manifest")
            } else {
                Log.d(TAG, "App's Declared Permissions (${requestedPermissions.size} total):")
                requestedPermissions.forEach { permission ->
                    Log.d(TAG, "  - $permission (${try {
                        val info = packageManager.getPermissionInfo(permission, 0)
                        info.group ?: "no group"
                    } catch (e: Exception) {
                        "not found"
                    }})")
                }
            }

            Log.d(TAG, "")
            Log.d(TAG, "Conclusion:")
            Log.d(TAG, "  System permissions shown: 50+ in UNDEFINED group")
            Log.d(TAG, "  App's declared permissions: ${requestedPermissions?.size ?: 0}")
            Log.d(TAG, "  → PackageManager shows ALL system permissions, not just app's permissions")

        } catch (e: Exception) {
            Log.e(TAG, "Error getting app permissions: ${e.message}")
        }

        Log.d(TAG, "================================================================================")
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
