package com.ax.library.ax_permission.permission

import android.app.Activity
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission

internal object PermissionItemData {

    fun generateInitialItems(
        activity: Activity,
        requiredPermissions: List<Permission>,
        optionalPermissions: List<Permission>,
    ): List<Item> {
        val items = mutableListOf<Item>()
        var index = 0

        if (requiredPermissions.isNotEmpty()) {
            // 필수 권한 헤더 추가
            items.add(
                Item.Header(id = index++, text = activity.getString(R.string.ax_permission_item_required_permission_header))
            )
            // 필수 권한 아이템들 추가
            requiredPermissions.forEach { permission ->
                items.add(
                    generateItem(activity, permission = permission, itemId = index++, isRequired = true)
                )
            }
            // 필수 권한 푸터 추가
            items.add(
                Item.Footer(id = index++, text = activity.getString(R.string.ax_permission_item_required_permission_footer))
            )
        }

        if (optionalPermissions.isNotEmpty()) {
            // 선택 권한 헤더 추가
            items.add(
                Item.Header(id = index++, text = activity.getString(R.string.ax_permission_item_optional_permission_header))
            )
            // 선택 권한 아이템들 추가
            optionalPermissions.forEach { permission ->
                items.add(
                    generateItem(activity, permission = permission, itemId = index++, isRequired = false)
                )
            }
            // 선택 권한 푸터 추가
            items.add(
                Item.Footer(id = index++, text = activity.getString(R.string.ax_permission_item_optional_permission_footer))
            )
        }

        return items
    }

    private fun generateItem(
        activity: Activity,
        permission: Permission,
        itemId: Int,
        isRequired: Boolean,
    ): Item.PermissionItem {
        val isGranted = PermissionChecker.check(activity, permission).isGranted

        return when (permission) {
            is Permission.Special -> Item.PermissionItem.Special(
                id = itemId,
                permission = permission,
                isRequired = isRequired,
                isGranted = isGranted,
                isHighlights = false,
            )

            is Permission.Runtime -> Item.PermissionItem.Runtime(
                id = itemId,
                permission = permission,
                isRequired = isRequired,
                isGranted = isGranted,
                isHighlights = false,
            )
        }
    }
}
