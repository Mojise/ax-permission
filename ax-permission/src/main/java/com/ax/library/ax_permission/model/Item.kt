package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
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

    data class PermissionItem constructor(
        override val id: Int,
        val permission: Permission,
        @DrawableRes
        val iconDrawableResId: Int = 0, // 0 = 아이콘 없음
        val name: String,
        val description: String,
        val isRequired: Boolean,
        val isGranted: Boolean,
    ) : Item, Serializable {

        val isOptional: Boolean
            get() = isRequired.not()

        val isNotGranted: Boolean
            get() = isGranted.not()

        val hasIcon: Boolean
            get() = iconDrawableResId != 0
    }

    companion object {

        private const val NO_ID = -1

        val DiffItemCallback = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
        }
    }
}