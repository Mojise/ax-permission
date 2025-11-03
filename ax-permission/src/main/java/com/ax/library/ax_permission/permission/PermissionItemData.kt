package com.ax.library.ax_permission.permission

import android.content.Context
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.PermissionType

internal object PermissionItemData {

    fun generateInitialItems(
        context: Context,
        requiredPermissionTypes: List<PermissionType>,
        optionalPermissionTypes: List<PermissionType>,
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
        type: PermissionType,
        itemId: Int,
        isRequired: Boolean,
    ): Item.Permission = when (type) {
        PermissionType.DrawOverlays -> Item.Permission(
            id = itemId,
            type = type,
            iconDrawableResId = R.drawable.ic_ax_permission_draw_overlays,
            name = context.getString(R.string.ax_permission_draw_overlays_name),
            description = context.getString(R.string.ax_permission_draw_overlays_description),
            isRequired = isRequired,
            isGranted = PermissionChecker.check(context, type),
        )
        PermissionType.AccessNotifications -> Item.Permission(
            id = itemId,
            type = type,
            iconDrawableResId = R.drawable.ic_ax_permission_alarm,
            name = context.getString(R.string.ax_permission_access_notification_name),
            description = context.getString(R.string.ax_permission_access_notification_description),
            isRequired = isRequired,
            isGranted = PermissionChecker.check(context, type),
        )
        PermissionType.IgnoreBatteryOptimizations -> Item.Permission(
            id = itemId,
            type = type,
            iconDrawableResId = R.drawable.ic_ax_permission_battery,
            name = context.getString(R.string.ax_permission_ignore_battery_optimization_name),
            description = context.getString(R.string.ax_permission_ignore_battery_optimization_description),
            isRequired = isRequired,
            isGranted = PermissionChecker.check(context, type),
        )
        else -> throw IllegalArgumentException("Unsupported permission type: $type")
    }
}