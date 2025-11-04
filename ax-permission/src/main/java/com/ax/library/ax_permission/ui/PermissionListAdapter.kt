package com.ax.library.ax_permission.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.ItemAxPermissionBinding
import com.ax.library.ax_permission.databinding.ItemAxPermissionFooterBinding
import com.ax.library.ax_permission.databinding.ItemAxPermissionHeaderBinding
import com.ax.library.ax_permission.model.Item


/**
 * 권한 목록 ListAdapter
 *
 * - Header: 권한 목록의 헤더 (예: "필수 권한" or "선택 권한")
 * - Footer: 권한 목록의 푸터 (예: "위 필수 권한은 모두 허용해야 앱을 사용할 수 있습니다", "선택 권한은 선택적으로 허용할 수 있습니다.")
 * - Divider: 권한 목록의 구분선
 * - Permission: 권한 항목
 */
internal class PermissionListAdapter(
    private val onPermissionItemClicked: (Item.PermissionItem) -> Unit,
) : ListAdapter<Item, RecyclerView.ViewHolder>(Item.DiffItemCallback) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Item.Header     -> R.layout.item_ax_permission_header
        is Item.Footer     -> R.layout.item_ax_permission_footer
        is Item.Divider    -> R.layout.item_ax_permission_divider
        is Item.PermissionItem -> R.layout.item_ax_permission
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_ax_permission_header  -> createHeaderViewHolder(parent)
        R.layout.item_ax_permission_footer  -> createFooterViewHolder(parent)
        R.layout.item_ax_permission_divider -> createDividerViewHolder(parent)
        R.layout.item_ax_permission         -> createPermissionViewHolder(parent, onPermissionItemClicked)
        else -> throw IllegalArgumentException("Unknown view type: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder     -> holder.bind(getItem(position) as Item.Header)
            is FooterViewHolder     -> holder.bind(getItem(position) as Item.Footer)
            is DividerViewHolder    -> { /* No binding needed for divider */ }
            is PermissionViewHolder -> holder.bind(getItem(position) as Item.PermissionItem)
        }
    }
}

/**
 * Header ViewHolder
 */
private class HeaderViewHolder(
    private val binding: ItemAxPermissionHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(header: Item.Header) {
        binding.header = header
        binding.executePendingBindings()
    }
}

/**
 * Footer ViewHolder
 */
private class FooterViewHolder(
    private val binding: ItemAxPermissionFooterBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(footer: Item.Footer) {
        binding.footer = footer
        binding.executePendingBindings()
    }
}

/**
 * Divider ViewHolder
 */
private class DividerViewHolder(
    private val binding: ItemAxPermissionFooterBinding,
) : RecyclerView.ViewHolder(binding.root)

/**
 * Permission ViewHolder
 */
private class PermissionViewHolder(
    private val binding: ItemAxPermissionBinding,
    private val onPermissionItemClicked: (Item.PermissionItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val permission = binding.permission ?: return@setOnClickListener
            onPermissionItemClicked(permission)
        }
    }

    fun bind(permission: Item.PermissionItem) {
        binding.permission = permission
        binding.ivPermissionIcon.setImageResource(permission.iconDrawableResId)
        binding.root.isSelected = permission.isGranted
        (binding.root as? ViewGroup)?.forEach { it.isSelected = permission.isGranted }
        binding.executePendingBindings()
    }
}

private fun createHeaderViewHolder(parent: ViewGroup) = HeaderViewHolder(
    ItemAxPermissionHeaderBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
)

private fun createFooterViewHolder(parent: ViewGroup) = FooterViewHolder(
    ItemAxPermissionFooterBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
)

private fun createDividerViewHolder(parent: ViewGroup) = DividerViewHolder(
    ItemAxPermissionFooterBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
)

private fun createPermissionViewHolder(
    parent: ViewGroup,
    onPermissionItemClicked: (Item.PermissionItem) -> Unit
) = PermissionViewHolder(
    ItemAxPermissionBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ),
    onPermissionItemClicked,
)