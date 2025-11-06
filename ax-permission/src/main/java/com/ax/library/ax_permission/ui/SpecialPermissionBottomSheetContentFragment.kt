package com.ax.library.ax_permission.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.databinding.FragmentPermissionBottomSheetContentBinding
import com.ax.library.ax_permission.util.DrawableUtil
import com.ax.library.ax_permission.util.dp
import com.ax.library.ax_permission.util.repeatOnStarted

internal class SpecialPermissionBottomSheetContentFragment : Fragment() {

    private lateinit var binding: FragmentPermissionBottomSheetContentBinding
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
        binding = FragmentPermissionBottomSheetContentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with (binding) {
            ivIcon.setPadding(AxPermission.configurations.iconPaddings + 4.dp)
            ivIcon.background = DrawableUtil.createGradientDrawable(
                cornerRadius = 100f.dp,
                backgroundColor = requireContext().getColor(R.color.ax_permission_item_icon_background_color),
                backgroundSelectedColor = requireContext().getColor(AxPermission.configurations.primaryColorResId),
            )
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

                binding.tvGuide.text = SpannableStringBuilder().also { ssb ->
                    val bulletTexts = listOf(
                        getString(R.string.ax_permission_bottom_sheet_settings_guide_1_format, moveToSettingsButtonText),
                        getString(R.string.ax_permission_bottom_sheet_settings_guide_2_format, appName)
                    )
                    val boldTexts = listOf(
                        moveToSettingsButtonText,
                        "“$appName”",
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