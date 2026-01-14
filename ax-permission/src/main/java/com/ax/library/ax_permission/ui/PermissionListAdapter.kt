package com.ax.library.ax_permission.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.ItemAxPermissionBinding
import com.ax.library.ax_permission.databinding.ItemAxPermissionDividerBinding
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
        binding.tvHeaderText.text = header.text
    }
}

/**
 * ### Footer ViewHolder
 */
private class FooterViewHolder(
    private val binding: ItemAxPermissionFooterBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(footer: Item.Footer) {
        binding.tvFooterText.text = footer.text
    }
}

/**
 * ### Divider ViewHolder
 */
private class DividerViewHolder(
    private val binding: ItemAxPermissionDividerBinding,
) : RecyclerView.ViewHolder(binding.root)

/**
 * ### Permission ViewHolder
 */
private class PermissionViewHolder(
    private val binding: ItemAxPermissionBinding,
    private val onPermissionItemClicked: (Item.PermissionItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var currentPermissionItem: Item.PermissionItem? = null

    init {
        binding.root.setOnClickListener {
            currentPermissionItem?.let { onPermissionItemClicked(it) }
        }

        val context = itemView.context

        // 초기 설정
        AxPermission.configurations.let {
            // 하이라이팅 스크림 배경색 설정 (동적 corner radius 적용)
            binding.viewHighlightedScrim.background = DrawableUtil.createGradientDrawable(
                cornerRadius = AxPermission.configurations.cornerRadius,
                backgroundColor = context.getColor(R.color.ax_permission_item_highlight_color),
            )
            // 권한 아이템 배경 설정
            binding.root.background = DrawableUtil.createGradientDrawable(
                cornerRadius = AxPermission.configurations.cornerRadius,
                backgroundColor = context.getColor(R.color.ax_permission_item_background_color),
                backgroundSelectedColor = context.getColor(it.grantedItemBackgroundColorResId),
            )
            // 권한 아이템 포그라운드 리플 설정
            binding.root.foreground = DrawableUtil.createRippleDrawable(
                cornerRadius = AxPermission.configurations.cornerRadius,
                rippleColor = context.getColor(R.color.ax_permission_ripple_color),
            )
            // 권한 아이콘 패딩 설정
            binding.ivPermissionIcon.setPadding(it.iconPaddings)
            binding.ivPermissionIcon.background = DrawableUtil.createGradientDrawable(
                cornerRadius = 100f.dp,
                backgroundColor = context.getColor(R.color.ax_permission_item_icon_background_color),
                backgroundSelectedColor = context.getColor(it.primaryColorResId),
            )
            // 텍스트 색상 설정 (허용된 상태에서는 밝은 배경이므로 어두운 텍스트 사용)
            binding.tvPermissionName.setTextColor(ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    context.getColor(R.color.ax_permission_text_color_dark_day),
                    context.getColor(R.color.ax_permission_text_color_dark)
                )
            ))
            binding.tvPermissionDescription.setTextColor(ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    0xFF666666.toInt(), // 허용된 상태에서는 #666666
                    context.getColor(R.color.ax_permission_text_color_light)
                )
            ))
            // 뱃지 텍스트 색상 설정 (다크모드에서는 #d2d2d2)
            binding.tvPermissionGrantedBadge.setTextColor(ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf()
                ),
                intArrayOf(
                    context.getColor(R.color.ax_permission_text_color_dark_night),
                    context.getColor(R.color.ax_permission_white)
                )
            ))
            // 뱃지 배경색 설정 (글로벌 config의 primaryColorResId 적용)
            binding.tvPermissionGrantedBadge.background = DrawableUtil.createGradientDrawable(
                cornerRadius = 100f.dp,
                backgroundColor = context.getColor(it.primaryColorResId),
            )
        }
    }

    fun bind(permissionItem: Item.PermissionItem) {
        currentPermissionItem = permissionItem

        // View 바인딩 (Data Binding 표현식 대체)
        binding.viewHighlightedScrim.visibility = if (permissionItem.isHighlights) View.VISIBLE else View.GONE
        binding.tvPermissionName.setText(permissionItem.titleResId)
        binding.tvPermissionDescription.setText(permissionItem.descriptionResId)
        binding.tvPermissionGrantedBadge.visibility = if (permissionItem.isGranted) View.VISIBLE else View.GONE

        binding.root.isSelected = permissionItem.isGranted
        (binding.root as? ViewGroup)?.forEach { it.isSelected = permissionItem.isGranted }

        binding.ivPermissionIcon.setImageResource(permissionItem.iconResId)
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
    ItemAxPermissionDividerBinding.inflate(
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
