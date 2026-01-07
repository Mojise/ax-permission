package com.ax.library.ax_permission.app.test

import android.content.pm.PackageManager
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

class AppPermissionAdapter(
    private val packageManager: PackageManager
) : ListAdapter<PermissionInfo, AppPermissionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_permission, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPermissionIcon: ImageView = itemView.findViewById(R.id.iv_permission_icon)
        private val tvPermissionLabel: TextView = itemView.findViewById(R.id.tv_permission_label)
        private val tvPermissionDescription: TextView = itemView.findViewById(R.id.tv_permission_description)
        private val tvPermissionName: TextView = itemView.findViewById(R.id.tv_permission_name)
        private val tvPermissionGroup: TextView = itemView.findViewById(R.id.tv_permission_group)
        private val tvPermissionProtection: TextView = itemView.findViewById(R.id.tv_permission_protection)

        fun bind(permission: PermissionInfo) {
            // Load icon
            val icon = try {
                permission.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }
            ivPermissionIcon.setImageDrawable(icon)

            // Load label
            val label = try {
                permission.loadLabel(packageManager).toString()
            } catch (e: Exception) {
                permission.name
            }
            tvPermissionLabel.text = label

            // Load description
            val description = try {
                permission.loadDescription(packageManager)?.toString()
            } catch (e: Exception) {
                null
            }
            tvPermissionDescription.text = description ?: "No description"

            // Show permission name
            tvPermissionName.text = permission.name

            // Show group
            val group = permission.group ?: "No group"
            tvPermissionGroup.text = "Group: $group"

            // Show protection level
            val protectionLevel = getProtectionLevelString(permission.protection)
            tvPermissionProtection.text = "Protection: $protectionLevel"
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

    private class DiffCallback : DiffUtil.ItemCallback<PermissionInfo>() {
        override fun areItemsTheSame(
            oldItem: PermissionInfo,
            newItem: PermissionInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: PermissionInfo,
            newItem: PermissionInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}
