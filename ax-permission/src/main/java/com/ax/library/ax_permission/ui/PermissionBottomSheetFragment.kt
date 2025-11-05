package com.ax.library.ax_permission.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentPermissionBottomSheetBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.permission.PermissionRequestHelper
import com.ax.library.ax_permission.util.disableUserInputAndTouch

internal class PermissionBottomSheetFragment : FloatingBottomSheetDialogFragment<FragmentPermissionBottomSheetBinding>() {

    override val layoutResId: Int = R.layout.fragment_permission_bottom_sheet

    private val permissionIdsFromBundle: List<Int> by lazy { arguments?.getIntArray(ARG_PERMISSION_IDS)?.toList() ?: emptyList() }

    private val activityViewModel: PermissionViewModel by activityViewModels()
    private lateinit var adapter: ViewPagerAdapter

    /**
     * dismiss 시, 워크플로우를 종료할 지 여부
     */
    private var isCompleted: Boolean = false

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
            val currPermissionItem = activityViewModel.permissionItems.value.find { it.id == currId }
            if (currPermissionItem != null) {
                val isGranted = PermissionChecker.check(requireContext(), currPermissionItem.permission)

                Log.d(TAG, "permissionLauncher :: currId=$currId, currPermission=${currPermissionItem.permission}, isGranted=${isGranted}")

                if (isGranted) {
                    // 권한이 허용된 경우 상태 업데이트 및 다음으로 진행
                    activityViewModel.updatePermissionGrantedState(currPermissionItem.permission, true)
                    activityViewModel.proceedToNext()

                    if (currId != lastId) {
                        // 다음 권한 페이지로 이동
                        currentViewPagerIndex += 1
                    } else {
                        // 모든 권한이 처리된 경우 바텀시트 닫기
                        isCompleted = true
                        dismiss()
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

        if (isCompleted.not()) {
            // TODO: 화면 전환 등 Activity Configuration 변경으로 인해 바텀시트가 닫힌 경우 워크플로우를 종료하지 않도록 처리 필요
            activityViewModel.finishWorkflow()
        }
    }

    private fun checkPermissionAndMoveNextOrDismissOrNothing() {
//        val currentPermission = permissionItems.getOrNull(currentViewPagerIndex)
//            ?: return
//
//        val isGranted = PermissionChecker.check(requireContext(), currentPermission.permission)
//        activityViewModel.updatePermissionGrantedState(currentPermission.permission, isGranted)
//
//        if (isGranted) {
//            // 권한 허용됨: ViewPager 내에서 다음 Special 권한으로 이동 (워크플로우/레거시 모두 동일)
//            if (targetPermissionItemId == null) {
//                // 현재 아이템보다 뒤에 있는 권한 중, 첫 번째로 허용되지 않은 권한의 인덱스
//                val nextNotGrantedPermissionIndex = permissionItems
//                    .indexOfFirst(currentViewPagerIndex + 1, Item.PermissionItem::isNotGranted)
//                    .takeIf { it >= 0 }
//
//                val delay = 300L // Settings에서 돌아온 후 300ms 딜레이
//
//                Handler(Looper.getMainLooper()).postDelayed({
//                    if (nextNotGrantedPermissionIndex != null) {
//                        // 다음 Special 권한으로 이동
//                        binding.viewPager.currentItem = nextNotGrantedPermissionIndex
//                    } else {
//                        // ViewPager의 모든 Special 권한 처리 완료 → dismiss
//                        dismiss()
//                    }
//                }, delay)
//            } else {
//                // targetPermissionItemId가 지정된 경우 (개별 권한 요청)
//                dismiss()
//            }
//        } else {
//            // 권한이 허용되지 않은 경우: 현재 아이템 유지 (바텀시트 유지)
//            // Settings에서 돌아온 후에도 권한이 거부된 상태라면 사용자가 다시 시도하거나 취소 버튼을 눌러야 함
//        }
    }

    private fun initView() {
        with (binding) {
            btnPositive.setOnClickListener {
                val permission = permissionItems[currentViewPagerIndex]
                    .permission as Permission.Special
                requestPermission(permission)
            }
            btnNegative.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initViewPagerAndData() {
        with (binding) {
            adapter = ViewPagerAdapter(this@PermissionBottomSheetFragment, permissionItems)
            viewPager.adapter = adapter
            viewPager.disableUserInputAndTouch()

            //Log.w(TAG, "initViewPagerAndData() called with: initialViewPagerItemIndex = $initialViewPagerItemIndex")

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.btnPositive.text =
                        if (permissionItems[position].permission is Permission.Special) {
                            getString(R.string.ax_permission_bottom_sheet_positive_button_text_move_to_settings)
                        } else {
                            getString(R.string.ax_permission_bottom_sheet_positive_button_text_allow)
                        }
                }
            })

            val currentState = activityViewModel.workflowState.value
            if (currentState is PermissionWorkflowState.Running) {
                // 워크플로우 진행 중인 경우: 현재 요청 중인 권한으로 이동
                val initialViewPagerItemIndex = permissionItems
                    .indexOfFirst { it.id == currentState.currentId }
                viewPager.setCurrentItem(initialViewPagerItemIndex, false)
            }
        }
    }

    private fun requestPermission(permission: Permission.Special) {
        // 특별 권한: Settings로 이동
        PermissionRequestHelper.requestSpecialPermission(
            context = requireContext(),
            launcher = permissionLauncher,
            permission = permission,
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
            return PermissionBottomSheetContentFragment.newInstance(permission.id)
        }
    }

    companion object {

        internal const val TAG = "PermissionBottomSheetFragment"

        internal const val ARG_TARGET_PERMISSION_ITEM_ID = "target_permission_item_id"
        private const val ARG_PERMISSION_IDS = "permission_ids"

        internal fun show(
            fragmentManager: FragmentManager,
            permissionIds: List<Int>,
        ): PermissionBottomSheetFragment {
            Log.d(TAG, "show() called with :: permissionIds=$permissionIds")
            return PermissionBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putIntArray(ARG_PERMISSION_IDS, permissionIds.toIntArray())
                }
                show(fragmentManager, TAG)
            }
        }
    }
}