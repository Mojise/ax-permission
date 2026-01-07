package com.ax.library.ax_permission.ui

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentAxPermissionCommonDialogBinding
import com.ax.library.ax_permission.util.dp

/**
 * 영구 거부된 권한에 대해 앱 설정으로 이동을 안내하는 바텀시트
 *
 * Fragment Result API를 사용하여 버튼 클릭 이벤트를 전달합니다.
 * 이 방식은 Configuration change에도 안전합니다.
 *
 * 사용자는 다음 방법으로 바텀시트를 닫을 수 있습니다:
 * - Back button
 * - Outside touch
 * - Drag down
 * - 취소 버튼 클릭
 * - 앱 설정 버튼 클릭 (설정 화면으로 이동, 바텀시트는 유지)
 */
internal class PermissionPermanentlyDeniedBottomSheet : FloatingBottomSheetDialogFragment<FragmentAxPermissionCommonDialogBinding>() {

    override val layoutResId: Int = R.layout.fragment_ax_permission_common_dialog

    /**
     * 이 바텀시트가 담당하는 권한 아이템 ID
     */
    val permissionItemId: Int
        get() = arguments?.getInt(ARG_PERMISSION_ITEM_ID, -1) ?: -1

    /**
     * 이 바텀시트가 담당하는 권한 문자열 리스트
     */
    val permissions: List<String>
        get() = arguments?.getStringArrayList(ARG_PERMISSIONS) ?: emptyList()

    private val permissionName: String?
        get() = arguments?.getString(ARG_PERMISSION_NAME)

    /**
     * 버튼 클릭으로 인한 dismiss인지 구분하기 위한 플래그
     * true면 버튼 클릭으로 dismiss, false면 back/outside/drag로 dismiss
     */
    private var isDismissedByButton = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupContent()
        setupButtons()
    }

    /**
     * 바텀시트 컨텐츠 설정
     */
    private fun setupContent() {
        val name = permissionName ?: ""
        val goToSettingsButtonText = getString(R.string.ax_permission_permanently_denied_dialog_positive_button)

        with(binding) {
            // 제목
            tvTitle.text = getString(R.string.ax_permission_permanently_denied_dialog_title)

            // 메인 설명 (검정 글씨)
            tvDescription.text = getString(
                R.string.ax_permission_permanently_denied_dialog_message_format,
                name
            )
            tvDescription.setTextColor(requireContext().getColor(R.color.ax_permission_text_color_dark))

            // 가이드 텍스트 (bullet + bold)
            tvDescriptionGuide.visibility = android.view.View.VISIBLE
            tvDescriptionGuide.text = buildGuideText(name, goToSettingsButtonText)
        }
    }

    /**
     * 가이드 텍스트 생성 (bullet + bold 스타일 적용)
     */
    private fun buildGuideText(permissionName: String, goToSettingsButtonText: String): SpannableStringBuilder {
        val guide1 = getString(R.string.ax_permission_permanently_denied_dialog_guide_1)
        val guide2 = getString(R.string.ax_permission_permanently_denied_dialog_guide_2)
        val guide3 = getString(R.string.ax_permission_permanently_denied_dialog_guide_3_format, permissionName)

        // 각 줄별 bold 처리할 텍스트 (유니코드 따옴표 "" 사용)
        val boldTextsForGuide1 = listOf(goToSettingsButtonText)
        val boldTextsForGuide2 = listOf("권한")
        val boldTextsForGuide3 = listOf(""""$permissionName"""", """"허용"""")

        return SpannableStringBuilder().apply {
            appendLineWithBulletAndBold(guide1, boldTextsForGuide1)
            append("\n")
            appendLineWithBulletAndBold(guide2, boldTextsForGuide2)
            append("\n")
            appendLineWithBulletAndBold(guide3, boldTextsForGuide3)
        }
    }

    /**
     * 한 줄에 bullet과 bold 스타일을 적용하여 추가
     */
    private fun SpannableStringBuilder.appendLineWithBulletAndBold(
        text: String,
        boldTexts: List<String>
    ) {
        val lineStart = length
        append(text)

        // bullet 적용
        setSpan(
            BulletSpan(
                8.dp,
                requireContext().getColor(R.color.ax_permission_text_color_light)
            ),
            lineStart,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // bold 적용 (이 줄 범위 내에서만 검색)
        boldTexts.forEach { boldText ->
            val boldStart = indexOf(boldText, lineStart)
            if (boldStart >= lineStart) {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    boldStart,
                    boldStart + boldText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    /**
     * 버튼 설정
     */
    private fun setupButtons() {
        with(binding) {
            btnSecondary.text = getString(R.string.ax_permission_permanently_denied_dialog_negative_button)
            btnPrimary.text = getString(R.string.ax_permission_permanently_denied_dialog_positive_button)

            btnSecondary.setOnClickListener {
                // 버튼 클릭으로 인한 dismiss임을 표시
                isDismissedByButton = true

                // Fragment Result API로 결과 전달
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(RESULT_ACTION to ACTION_NEGATIVE)
                )
                dismiss()
            }

            btnPrimary.setOnClickListener {
                // 버튼 클릭으로 인한 dismiss임을 표시
                isDismissedByButton = true

                // 설정 화면으로 이동 - 바텀시트는 dismiss하지 않고 유지
                // 설정에서 돌아온 후 권한이 허용되면 Activity에서 dismiss 호출
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(RESULT_ACTION to ACTION_POSITIVE)
                )
            }
        }
    }

    /**
     * 바텀시트가 취소될 때 호출 (back button, outside touch, drag down)
     *
     * 버튼 클릭으로 인한 dismiss가 아닌 경우에만 워크플로우 종료 이벤트 전달
     */
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        if (!isDismissedByButton) {
            // Back/outside/drag로 dismiss된 경우 - negative 버튼과 동일하게 워크플로우 종료
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_ACTION to ACTION_NEGATIVE)
            )
        }
    }

    companion object {

        internal const val TAG = "PermissionPermanentlyDeniedBottomSheet"

        /** Fragment Result API request key */
        const val REQUEST_KEY = "permanently_denied_dialog_result"

        /** Result bundle key for action */
        const val RESULT_ACTION = "action"

        /** Action: positive button clicked (go to settings) */
        const val ACTION_POSITIVE = "positive"

        /** Action: negative button clicked (cancel) */
        const val ACTION_NEGATIVE = "negative"

        private const val ARG_PERMISSION_ITEM_ID = "permission_item_id"
        private const val ARG_PERMISSIONS = "permissions"
        private const val ARG_PERMISSION_NAME = "permission_name"

        fun show(
            fragmentManager: FragmentManager,
            permissionItemId: Int,
            permissions: List<String>,
            permissionName: String,
        ) {
            PermissionPermanentlyDeniedBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PERMISSION_ITEM_ID, permissionItemId)
                    putStringArrayList(ARG_PERMISSIONS, ArrayList(permissions))
                    putString(ARG_PERMISSION_NAME, permissionName)
                }
                show(fragmentManager, TAG)
            }
        }
    }
}
