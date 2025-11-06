package com.ax.library.ax_permission.permission

import android.app.Activity
import android.content.Context
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission

internal object PermissionItemData {

    fun generateInitialItems(
        activity: Activity,
        requiredPermissionTypes: List<Permission>,
        optionalPermissionTypes: List<Permission>,
    ): List<Item> {
        val items = mutableListOf<Item>()
        var index = 0

        if (requiredPermissionTypes.isNotEmpty()) {
            items.add(Item.Header(id = index++, text = "필수 권한"))
            requiredPermissionTypes.forEach { type ->
                items.add(generateItem(activity, type = type, itemId = index++, isRequired = true))
            }
            items.add(Item.Footer(id = index++, text = "* 필수 권한은 모두 허용 후에 앱을 이용할 수 있습니다."))
        }

        if (optionalPermissionTypes.isNotEmpty()) {
            items.add(Item.Header(id = index++, text = "선택 권한"))
            optionalPermissionTypes.forEach { type ->
                items.add(generateItem(activity, type = type, itemId = index++, isRequired = false))
            }
            items.add(Item.Footer(id = index++, text = "* 선택 권한은 허용하지 않아도 앱을 이용할 수 있습니다."))
        }

        //items.add(Item.EmptySpaceFooter(id = index++))

        return items
    }

    private fun generateItem(
        activity: Activity,
        type: Permission,
        itemId: Int,
        isRequired: Boolean,
    ): Item.PermissionItem {
        return Item.PermissionItem(
            id = itemId,
            permission = type,
            isRequired = isRequired,
            isGranted = PermissionChecker.check(activity, type).isGranted,
            isHighlights = false,
        )
    }
}