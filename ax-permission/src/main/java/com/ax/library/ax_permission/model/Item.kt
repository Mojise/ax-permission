package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.Serializable

internal sealed interface Item {

    val id: Int

    data class Header(
        override val id: Int,
        val text: String,
    ) : Item

    data class Footer(
        override val id: Int,
        val text: String,
    ) : Item

    data class Divider(
        override val id: Int,
    ) : Item

    sealed interface PermissionItem : Item, Serializable {

        val permission: Permission

        @get:DrawableRes
        val iconResId: Int
            get() = permission.iconResId

        @get:StringRes
        val titleResId: Int
            get() = permission.titleResId

        @get:StringRes
        val descriptionResId: Int
            get() = permission.descriptionResId

        val isRequired: Boolean
        val isGranted: Boolean
        val isHighlights: Boolean

        val isOptional: Boolean
            get() = isRequired.not()

        val isNotGranted: Boolean
            get() = isGranted.not()

        /**
         * 특별 권한 아이템
         *
         * @param permission 특별 권한 (Permission.Special)
         */
        data class Special(
            override val id: Int,
            override val permission: Permission.Special,
            override val isRequired: Boolean,
            override val isGranted: Boolean,
            override val isHighlights: Boolean,
        ) : PermissionItem {
            /** Settings 액션 문자열 (예: Settings.ACTION_MANAGE_OVERLAY_PERMISSION) */
            val action: String get() = permission.action
        }

        /**
         * 런타임 권한 아이템
         *
         * @param permission 런타임 권한 (Permission.Runtime.Single 또는 Permission.Runtime.Group)
         */
        data class Runtime(
            override val id: Int,
            override val permission: Permission.Runtime,
            override val isRequired: Boolean,
            override val isGranted: Boolean,
            override val isHighlights: Boolean,
        ) : PermissionItem {
            /** 권한 문자열 리스트 */
            val permissions: List<String>
                get() = when (permission) {
                    is Permission.Runtime.Single -> listOf(permission.permission)
                    is Permission.Runtime.Group -> permission.permissions
                }
        }
    }

    data class EmptySpaceFooter(
        override val id: Int,
    ) : Item

    companion object {

        private const val NO_ID = -1

        val DiffItemCallback = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
        }
    }
}