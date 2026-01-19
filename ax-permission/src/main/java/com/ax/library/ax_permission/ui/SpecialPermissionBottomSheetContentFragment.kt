package com.ax.library.ax_permission.ui

import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.databinding.FragmentAxPermissionSpecialBottomSheetContentBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.util.DrawableUtil
import com.ax.library.ax_permission.util.dp
import com.ax.library.ax_permission.util.repeatOnStarted

internal class SpecialPermissionBottomSheetContentFragment : Fragment() {

    private lateinit var binding: FragmentAxPermissionSpecialBottomSheetContentBinding
    private val activityViewModel: PermissionViewModel by activityViewModels()
    private var permissionItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionItemId = arguments?.getInt(ARG_PERMISSION_ITEM_ID, -1)
            ?.takeIf { it >= 0 }

        collectUiStates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAxPermissionSpecialBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        // Fragment가 화면에 표시될 때 NestedScrollView 레이아웃 갱신
        binding.nsvScrollContainer.post {
            binding.nsvScrollContainer.requestLayout()
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        // ViewPager2에서 페이지가 보이게 될 때 NestedScrollView 레이아웃 갱신
        if (menuVisible && ::binding.isInitialized) {
            binding.nsvScrollContainer.post {
                binding.nsvScrollContainer.requestLayout()
            }
        }
    }

    private fun initView() {
        with (binding) {
            ivIcon.setPadding(AxPermission.configurations.iconPaddings + 4.dp)
            ivIcon.background = DrawableUtil.createGradientDrawable(
                cornerRadius = 100f.dp,
                //backgroundColor = requireContext().getColor(R.color.ax_permission_item_icon_background_color),
                backgroundColor = requireContext().getColor(AxPermission.configurations.primaryColorResId),
                backgroundSelectedColor = requireContext().getColor(AxPermission.configurations.primaryColorResId),
            )

            // NestedScrollView의 최대 높이 설정 (화면 높이의 35%)
            val displayMetrics = resources.displayMetrics
            val maxScrollHeight = (displayMetrics.heightPixels * 0.35f).toInt()
            val scrollViewParams = nsvScrollContainer.layoutParams as ConstraintLayout.LayoutParams
            scrollViewParams.matchConstraintMaxHeight = maxScrollHeight
            nsvScrollContainer.layoutParams = scrollViewParams
        }
    }

    private fun collectUiStates() {
        val appName = getString(AxPermission.configurations.appNameResId)
        val moveToSettingsButtonText = getString(R.string.ax_permission_bottom_sheet_positive_button_text_move_to_settings)

        repeatOnStarted {
            activityViewModel.permissionItems.collect { items ->

                val permissionItem = items.find { it.id == permissionItemId }
                    ?: return@collect

                binding.ivIcon.setImageResource(permissionItem.iconResId)
                binding.ivIcon.isSelected = permissionItem.isGranted
                binding.tvTitle.text = getString(permissionItem.titleResId)
                binding.tvDescription.text = getString(permissionItem.descriptionResId)

                // 접근성 권한인지 확인
                val isAccessibilityPermission = permissionItem is Item.PermissionItem.Special &&
                        permissionItem.action == Settings.ACTION_ACCESSIBILITY_SETTINGS

                binding.tvGuide.text = if (isAccessibilityPermission) {
                    buildAccessibilityGuideText(appName, moveToSettingsButtonText, binding.tvGuide.paint)
                } else {
                    buildDefaultGuideText(appName, moveToSettingsButtonText)
                }
            }
        }
    }

    /**
     * 접근성 권한용 가이드 텍스트 생성 (번호 형식 + 들여쓰기)
     */
    private fun buildAccessibilityGuideText(
        appName: String,
        buttonText: String,
        paint: android.graphics.Paint
    ): SpannableStringBuilder {
        return SpannableStringBuilder().also { ssb ->
            val circledNumbers = listOf("①", "②", "③", "④")
            val guideTexts = listOf(
                getString(R.string.ax_permission_bottom_sheet_accessibility_guide_1_format, buttonText),
                getString(R.string.ax_permission_bottom_sheet_accessibility_guide_2),
                getString(R.string.ax_permission_bottom_sheet_accessibility_guide_3_format, appName),
                getString(R.string.ax_permission_bottom_sheet_accessibility_guide_4)
            )
            val boldTexts = listOf(
                "[$buttonText]",
                getString(R.string.ax_permission_bottom_sheet_accessibility_guide_2).substringAfter("[").substringBefore("]").let { "[$it]" },
                "[$appName]",
                "[사용 안 함]",
                "[Off]",
                "[허용]",
                "[Allow]"
            )

            // "① " 텍스트의 실제 너비를 측정하여 들여쓰기 크기로 사용
            val leadingMargin = paint.measureText("① ").toInt()

            guideTexts.forEachIndexed { idx, text ->
                val start = ssb.length
                ssb.append("${circledNumbers[idx]} $text")
                if (idx < guideTexts.size - 1) {
                    ssb.append("\n")
                }

                // 번호 다음 줄 들여쓰기 적용
                ssb.setSpan(
                    LeadingMarginSpan.Standard(0, leadingMargin),
                    start,
                    ssb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }

            // Bold 처리
            boldTexts.forEach { text ->
                var start = ssb.indexOf(text)
                while (start >= 0) {
                    ssb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        start + text.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    start = ssb.indexOf(text, start + text.length)
                }
            }
        }
    }

    /**
     * 기본 특별 권한용 가이드 텍스트 생성 (불릿 형식)
     */
    private fun buildDefaultGuideText(appName: String, buttonText: String): SpannableStringBuilder {
        return SpannableStringBuilder().also { ssb ->
            val bulletTexts = listOf(
                getString(R.string.ax_permission_bottom_sheet_settings_guide_1_format, buttonText),
                getString(R.string.ax_permission_bottom_sheet_settings_guide_2_format, appName)
            )
            val boldTexts = listOf(
                buttonText,
                "[$appName]",
                getString(R.string.ax_permission_bottom_sheet_settings_guide_bold_1),
            )
            bulletTexts.forEachIndexed { idx, text ->
                val start = ssb.length
                ssb.append(text)
                if (idx < bulletTexts.size - 1) {
                    ssb.append("\n")
                }

                ssb.setSpan(
                    BulletSpan(
                        8.dp,
                        requireContext().getColor(R.color.ax_permission_text_color_dark)
                    ),
                    start,
                    ssb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
            boldTexts.forEach { text ->
                val start = ssb.indexOf(text)
                if (start >= 0) {
                    ssb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        start + text.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
            }
        }
    }

    companion object {

        private const val ARG_PERMISSION_ITEM_ID = "permission_item_id"

        fun newInstance(
            permissionItemId: Int
        ): SpecialPermissionBottomSheetContentFragment {
            val fragment = SpecialPermissionBottomSheetContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PERMISSION_ITEM_ID, permissionItemId)
                }
            }
            return fragment
        }
    }
}