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
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.ActivityAxPermissionBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.util.repeatOnStarted
import com.ax.library.ax_permission.util.showToast

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

    private val isRequiredPermissionsAllGranted: Boolean
        get() = viewModel.isRequiredPermissionsAllGranted.value

    /**
     * 런타임 권한 요청 런처
     */
    private val runtimePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        Log.d(TAG, "runtimePermissionLauncher :: grants=$grants")
        // 권한 요청 결과 처리
        // grants가 비어있지 않은 경우에만 처리
        // 권한 요청 시스템 다이얼로그가 표시 중인 상태에서, 액티비티의 Configuration이 변경되면 grants가 비어 있는 맵으로 반환되는 것으로 확인됨.
        if (grants.isNotEmpty()) {
            handleRuntimePermissionResult(grants)
        }
    }

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
            AxPermission.clear()
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
                        showSpecialPermissionsBottomSheet()
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

    /**
     * 특별 권한 바텀시트 표시
     */
    private fun showSpecialPermissionsBottomSheet() {
        val bottomSheet = supportFragmentManager.findFragmentByTag(SpecialPermissionBottomSheetFragment.TAG) as? SpecialPermissionBottomSheetFragment
        Log.d(TAG, "showPermissionBottomSheet() :: bottomSheet=(isAdded=${bottomSheet?.isAdded} isVisible=${bottomSheet?.isVisible})")
        if (bottomSheet?.isAdded == true) {
            // 이미 바텀시트가 열려있으면 다시 열지 않음
            return
        }

        val specialPermissionIds = viewModel.getConsecutiveSpecialPermissionIds()

        SpecialPermissionBottomSheetFragment
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

        Log.d(TAG, """
            handleRuntimePermissionResult()
            grants=$grants
            currentPermissionItem=${currentPermissionItem}
        """.trimIndent())

        // 권한 허용 상태 업데이트
        val isGranted = PermissionChecker.check(this, currentPermissionItem.permission)
        viewModel.updatePermissionGrantedState(currentPermissionItem.permission, isGranted)
        viewModel.proceedToNextPermissionInWorkflow()
    }

    private fun onPermissionItemClicked(item: Item.PermissionItem) {
        if (item.isGranted) {
            showToast("[${getString(item.permission.titleResId)}] 권한이 이미 허용되었습니다")
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