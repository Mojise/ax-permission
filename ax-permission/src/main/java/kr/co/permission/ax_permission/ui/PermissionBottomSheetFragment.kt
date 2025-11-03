package kr.co.permission.ax_permission.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ax.library.ax_permission.R
import kr.co.permission.ax_permission.common.TAG
import kr.co.permission.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentPermissionBottomSheetBinding
import kr.co.permission.ax_permission.model.Item
import kr.co.permission.ax_permission.model.PermissionType
import kr.co.permission.ax_permission.permission.PermissionChecker
import kr.co.permission.ax_permission.util.disableUserInputAndTouch
import kr.co.permission.ax_permission.util.indexOfFirst

internal class PermissionBottomSheetFragment : FloatingBottomSheetDialogFragment<FragmentPermissionBottomSheetBinding>() {

    override val layoutResId: Int = R.layout.fragment_permission_bottom_sheet

    private val activityViewModel: PermissionViewModel by activityViewModels()
    private var targetPermissionItemId: Int? = null
    private lateinit var adapter: ViewPagerAdapter

    private val permissionItems: List<Item.Permission>
        get() = activityViewModel.permissionItems.value

    private var currentViewPagerIndex: Int
        get() = binding.viewPager.currentItem
        set(value) { binding.viewPager.currentItem = value }

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

        val isGranted = PermissionChecker.check(requireContext(), currentPermission.type)
        activityViewModel.updatePermissionGrantedState(currentPermission.type, isGranted)

        if (isGranted) {
            if (targetPermissionItemId == null) {
                // 현재 아이템보다 뒤에 있는 권한 중, 첫 번째에 있는 권한의 인덱스
                val nextNotGrantedPermissionIndex = permissionItems
                    .indexOfFirst(currentViewPagerIndex + 1, Item.Permission::isNotGranted)
                    .takeIf { it >= 0 }

                val delay = if (currentPermission.type.isAction)
                    300L // 액션 권한은 300ms 딜레이
                    else 0L // 일반 권한은 딜레이 없음

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
                        if (permissionItems[position].type.isAction) {
                            getString(R.string.ax_permission_bottom_sheet_positive_button_text_move_to_settings)
                        } else {
                            getString(R.string.ax_permission_bottom_sheet_positive_button_text_allow)
                        }
                }
            })

            viewPager.setCurrentItem(initialViewPagerItemIndex, false)
        }
    }

    private fun requestPermission(permission: Item.Permission) {
        when (permission.type) {
            PermissionType.DrawOverlays -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = "package:${requireContext().packageName}".toUri()
                }
                startActivity(intent)
            }
            PermissionType.AccessNotifications -> {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                startActivity(intent)
            }
            PermissionType.IgnoreBatteryOptimizations -> {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:${requireContext().packageName}".toUri()
                }
                startActivity(intent)
            }
        }
    }

    /**
     * ViewPager's FragmentStateAdapter
     */
    internal class ViewPagerAdapter constructor(
        fragment: Fragment,
        private val permissions: List<Item.Permission>,
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