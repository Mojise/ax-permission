package com.ax.library.ax_permission.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentAxPermissionCommonDialogBinding

/**
 * 영구 거부된 권한에 대해 앱 설정으로 이동을 안내하는 바텀시트
 *
 * Fragment Result API를 사용하여 버튼 클릭 이벤트를 전달합니다.
 * 이 방식은 Configuration change에도 안전합니다.
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바텀시트 외부 터치로 닫히지 않도록 설정
        isCancelable = false
    }

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

        with(binding) {
            tvTitle.text = getString(R.string.ax_permission_permanently_denied_dialog_title)
            tvDescription.text = getString(
                R.string.ax_permission_permanently_denied_dialog_message_format,
                name
            )
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
                // Fragment Result API로 결과 전달
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(RESULT_ACTION to ACTION_NEGATIVE)
                )
                dismiss()
            }

            btnPrimary.setOnClickListener {
                // 설정 화면으로 이동 - 바텀시트는 dismiss하지 않고 유지
                // 설정에서 돌아온 후 권한이 허용되면 Activity에서 dismiss 호출
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(RESULT_ACTION to ACTION_POSITIVE)
                )
            }
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
