package com.ax.library.ax_permission.permission

import android.content.Context
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission

internal object PermissionItemData {

    fun generateInitialItems(
        context: Context,
        requiredPermissionTypes: List<Permission>,
        optionalPermissionTypes: List<Permission>,
    ): List<Item> {
        val items = mutableListOf<Item>()
        var index = 0

        if (requiredPermissionTypes.isNotEmpty()) {
            items.add(Item.Header(id = index++, text = "※ 필수 권한"))
            requiredPermissionTypes.forEach { type ->
                items.add(generateItem(context, type = type, itemId = index++, isRequired = true))
            }
            items.add(Item.Footer(id = index++, text = "필수 권한은 앱 사용에 반드시 필요합니다."))
        }

        if (optionalPermissionTypes.isNotEmpty()) {
            items.add(Item.Header(id = index++, text = "※ 선택 권한"))
            optionalPermissionTypes.forEach { type ->
                items.add(generateItem(context, type = type, itemId = index++, isRequired = true))
            }
            items.add(Item.Footer(id = index++, text = "선택 권한은 앱 사용에 도움이 되지만, 필수는 아닙니다."))
        }

        return items
    }

    private fun generateItem(
        context: Context,
        type: Permission,
        itemId: Int,
        isRequired: Boolean,
    ): Item.PermissionItem {
        return Item.PermissionItem(
            id = itemId,
            permission = type,
            iconDrawableResId = type.iconResId,
            name = context.getString(type.titleResId),
            description = context.getString(type.descriptionResId),
            isRequired = isRequired,
            isGranted = PermissionChecker.check(context, type),
        )
    }
}