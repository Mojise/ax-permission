package com.ax.library.ax_permission.app.test

import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ax.library.ax_permission.app.R

class PermissionGroupAdapter(
    private val packageManager: PackageManager
) : ListAdapter<PermissionGroupInfo, PermissionGroupAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_permission_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivGroupIcon: ImageView = itemView.findViewById(R.id.iv_group_icon)
        private val tvGroupLabel: TextView = itemView.findViewById(R.id.tv_group_label)
        private val tvGroupDescription: TextView = itemView.findViewById(R.id.tv_group_description)
        private val tvGroupName: TextView = itemView.findViewById(R.id.tv_group_name)
        private val tvPermissionsList: TextView = itemView.findViewById(R.id.tv_permissions_list)
        private val tvPackageName: TextView = itemView.findViewById(R.id.tv_package_name)
        private val tvProtectionLevels: TextView = itemView.findViewById(R.id.tv_protection_levels)

        fun bind(permissionGroup: PermissionGroupInfo) {
            // Load icon
            val icon = permissionGroup.loadIcon(packageManager)
            ivGroupIcon.setImageDrawable(icon)

            // Load label
            val label = permissionGroup.loadLabel(packageManager).toString()
            tvGroupLabel.text = label

            // Load description
            val description = permissionGroup.loadDescription(packageManager)
            tvGroupDescription.text = description?.toString() ?: "No description"

            // Show group name
            tvGroupName.text = permissionGroup.name

            // 1. Load permissions in this group
            val permissions = try {
                packageManager.queryPermissionsByGroup(permissionGroup.name, 0)
            } catch (e: Exception) {
                null
            }
            val permissionNames = permissions?.map {
                it.name.substringAfterLast('.')
            }?.joinToString(", ") ?: "None"
            tvPermissionsList.text = "Permissions: $permissionNames"

            // 2. Package name
            tvPackageName.text = "Package: ${permissionGroup.packageName}"

            // 3. Protection levels
            val protectionLevels = permissions?.map { permission ->
                getProtectionLevelString(permission.protection)
            }?.distinct()?.joinToString(", ") ?: "None"
            tvProtectionLevels.text = "Protection: $protectionLevels"
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

    private class DiffCallback : DiffUtil.ItemCallback<PermissionGroupInfo>() {
        override fun areItemsTheSame(
            oldItem: PermissionGroupInfo,
            newItem: PermissionGroupInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: PermissionGroupInfo,
            newItem: PermissionGroupInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}
