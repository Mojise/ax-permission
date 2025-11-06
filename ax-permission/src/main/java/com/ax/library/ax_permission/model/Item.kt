package com.ax.library.ax_permission.model

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
        val isRequired: Boolean,
        val isGranted: Boolean,
        val isHighlights: Boolean,
    ) : Item, Serializable {

        val isOptional: Boolean
            get() = isRequired.not()

        val isNotGranted: Boolean
            get() = isGranted.not()
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