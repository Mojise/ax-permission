package com.ax.library.ax_permission.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentPermissionBottomSheetBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.permission.PermissionRequestHelper
import com.ax.library.ax_permission.util.DrawableUtil
import com.ax.library.ax_permission.util.disableUserInputAndTouch
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * # 특별 권한 요청용 바텀시트 프래그먼트
 */
internal class SpecialPermissionBottomSheetFragment : FloatingBottomSheetDialogFragment<FragmentPermissionBottomSheetBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPermissionBottomSheetBinding {
        return FragmentPermissionBottomSheetBinding.inflate(inflater, container, false)
    }

    private val permissionIdsFromBundle: List<Int> by lazy { arguments?.getIntArray(ARG_PERMISSION_IDS)?.toList() ?: emptyList() }

    private val activityViewModel: PermissionViewModel by activityViewModels()
    private lateinit var adapter: ViewPagerAdapter

    /**
     * 해당 바텀시트가 생성될 때 전달 받은 특별 권한 목록([permissionIdsFromBundle])이 모두 허용되었는지 여부.
     *
     * - 바텀시트가 dismiss 될 때, 이 값이 false이면 워크플로우를 중단(종료)시킨다.
     */
    private var areTargetPermissionsAllGranted: Boolean = false

    /**
     * 바텀시트에 표시할 권한 목록
     */
    private val permissionItems: List<Item.PermissionItem>
        get() = activityViewModel.permissionItems.value
            .filter { it.id in permissionIdsFromBundle }

    private var currentViewPagerIndex: Int
        get() = binding.viewPager.currentItem
        set(value) { binding.viewPager.currentItem = value }

    // 런타임 권한 요청을 위한 ActivityResultLauncher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // 결과는 onResume에서 처리됨 (checkPermissionAndMoveNextOrDismissOrNothing)

        val currentState = activityViewModel.workflowState.value
        if (currentState is PermissionWorkflowState.Running) {
            val currId = currentState.currentId
            val lastId = permissionIdsFromBundle.lastOrNull()
            val currPermissionItem = activityViewModel.permissionItems.value
                .find { it.id == currId }
                as? Item.PermissionItem.Special

            if (currPermissionItem != null) {
                // Extract the first Special permission from the permissions list
                val isGranted = PermissionChecker
                    .checkSpecialPermission(requireActivity(), currPermissionItem.action)
                    .isGranted

                Log.d(TAG, "permissionLauncher :: currId=$currId, currPermission=${currPermissionItem.action}, isGranted=${isGranted}")

                if (isGranted) {
                    // 권한이 허용된 경우 상태 업데이트 및 다음으로 진행
                    activityViewModel.updatePermissionGrantedState(currPermissionItem, true)

                    lifecycleScope.launch {
                        delay(300) // 권한 설정 Intent 화면에서 복귀하는 시간을 약간 기다림
                        activityViewModel.proceedToNextPermissionInWorkflow()

                        if (currId != lastId) {
                            // 다음 권한 페이지로 이동
                            currentViewPagerIndex += 1 // TODO: safe하게 변경
                        } else {
                            // 모든 권한이 처리된 경우 바텀시트 닫기
                            areTargetPermissionsAllGranted = true
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewPagerAndData()
    }

    override fun onResume() {
        super.onResume()

        //checkPermissionAndMoveNextOrDismissOrNothing()
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)

        //Log.e(TAG, "onDismiss() :: areTargetPermissionsAllGranted=$areTargetPermissionsAllGranted, isChangingConfigurations=${requireActivity().isChangingConfigurations}")

        if (areTargetPermissionsAllGranted.not() &&
            requireActivity().isChangingConfigurations.not()
        ) {
            activityViewModel.finishWorkflow()
        }
    }

    private fun initView() {
        with (binding) {

            // btnNegative 리플 효과 설정
            btnNegative.foreground = DrawableUtil.createRippleDrawable(
                cornerRadius = AxPermission.configurations.cornerRadius,
                rippleColor = requireContext().getColor(R.color.ax_permission_ripple_color),
            )

            btnPositive.setOnClickListener {
                // Extract first Special permission from the permission group
                val specialPermission = permissionItems[currentViewPagerIndex] as Item.PermissionItem.Special

                requestPermission(specialPermission.action)
            }
            btnNegative.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initViewPagerAndData() {
        with (binding) {
            adapter = ViewPagerAdapter(this@SpecialPermissionBottomSheetFragment, permissionItems)
            viewPager.adapter = adapter
            viewPager.disableUserInputAndTouch()

            //Log.w(TAG, "initViewPagerAndData() called with: initialViewPagerItemIndex = $initialViewPagerItemIndex")

            val currentState = activityViewModel.workflowState.value
            if (currentState is PermissionWorkflowState.Running) {
                // 워크플로우 진행 중인 경우: 현재 요청 중인 권한으로 이동
                val initialViewPagerItemIndex = permissionItems
                    .indexOfFirst { it.id == currentState.currentId }
                viewPager.setCurrentItem(initialViewPagerItemIndex, false)
            }
        }
    }

    private fun requestPermission(action: String) {
        // 특별 권한: Settings로 이동
        PermissionRequestHelper.requestSpecialPermission(
            context = requireContext(),
            launcher = permissionLauncher,
            action = action,
        )
    }

    /**
     * ViewPager's FragmentStateAdapter
     */
    internal class ViewPagerAdapter constructor(
        fragment: Fragment,
        private val permissions: List<Item.PermissionItem>,
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = permissions.size

        override fun createFragment(position: Int): Fragment {
            val permission = permissions[position]
            return SpecialPermissionBottomSheetContentFragment.newInstance(permission.id)
        }
    }

    companion object {

        internal const val TAG = "PermissionBottomSheetFragment"

        internal const val ARG_TARGET_PERMISSION_ITEM_ID = "target_permission_item_id"
        private const val ARG_PERMISSION_IDS = "permission_ids"

        internal fun show(
            fragmentManager: FragmentManager,
            permissionIds: List<Int>,
        ): SpecialPermissionBottomSheetFragment {
            Log.d(TAG, "show() called with :: permissionIds=$permissionIds")
            return SpecialPermissionBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putIntArray(ARG_PERMISSION_IDS, permissionIds.toIntArray())
                }
                show(fragmentManager, TAG)
            }
        }
    }
}