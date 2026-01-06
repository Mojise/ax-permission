package com.ax.library.ax_permission.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.DialogAxPermissionPermanentlyDeniedBinding
import com.ax.library.ax_permission.util.dp

/**
 * 영구 거부된 권한에 대해 앱 설정으로 이동을 안내하는 다이얼로그
 *
 * 캡처화면의 토스 스타일 다이얼로그 디자인을 따릅니다.
 *
 * Fragment Result API를 사용하여 버튼 클릭 이벤트를 전달합니다.
 * 이 방식은 Configuration change에도 안전합니다.
 */
internal class PermissionPermanentlyDeniedDialog : DialogFragment() {

    private var _binding: DialogAxPermissionPermanentlyDeniedBinding? = null
    private val binding get() = _binding!!

    /**
     * 이 다이얼로그가 담당하는 권한 아이템 ID
     */
    val permissionItemId: Int
        get() = arguments?.getInt(ARG_PERMISSION_ITEM_ID, -1) ?: -1

    /**
     * 이 다이얼로그가 담당하는 권한 문자열 리스트
     */
    val permissions: List<String>
        get() = arguments?.getStringArrayList(ARG_PERMISSIONS) ?: emptyList()

    private val permissionName: String?
        get() = arguments?.getString(ARG_PERMISSION_NAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 다이얼로그 외부 터치로 닫히지 않도록 설정
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            // 타이틀 바 제거 및 투명 배경 설정
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAxPermissionPermanentlyDeniedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialogBackground()
        setupContent()
        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 너비를 화면의 85%로 설정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 다이얼로그 배경 설정 (둥근 모서리)
     */
    private fun setupDialogBackground() {
        binding.root.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(requireContext().getColor(R.color.ax_permission_white))
            cornerRadius = 20f.dp
        }
    }

    /**
     * 다이얼로그 컨텐츠 설정
     */
    private fun setupContent() {
        val name = permissionName ?: ""

        binding.tvTitle.text = getString(R.string.ax_permission_permanently_denied_dialog_title)
        binding.tvDescription.text = getString(
            R.string.ax_permission_permanently_denied_dialog_message_format,
            name
        )
    }

    /**
     * 버튼 설정
     */
    private fun setupButtons() {
        binding.btnNegative.text = getString(R.string.ax_permission_permanently_denied_dialog_negative_button)
        binding.btnPositive.text = getString(R.string.ax_permission_permanently_denied_dialog_positive_button)

        binding.btnNegative.setOnClickListener {
            // Fragment Result API로 결과 전달
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_ACTION to ACTION_NEGATIVE)
            )
            dismiss()
        }

        binding.btnPositive.setOnClickListener {
            // 설정 화면으로 이동 - 다이얼로그는 dismiss하지 않고 유지
            // 설정에서 돌아온 후 권한이 허용되면 Activity에서 dismiss 호출
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_ACTION to ACTION_POSITIVE)
            )
        }
    }

    companion object {

        internal const val TAG = "PermissionPermanentlyDeniedDialog"

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
            PermissionPermanentlyDeniedDialog().apply {
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
