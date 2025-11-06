package com.ax.library.ax_permission.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.ItemAxPermissionBinding
import com.ax.library.ax_permission.databinding.ItemAxPermissionEmptySpaceFooterBinding
import com.ax.library.ax_permission.databinding.ItemAxPermissionFooterBinding
import com.ax.library.ax_permission.databinding.ItemAxPermissionHeaderBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.util.DrawableUtil
import com.ax.library.ax_permission.util.dp


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
        is Item.Header              -> R.layout.item_ax_permission_header
        is Item.Footer              -> R.layout.item_ax_permission_footer
        is Item.Divider             -> R.layout.item_ax_permission_divider
        is Item.PermissionItem      -> R.layout.item_ax_permission
        is Item.EmptySpaceFooter    -> R.layout.item_ax_permission_empty_space_footer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_ax_permission_header              -> createHeaderViewHolder(parent)
        R.layout.item_ax_permission_footer              -> createFooterViewHolder(parent)
        R.layout.item_ax_permission_divider             -> createDividerViewHolder(parent)
        R.layout.item_ax_permission                     -> createPermissionViewHolder(parent, onPermissionItemClicked)
        R.layout.item_ax_permission_empty_space_footer  -> createEmptySpaceFooterViewHolder(parent)
        else -> throw IllegalArgumentException("Unknown view type: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder           -> holder.bind(getItem(position) as Item.Header)
            is FooterViewHolder           -> holder.bind(getItem(position) as Item.Footer)
            is DividerViewHolder          -> { /* No binding needed for divider */ }
            is PermissionViewHolder       -> holder.bind(getItem(position) as Item.PermissionItem)
            is EmptySpaceFooterViewHolder -> { /* No binding needed for empty space footer */ }
        }
    }
}

/**
 * ### Header ViewHolder
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
 * ### Footer ViewHolder
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
 * ### Divider ViewHolder
 */
private class DividerViewHolder(
    private val binding: ItemAxPermissionFooterBinding,
) : RecyclerView.ViewHolder(binding.root)

/**
 * ### Permission ViewHolder
 */
private class PermissionViewHolder(
    private val binding: ItemAxPermissionBinding,
    private val onPermissionItemClicked: (Item.PermissionItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val permissionItem = binding.permissionItem ?: return@setOnClickListener
            onPermissionItemClicked(permissionItem)
        }

        val context = itemView.context

        // 초기 설정
        AxPermission.configurations.let {
            // 하이라이팅 스크림 배경색 설정
            binding.viewHighlightedScrim.backgroundTintList = context.getColorStateList(it.primaryColorResId)
            // 권한 아이콘 패딩 설정
            binding.ivPermissionIcon.setPadding(it.iconPaddings)
            binding.ivPermissionIcon.background = DrawableUtil.createGradientDrawable(
                cornerRadius = 100f.dp,
                backgroundColor = context.getColor(R.color.ax_permission_item_icon_background_color),
                backgroundSelectedColor = context.getColor(it.primaryColorResId),
            )
        }
    }

    fun bind(permissionItem: Item.PermissionItem) {
        binding.permissionItem = permissionItem
        binding.isHighlighted = permissionItem.isHighlights
        binding.ivPermissionIcon.setImageResource(permissionItem.permission.iconResId)
        binding.root.isSelected = permissionItem.isGranted
        (binding.root as? ViewGroup)?.forEach { it.isSelected = permissionItem.isGranted }
        binding.executePendingBindings()
    }
}
/**
 * ### Empty Space Footer ViewHolder
 */
private class EmptySpaceFooterViewHolder(
    private val binding: ItemAxPermissionEmptySpaceFooterBinding,
) : RecyclerView.ViewHolder(binding.root)

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

private fun createEmptySpaceFooterViewHolder(parent: ViewGroup) = EmptySpaceFooterViewHolder(
    ItemAxPermissionEmptySpaceFooterBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
)