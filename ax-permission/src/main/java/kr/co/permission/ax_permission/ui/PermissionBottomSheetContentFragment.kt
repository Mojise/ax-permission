package kr.co.permission.ax_permission.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.FragmentPermissionBottomSheetContentBinding
import kr.co.permission.ax_permission.util.dp
import kr.co.permission.ax_permission.util.repeatOnStarted

internal class PermissionBottomSheetContentFragment : Fragment() {

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
            // Do nothing.
        }
    }

    private fun collectUiStates() {
        repeatOnStarted {
            activityViewModel.permissionItems.collect { items ->

                val permission = items.find { it.id == permissionItemId }
                    ?: return@collect

                binding.ivIcon.setImageResource(permission.iconDrawableResId)
                binding.tvTitle.text = permission.name
                binding.tvDescription.text = permission.description

                binding.tvGuide.text = SpannableStringBuilder().also { ssb ->
//                    val items = listOf(
//                        "권한 허용하기 버튼을 누릅니다.",
//                        "설정 화면에서 [숨톡] → 알림 접근 스위치를 켜주세요."
//                    )
                    val bulletTexts = if (permission.type.isAction) {
                        listOf(
                            getString(R.string.ax_permission_guide_action_type_1),
                            getString(R.string.ax_permission_guide_action_type_2)
                        )
                    } else {
                        listOf(
                            getString(R.string.ax_permission_guide_normal_type_1),
                            getString(R.string.ax_permission_guide_normal_type_2)
                        )
                    }
                    val boldTexts = if (permission.type.isAction) {
                        listOf(
                            "'권한 설정으로 이동'",
                            "'숨톡'",
                            "스위치를 켜서",
                        )
                    } else {
                        listOf(
                            "'권한 허용하기'",
                            "'허용' 버튼을 눌러",
                        )
                    }
                    bulletTexts.forEachIndexed { idx, text ->
                        val start = ssb.length
                        ssb.append(text).append("\n")

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
        ): PermissionBottomSheetContentFragment {
            val fragment = PermissionBottomSheetContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PERMISSION_ITEM_ID, permissionItemId)
                }
            }
            return fragment
        }
    }
}