package com.ax.library.ax_permission.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.IntentCompat
import com.ax.library.ax_permission.AxPermission
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.ActivityAxPermissionBinding
import com.ax.library.ax_permission.util.repeatOnStarted
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.util.showToast
import kotlin.getValue

internal class PermissionActivity : BasePermissionActivity<ActivityAxPermissionBinding>(R.layout.activity_ax_permission) {

    @Suppress("UNCHECKED_CAST")
    private val requiredPermissions: List<Permission> by lazy {
        IntentCompat.getSerializableExtra(intent, EXTRA_REQUIRED_PERMISSIONS, ArrayList::class.java) as? ArrayList<Permission>
            ?: emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    private val optionalPermissions: List<Permission> by lazy {
        IntentCompat.getSerializableExtra(intent, EXTRA_OPTIONAL_PERMISSIONS, ArrayList::class.java) as? ArrayList<Permission>
            ?: emptyList()
    }

    private val viewModel: PermissionViewModel by viewModels {
        PermissionViewModelFactory(application, requiredPermissions, optionalPermissions)
    }

    private val listAdapter = PermissionListAdapter(onPermissionItemClicked = ::onPermissionItemClicked)

    /**
     * 런타임 권한 요청 런처
     */
    private val runtimePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        handleRuntimePermissionResult(grants)
    }

    private val isAllPermissionsGranted: Boolean
        get() = viewModel.isAllPermissionsGranted.value

    private val isRequiredPermissionsAllGranted: Boolean
        get() = viewModel.isRequiredPermissionsAllGranted.value

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)

        initView()
        collectPermissionItems()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isRequiredPermissionsAllGranted) {
                    AxPermission.callback?.onRequiredPermissionsAllGranted(this@PermissionActivity)
                    finish()
                } else {
                    showExitBottomSheet()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // 특별 권한 Settings에서 돌아왔을 때 권한 상태 확인 및 워크플로우 계속 진행
        //checkSpecialPermissionAndContinueWorkflow()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) { // Only clear the callback if the activity is finishing, not on configuration changes
            AxPermission.callback = null
        }
    }

    private fun initTheme() {
        val theme = IntentCompat.getSerializableExtra(intent, EXTRA_THEME, PermissionTheme::class.java)

        delegate.localNightMode = when (theme) {
            PermissionTheme.Day -> AppCompatDelegate.MODE_NIGHT_NO
            PermissionTheme.Night -> AppCompatDelegate.MODE_NIGHT_YES
            PermissionTheme.DayNight -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // Default to follow system theme
        }
    }

    private fun initView() {
        with (binding) {
            tvTitle.text = getString(R.string.ax_permission_title)

            permissionListView.adapter = listAdapter

            btnBottomButton.setOnClickListener {
                if (isRequiredPermissionsAllGranted) {
                    AxPermission.callback?.onRequiredPermissionsAllGranted(this@PermissionActivity)
                    finish()
                } else {
                    // 배치 요청 시작 (권한 모두 허용하기)
                    viewModel.startRequestPermissionsWorkFlow()
                }
            }
        }
    }

    private fun collectPermissionItems() {
        repeatOnStarted {
            viewModel.items.collect(listAdapter::submitList)
        }
        repeatOnStarted {
            viewModel.isRequiredPermissionsAllGranted.collect(::updateBottomButtonState)
        }
        repeatOnStarted {
            viewModel.workflowState.collect(::handleWorkflowStateChange)
        }
    }

    private fun updateBottomButtonState(isAllGranted: Boolean) {
        if (isAllGranted) {
            binding.btnBottomButton.text = getString(R.string.ax_permission_bottom_button_text_complete)
            binding.btnBottomButton.setTextColor(getColor(R.color.ax_permission_primary_button_text_color))
            binding.btnBottomButton.background = AppCompatResources.getDrawable(this, R.drawable.bg_ax_permission_primary_button)
        } else {
            binding.btnBottomButton.text = getString(R.string.ax_permission_bottom_button_text_allow_all)
            binding.btnBottomButton.setTextColor(getColor(R.color.ax_permission_secondary_button_text_color))
            binding.btnBottomButton.background = AppCompatResources.getDrawable(this, R.drawable.bg_ax_permission_secondary_button)
        }
    }

    /**
     * 워크플로우 상태 변경 처리
     */
    private fun handleWorkflowStateChange(state: PermissionWorkflowState) {
        Log.w(TAG, "handleWorkflowStateChange() \nstate=$state")
        when (state) {
            is PermissionWorkflowState.Idle -> {

            }
            is PermissionWorkflowState.Running -> {
                val currentPermissionItem = viewModel.permissionItems.value.find { it.id == state.currentId }
                if (currentPermissionItem == null) {
                    return
                }

                when (currentPermissionItem.permission) {
                    is Permission.Runtime -> {
                        requestCurrentRuntimePermissionInWorkFlow(state)
                    }
                    is Permission.Special -> {
                        showPermissionBottomSheet()
                    }
                }
            }
        }
    }

    /**
     * 워크플로우 내에서 현재 런타임 권한 요청
     */
    private fun requestCurrentRuntimePermissionInWorkFlow(state: PermissionWorkflowState.Running) {
        val currentRuntimePermissionItemId = state.permissionItemIds[state.currentIndex]
        val currentRuntimePermissionItem = viewModel.permissionItems.value.find { it.id == currentRuntimePermissionItemId }
        if (currentRuntimePermissionItem == null || currentRuntimePermissionItem.permission !is Permission.Runtime) {
            return
        }
        runtimePermissionLauncher.launch(currentRuntimePermissionItem.permission.manifestPermissions.toTypedArray())
    }

    private fun showPermissionBottomSheet() {
        val bottomSheet = supportFragmentManager.findFragmentByTag(PermissionBottomSheetFragment.TAG) as? PermissionBottomSheetFragment
        Log.d(TAG, "showPermissionBottomSheet() :: bottomSheet?.isVisible=${bottomSheet?.isVisible}")
        if (bottomSheet?.isVisible == true) {
            // 이미 바텀시트가 열려있으면 다시 열지 않음
            return
        }

        val specialPermissionIds = viewModel.getConsecutiveSpecialPermissionIds()

        PermissionBottomSheetFragment
            .show(supportFragmentManager, specialPermissionIds)
    }

    /**
     * 런타임 권한 요청 결과 처리
     */
    private fun handleRuntimePermissionResult(grants: Map<String, Boolean>) {
        val currentState = viewModel.workflowState.value as? PermissionWorkflowState.Running
            ?: return

        val currentPermissionItem = viewModel.permissionItems.value
            .find { it.id == currentState.currentId }
            ?: return

        // 권한 허용 상태 업데이트
        val isGranted = PermissionChecker.check(this, currentPermissionItem.permission)
        viewModel.updatePermissionGrantedState(currentPermissionItem.permission, isGranted)
        viewModel.proceedToNext()
    }

    private fun onPermissionItemClicked(item: Item.PermissionItem) {
        if (item.isGranted) {
            showToast("[${item.name}] 권한이 이미 허용되었습니다")
        } else {
            // 개별 요청 시작 (권한 아이템 클릭)
            viewModel.startRequestPermissionsWorkflow(item)
        }
    }

    private fun showExitBottomSheet() {
        PermissionExitBottomSheet
            .show(supportFragmentManager)
            .setCallback(object : PermissionExitBottomSheet.Callback {
                override fun onExitButtonClicked() {
                    AxPermission.callback?.onRequiredPermissionsAnyOneDenied()
                    finish()
                    //finishAffinity()
                }
                override fun onContinueButtonClicked() {
                    // Do nothing, just dismiss the bottom sheet
                }
            })
    }

    /**
     * 특별 권한 상태 확인 및 워크플로우 계속 진행
     * Settings에서 돌아온 후 권한 상태를 확인하고 다음 단계로 진행
     */
    private fun checkSpecialPermissionAndContinueWorkflow() {
//        val currentState = viewModel.workflowState.value
//
//        // 워크플로우가 진행 중이 아니면 무시
//        if (currentState is PermissionWorkflowState.Idle) {
//            return
//        }
//
//        // 바텀시트가 표시되어 있으면 무시 (바텀시트 자체에서 처리)
//        val bottomSheet = supportFragmentManager.findFragmentByTag(com.ax.library.ax_permission.common.TAG) as? PermissionBottomSheetFragment
//        if (bottomSheet != null && bottomSheet.isVisible) {
//            return
//        }
//
//        // 현재 처리 중인 권한 아이템 찾기
//        val currentPermissionItem = when (currentState) {
//            is PermissionWorkflowState.BatchRequesting -> currentState.pendingPermissions.firstOrNull()
//            is PermissionWorkflowState.SingleRequesting -> currentState.permissionItem
//            is PermissionWorkflowState.Idle -> null
//        }
//
//        if (currentPermissionItem == null) {
//            return
//        }
//
//        // Special 권한이 아니면 무시 (Runtime 권한은 handleRuntimePermissionResult에서 처리)
//        if (currentPermissionItem.permission !is Permission.Special) {
//            return
//        }
//
//        // 권한 상태 업데이트
//        val isGranted = PermissionChecker.check(this, currentPermissionItem.permission)
//        viewModel.updatePermissionGrantedState(currentPermissionItem.permission, isGranted)
//
//        // 워크플로우 진행
//        when (currentState) {
//            is PermissionWorkflowState.BatchRequesting -> {
//                if (isGranted) {
//                    // 허용됨: 다음 권한으로 진행
//                    viewModel.proceedToNext()
//                } else {
//                    // 거부됨: 배치 요청 취소
//                    viewModel.cancelBatchRequest()
//                }
//            }
//            is PermissionWorkflowState.SingleRequesting -> {
//                // 개별 요청: 완료 (허용/거부 여부와 상관없이)
//                viewModel.finishWorkflow()
//            }
//            is PermissionWorkflowState.Idle -> {
//                // Do nothing
//            }
//        }
    }

    /**
     * 권한 요청 처리
     * @param currentPermission 현재 처리할 권한
     * @param allPendingPermissions 전체 대기 중인 권한 목록 (연속된 Special 권한 그룹 확인용)
     */
    private fun processPermissionRequest(currentPermission: Item.PermissionItem, allPendingPermissions: List<Item.PermissionItem>) {
//        when (currentPermission.permission) {
//            is Permission.Special -> {
//                // 특별 권한: 바텀시트가 이미 열려있는지 확인
//                val bottomSheet = supportFragmentManager.findFragmentByTag(com.ax.library.ax_permission.common.TAG) as? PermissionBottomSheetFragment
//
//                if (bottomSheet?.isVisible == true) {
//                    // 바텀시트가 이미 열려있으면 다시 열지 않음 (ViewPager가 알아서 처리)
//                    return
//                }
//
//                // 미허용 Special 권한이 있는지 확인
//                val hasNotGrantedSpecialPermissions = viewModel.permissionItems.value
//                    .filter { it.permission is Permission.Special }
//                    .any { !it.isGranted }
//
//                if (!hasNotGrantedSpecialPermissions) {
//                    // 모든 Special 권한이 이미 허용됨 → 다음 권한으로 진행
//                    when (viewModel.workflowState.value) {
//                        is PermissionWorkflowState.BatchRequesting -> {
//                            viewModel.proceedToNext()
//                        }
//                        is PermissionWorkflowState.SingleRequesting -> {
//                            viewModel.finishWorkflow()
//                        }
//                        is PermissionWorkflowState.Idle -> {
//                            // Do nothing
//                        }
//                    }
//                    return
//                }
//
//                // 바텀시트 표시 (연속된 Special 권한들을 ViewPager로 처리)
//                // 배치 요청일 때는 targetPermissionItemId를 null로 전달 (ViewPager로 연속 처리)
//                // 개별 요청일 때는 해당 권한 ID 전달 (단일 권한만 처리)
//                val workflowState = viewModel.workflowState.value
//                val targetId = if (workflowState is PermissionWorkflowState.SingleRequesting) {
//                    currentPermission.id
//                } else {
//                    null  // 배치 요청
//                }
//                showPermissionBottomSheet()
//            }
//            is Permission.Runtime -> {
//                // 런타임 권한: 시스템 다이얼로그 표시
//                runtimePermissionLauncher.launch(currentPermission.permission.manifestPermissions.toTypedArray())
//            }
//        }
    }

    companion object {

        private const val EXTRA_THEME = "theme"
        private const val EXTRA_REQUIRED_PERMISSIONS = "required_permissions"
        private const val EXTRA_OPTIONAL_PERMISSIONS = "optional_permissions"

        internal fun start(
            context: Context,
            theme: PermissionTheme,
            requiredPermissions: List<Permission>,
            optionalPermissions: List<Permission>,
        ) {
            val intent = Intent(context, PermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(EXTRA_THEME, theme)
                putExtra(EXTRA_REQUIRED_PERMISSIONS, ArrayList(requiredPermissions))
                putExtra(EXTRA_OPTIONAL_PERMISSIONS, ArrayList(optionalPermissions))
            }

            context.startActivity(intent)
        }
    }
}