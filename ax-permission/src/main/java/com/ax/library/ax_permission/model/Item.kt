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

        @get:DrawableRes
        val iconResId: Int
        @get:StringRes
        val titleResId: Int
        @get:StringRes
        val descriptionResId: Int

        val isRequired: Boolean
        val isGranted: Boolean
        val isHighlights: Boolean

        val isOptional: Boolean
            get() = isRequired.not()

        val isNotGranted: Boolean
            get() = isGranted.not()

        data class Special(
            override val id: Int,

            val permission: Permission.Special,

            @field:DrawableRes
            override val iconResId: Int,
            @field:StringRes
            override val titleResId: Int,
            @field:StringRes
            override val descriptionResId: Int,

            override val isRequired: Boolean,
            override val isGranted: Boolean,
            override val isHighlights: Boolean,
        ) : PermissionItem

        data class Runtime(
            override val id: Int,

            val permissions: List<Permission.Runtime>,

            @field:DrawableRes
            override val iconResId: Int,
            @field:StringRes
            override val titleResId: Int,
            @field:StringRes
            override val descriptionResId: Int,

            override val isRequired: Boolean,
            override val isGranted: Boolean,
            override val isHighlights: Boolean,
        ) : PermissionItem
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