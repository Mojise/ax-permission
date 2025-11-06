package com.ax.library.ax_permission.permission

import android.app.Activity
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.PermissionsWithResources

internal object PermissionItemData {

    fun generateInitialItems(
        activity: Activity,
        requiredPermissions: List<PermissionsWithResources>,
        optionalPermissions: List<PermissionsWithResources>,
    ): List<Item> {
        val items = mutableListOf<Item>()
        var index = 0

        if (requiredPermissions.isNotEmpty()) {
            // 필수 권한 헤더 추가
            items.add(
                Item.Header(id = index++, text = activity.getString(R.string.ax_permission_item_required_permission_header))
            )
            // 필수 권한 아이템들 추가
            requiredPermissions.forEach { permissionsWithResources ->
                items.add(
                    generateItem(activity, permissionsWithResources = permissionsWithResources, itemId = index++, isRequired = true)
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
            optionalPermissions.forEach { permissionsWithResources ->
                items.add(
                    generateItem(activity, permissionsWithResources = permissionsWithResources, itemId = index++, isRequired = false)
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
        permissionsWithResources: PermissionsWithResources,
        itemId: Int,
        isRequired: Boolean,
    ): Item.PermissionItem {
        return when (permissionsWithResources) {
            is PermissionsWithResources.Special -> Item.PermissionItem.Special(
                id = itemId,
                permission = permissionsWithResources.permission,
                iconResId = permissionsWithResources.iconResId ?: 0,
                titleResId = permissionsWithResources.titleResId,
                descriptionResId = permissionsWithResources.descriptionResId,
                isRequired = isRequired,
                isGranted = PermissionChecker.check(activity, permissionsWithResources).isGranted,
                isHighlights = false,
            )

            is PermissionsWithResources.Runtime -> Item.PermissionItem.Runtime(
                id = itemId,
                permissions = permissionsWithResources.permissions,
                iconResId = permissionsWithResources.iconResId ?: 0,
                titleResId = permissionsWithResources.titleResId,
                descriptionResId = permissionsWithResources.descriptionResId,
                isRequired = isRequired,
                isGranted = PermissionChecker.check(activity, permissionsWithResources).isGranted,
                isHighlights = false,
            )
        }
    }
}