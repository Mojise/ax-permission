package com.ax.library.ax_permission.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.common.TAG
import com.ax.library.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentPermissionBottomSheetBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.permission.PermissionRequestHelper
import com.ax.library.ax_permission.util.disableUserInputAndTouch
import com.ax.library.ax_permission.util.indexOfFirst

internal class PermissionBottomSheetFragment : FloatingBottomSheetDialogFragment<FragmentPermissionBottomSheetBinding>() {

    override val layoutResId: Int = R.layout.fragment_permission_bottom_sheet

    private val activityViewModel: PermissionViewModel by activityViewModels()
    private var targetPermissionItemId: Int? = null
    private lateinit var adapter: ViewPagerAdapter

    private val permissionItems: List<Item.PermissionItem>
        get() = activityViewModel.permissionItems.value

    private var currentViewPagerIndex: Int
        get() = binding.viewPager.currentItem
        set(value) { binding.viewPager.currentItem = value }

    // 런타임 권한 요청을 위한 ActivityResultLauncher
    private val runtimePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        // 결과는 onResume에서 처리됨 (checkPermissionAndMoveNextOrDismissOrNothing)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        targetPermissionItemId = arguments?.getInt(ARG_TARGET_PERMISSION_ITEM_ID, -1)
            ?.takeIf { it >= 0 }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewPagerAndData()
    }

    override fun onResume() {
        super.onResume()

        checkPermissionAndMoveNextOrDismissOrNothing()
    }

    private fun checkPermissionAndMoveNextOrDismissOrNothing() {
        val currentPermission = permissionItems.getOrNull(currentViewPagerIndex)
            ?: return

        val isGranted = PermissionChecker.check(requireContext(), currentPermission.permission)
        activityViewModel.updatePermissionGrantedState(currentPermission.permission, isGranted)

        if (isGranted) {
            if (targetPermissionItemId == null) {
                // 현재 아이템보다 뒤에 있는 권한 중, 첫 번째에 있는 권한의 인덱스
                val nextNotGrantedPermissionIndex = permissionItems
                    .indexOfFirst(currentViewPagerIndex + 1, Item.PermissionItem::isNotGranted)
                    .takeIf { it >= 0 }

                val delay = if (currentPermission.permission is Permission.Special)
                    300L // 특별 권한은 300ms 딜레이 (Settings에서 돌아온 후)
                    else 0L // 런타임 권한은 딜레이 없음

                Handler(Looper.getMainLooper()).postDelayed({
                    if (nextNotGrantedPermissionIndex != null) {
                        binding.viewPager.currentItem = nextNotGrantedPermissionIndex
                    } else {
                        dismiss()
                    }
                }, delay)
            } else {
                dismiss()
            }
        } else {
            // 권한이 허용되지 않은 경우, 현재 아이템의 인덱스를 유지
        }
    }

    private fun initView() {
        with (binding) {
            btnPositive.setOnClickListener {
                val permission = permissionItems[currentViewPagerIndex]
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

            val initialViewPagerItemIndex =
                if (targetPermissionItemId == null) {
                    activityViewModel.firstNotGrantedPermissionItemIndex
                } else {
                    permissionItems.indexOfFirst { it.id == targetPermissionItemId }
                } ?: throw IllegalStateException("No not granted permission items found")

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

            viewPager.setCurrentItem(initialViewPagerItemIndex, false)
        }
    }

    private fun requestPermission(permissionItem: Item.PermissionItem) {
        when (val permission = permissionItem.permission) {
            is Permission.Special -> {
                // 특별 권한: Settings로 이동
                PermissionRequestHelper.requestSpecialPermission(
                    activity = requireActivity() as androidx.appcompat.app.AppCompatActivity,
                    permission = permission
                )
            }
            is Permission.Runtime -> {
                // 런타임 권한: ActivityResultLauncher 사용
                val permissions = permission.manifestPermissions.toTypedArray()
                if (permissions.isNotEmpty()) {
                    runtimePermissionsLauncher.launch(permissions)
                }
            }
        }
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
            return PermissionBottomSheetContentFragment.Companion.newInstance(permission.id)
        }
    }

    companion object {

        internal const val ARG_TARGET_PERMISSION_ITEM_ID = "target_permission_item_id"

        internal fun show(
            fragmentManager: FragmentManager,
            targetPermissionItemId: Int?,
        ): PermissionBottomSheetFragment? {
            return PermissionBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    if (targetPermissionItemId != null) {
                        putInt(ARG_TARGET_PERMISSION_ITEM_ID, targetPermissionItemId)
                    }
                }
                show(fragmentManager, TAG)
            }
        }
    }
}