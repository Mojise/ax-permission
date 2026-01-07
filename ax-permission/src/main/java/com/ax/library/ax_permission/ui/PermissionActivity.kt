package com.ax.library.ax_permission.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.IntentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.ax.AxPermissionGlobalConfigurations
import com.ax.library.ax_permission.databinding.ActivityAxPermissionBinding
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.permission.PermissionItemData
import com.ax.library.ax_permission.permission.PermissionRequestHelper
import com.ax.library.ax_permission.util.dp
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
        val initialItems = PermissionItemData.generateInitialItems(
            activity = this,
            requiredPermissions = requiredPermissions,
            optionalPermissions = optionalPermissions,
        )
        PermissionViewModelFactory(initialItems)
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

    /**
     * 앱 설정 화면 이동 런처 (영구 거부된 런타임 권한용)
     */
    private val appSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // 앱 설정에서 돌아왔을 때 권한 상태 확인
        handleAppSettingsResult()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // configurations 복원 (프로세스 재시작 대응)
        restoreConfigurations()

        initTheme()
        super.onCreate(savedInstanceState)

        initView()
        collectPermissionItems()
        setupPermanentlyDeniedDialogResultListener()

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

    /**
     * 영구 거부 바텀시트의 Fragment Result Listener 설정
     *
     * Fragment Result API를 사용하여 바텀시트 버튼 클릭 이벤트를 처리합니다.
     * 이 방식은 Configuration change에도 안전합니다.
     */
    private fun setupPermanentlyDeniedDialogResultListener() {
        supportFragmentManager.setFragmentResultListener(
            PermissionPermanentlyDeniedBottomSheet.REQUEST_KEY,
            this
        ) { _, bundle ->
            when (bundle.getString(PermissionPermanentlyDeniedBottomSheet.RESULT_ACTION)) {
                PermissionPermanentlyDeniedBottomSheet.ACTION_POSITIVE -> {
                    Log.d(TAG, "PermanentlyDeniedBottomSheet: positive button clicked - 앱 설정 화면으로 이동")
                    PermissionRequestHelper.openAppSettings(this, appSettingsLauncher)
                }
                PermissionPermanentlyDeniedBottomSheet.ACTION_NEGATIVE -> {
                    Log.d(TAG, "PermanentlyDeniedBottomSheet: negative button clicked - 워크플로우 종료")
                    viewModel.finishWorkflow()
                }
            }
        }
    }

    /**
     * Intent로부터 configurations를 복원하여 AxPermission.configurations에 할당
     *
     * 프로세스가 재시작되면 메모리 상태가 초기화되지만,
     * Android OS가 같은 Intent로 Activity를 재생성하므로
     * Intent extras로부터 configurations를 복원할 수 있습니다.
     */
    private fun restoreConfigurations() {
        val configurations = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CONFIGURATIONS, AxPermissionGlobalConfigurations::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CONFIGURATIONS)
        }

        if (configurations != null) {
            AxPermission.configurations = configurations
        }
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
            setTitleWithSmartLineBreak()

            permissionListView.adapter = listAdapter
            permissionListView.itemAnimator = null

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

    /**
     * 앱 이름 길이에 따라 스마트하게 줄바꿈을 처리하는 타이틀 설정
     *
     * 첫 번째 문장이 2줄 이상으로 넘어가면 앱 이름 뒤에서 줄바꿈을 추가하여
     * 더 자연스러운 읽기 흐름을 제공합니다.
     *
     * 예시:
     * - 1줄인 경우: "앱명의 원활한 서비스 이용을 위해"
     * - 2줄 이상인 경우: "앱명의\n원활한 서비스 이용을 위해"
     */
    private fun setTitleWithSmartLineBreak() {
        val appName = getString(
            AxPermission.configurations.appNameResId
                .takeIf { it != 0 }
                ?: throw IllegalStateException("App name resource ID is not set in configurations.")
        )

        binding.tvTitle.text = getString(R.string.ax_permission_title_format, appName)

        // TextView가 레이아웃된 후 실행하여 정확한 너비를 얻음
        binding.tvTitle.post {
            val firstLine = getString(R.string.ax_permission_title_first_line_format, appName)
            val secondLine = getString(R.string.ax_permission_title_second_line)

            // TextView의 가용 너비 계산 (padding 제외)
            val availableWidth = binding.tvTitle.width -
                binding.tvTitle.paddingStart - binding.tvTitle.paddingEnd

            // 가용 너비가 유효하지 않으면 기본 텍스트 사용
            if (availableWidth <= 0) {
                binding.tvTitle.text = getString(R.string.ax_permission_title_format, appName)
                return@post
            }

            // StaticLayout으로 첫 번째 줄의 실제 줄 수 계산
            val lineCount = calculateLineCount(firstLine, binding.tvTitle.paint, availableWidth)

            // 2줄 이상이면 앱명 뒤에 줄바꿈 추가
            val finalText = if (lineCount >= 2) {
                val appNamePart = getString(R.string.ax_permission_title_first_line_app_name_part_format, appName)
                val remainingPart = getString(R.string.ax_permission_title_first_line_remaining)
                "$appNamePart\n$remainingPart\n$secondLine"
            } else {
                "$firstLine\n$secondLine"
            }

            binding.tvTitle.text = finalText
        }
    }

    /**
     * 주어진 텍스트가 지정된 너비에서 몇 줄로 표시되는지 계산
     */
    private fun calculateLineCount(text: String, paint: TextPaint, width: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(false)
                .build()
                .lineCount
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text,
                paint,
                width,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            ).lineCount
        }
    }

    private fun collectPermissionItems() {
        repeatOnStarted {
            viewModel.items.collect { items ->
                listAdapter.submitList(items) {
                    // DiffUtil 계산 완료 후 하이라이팅된 아이템을 최상단으로 스크롤
                    binding.permissionListView.smoothScrollToHighlightedItem(items)
                }
            }
        }
        repeatOnStarted {
            viewModel.isRequiredPermissionsAllGranted.collect(::updateBottomButtonState)
        }
        repeatOnStarted {
            viewModel.workflowState.collect(::handleWorkflowStateChange)
        }
    }

    /**
     * 하이라이팅된 권한 아이템을 RecyclerView 최상단으로 스크롤
     *
     * 워크플로우 진행 중 현재 처리 중인 권한 아이템이 RecyclerView의 최상단에 배치되도록
     * smooth scroll을 수행합니다. 리스트가 충분히 길지 않거나 아이템이 하단에 있어서
     * 물리적으로 스크롤이 불가능한 경우는 최대한 위로 스크롤합니다.
     */
    private fun RecyclerView.smoothScrollToHighlightedItem(items: List<Item>) {
        postDelayed({
            val highlightedIndex = items.indexOfFirst {
                it is Item.PermissionItem && it.isHighlights
            }
                .takeIf { it != -1 }
                ?: return@postDelayed

            val layoutManager = layoutManager as? LinearLayoutManager
                ?: return@postDelayed

            // 8dp 스크롤 상단 오프셋
            val offset = 4.dp

            // 최상단으로 smooth scroll (아이템 개수 기반 동적 속도 + 상단 오프셋 적용)
            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START

                override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                    // 기본 계산에 오프셋 추가
                    return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference) + offset
                }

                override fun calculateTimeForScrolling(dx: Int): Int {
                    return 100
                }
            }
            smoothScroller.targetPosition = highlightedIndex
            layoutManager.startSmoothScroll(smoothScroller)
        }, 100)
    }

    private fun updateBottomButtonState(isAllGranted: Boolean) {
        if (isAllGranted) {
            binding.btnBottomButton.text = getString(R.string.ax_permission_bottom_button_text_complete)
//            binding.btnBottomButton.setTextColor(getColor(R.color.ax_permission_primary_button_text_color))
//            binding.btnBottomButton.background = AppCompatResources.getDrawable(this, R.drawable.bg_ax_permission_primary_button)
        } else {
            binding.btnBottomButton.text = getString(R.string.ax_permission_bottom_button_text_allow_all)
//            binding.btnBottomButton.setTextColor(getColor(R.color.ax_permission_secondary_button_text_color))
//            binding.btnBottomButton.background = AppCompatResources.getDrawable(this, R.drawable.bg_ax_permission_secondary_button)
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

                when (currentPermissionItem) {
                    is Item.PermissionItem.Runtime -> {
                        requestCurrentRuntimePermissionInWorkFlow(state)
                    }
                    is Item.PermissionItem.Special -> {
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
        val runtimePermissionItem = viewModel.permissionItems.value
            .find { it.id == currentRuntimePermissionItemId }
            as? Item.PermissionItem.Runtime
            ?: return

        // Extract runtime permissions from the permission group
        val runtimePermissions = runtimePermissionItem.permissions

        if (runtimePermissions.isEmpty()) {
            return
        }

        // 권한 상태 확인
        val permissionState = PermissionChecker.getGroupRuntimePermissionState(this, runtimePermissions)

        Log.d(TAG, "requestCurrentRuntimePermissionInWorkFlow() :: permissionState=$permissionState, permissions=$runtimePermissions")

        when {
            permissionState == PermissionChecker.RuntimePermissionState.GRANTED -> {
                // 이미 허용됨 - 다음으로 진행
                viewModel.updatePermissionGrantedState(runtimePermissionItem, true)
                viewModel.proceedToNextPermissionInWorkflow()
            }
            permissionState.canShowSystemDialog -> {
                // 시스템 다이얼로그 표시 가능
                runtimePermissionLauncher.launch(runtimePermissions.toTypedArray())
            }
            permissionState.needsSettingsNavigation -> {
                // 영구 거부됨 - 앱 설정으로 안내하는 다이얼로그 표시
                showPermanentlyDeniedDialog(runtimePermissionItem)
            }
        }
    }

    /**
     * 영구 거부된 권한에 대해 앱 설정으로 이동을 안내하는 바텀시트 표시
     *
     * 버튼 클릭 이벤트는 Fragment Result API를 통해 처리됩니다.
     * @see setupPermanentlyDeniedDialogResultListener
     */
    private fun showPermanentlyDeniedDialog(permissionItem: Item.PermissionItem.Runtime) {
        // 이미 바텀시트가 표시 중이면 무시
        val existingDialog = supportFragmentManager.findFragmentByTag(PermissionPermanentlyDeniedBottomSheet.TAG)
        if (existingDialog != null) {
            Log.w(TAG, "showPermanentlyDeniedDialog() :: permissionItem=$permissionItem (바텀시트 이미 표시 중)")
            return
        }

        Log.d(TAG, "showPermanentlyDeniedDialog() :: permissionItem=$permissionItem")

        val permissionName = getString(permissionItem.titleResId)

        PermissionPermanentlyDeniedBottomSheet.show(
            fragmentManager = supportFragmentManager,
            permissionItemId = permissionItem.id,
            permissions = permissionItem.permissions,
            permissionName = permissionName,
        )
    }

    /**
     * 영구 거부 바텀시트 dismiss
     */
    private fun dismissPermanentlyDeniedDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(PermissionPermanentlyDeniedBottomSheet.TAG)
            as? PermissionPermanentlyDeniedBottomSheet
        dialog?.dismiss()
    }

    /**
     * 앱 설정에서 돌아왔을 때 권한 상태 확인 및 처리
     *
     * 바텀시트가 담당하는 권한 정보를 바텀시트로부터 직접 가져와서 확인합니다.
     * 이렇게 하면 workflow가 이미 다음 권한으로 진행되어도 올바른 권한을 확인할 수 있습니다.
     *
     * 주의: Activity가 Settings에서 돌아올 때 실행 순서
     * 1. onStart() → StateFlow collector 동작 → handleWorkflowStateChange() → workflow 진행
     * 2. appSettingsLauncher 콜백 → handleAppSettingsResult() (이 함수)
     *
     * StateFlow가 먼저 실행되어 workflow가 이미 진행되었을 수 있으므로,
     * proceedToNextPermissionInWorkflow()를 중복 호출하지 않도록 주의해야 합니다.
     */
    private fun handleAppSettingsResult() {
        // 바텀시트로부터 권한 정보 가져오기
        val dialog = supportFragmentManager.findFragmentByTag(PermissionPermanentlyDeniedBottomSheet.TAG)
            as? PermissionPermanentlyDeniedBottomSheet

        if (dialog == null) {
            Log.w(TAG, "handleAppSettingsResult() :: bottomSheet not found, nothing to do")
            return
        }

        val permissionItemId = dialog.permissionItemId
        val permissions = dialog.permissions

        Log.d(TAG, "handleAppSettingsResult() :: permissionItemId=$permissionItemId, permissions=$permissions")

        if (permissions.isEmpty()) {
            Log.w(TAG, "handleAppSettingsResult() :: permissions is empty, dismissing bottomSheet")
            dialog.dismiss()
            return
        }

        // 권한 허용 상태 확인
        val isGranted = PermissionChecker
            .checkRuntimePermission(this, permissions)
            .isGranted

        Log.d(TAG, "handleAppSettingsResult() :: isGranted=$isGranted")

        // ViewModel의 권한 아이템 상태 업데이트
        val runtimePermissionItem = viewModel.permissionItems.value
            .find { it.id == permissionItemId }
            as? Item.PermissionItem.Runtime

        if (runtimePermissionItem != null) {
            viewModel.updatePermissionGrantedState(runtimePermissionItem, isGranted)
        }

        if (isGranted) {
            // 권한 허용됨 - 바텀시트 닫기
            dialog.dismiss()

            // workflow가 아직 이 권한에 머물러 있는 경우에만 다음으로 진행
            // (StateFlow가 먼저 실행되어 이미 진행된 경우 중복 호출 방지)
            val currentState = viewModel.workflowState.value as? PermissionWorkflowState.Running
            if (currentState?.currentId == permissionItemId) {
                Log.d(TAG, "handleAppSettingsResult() :: workflow still at this permission, proceeding to next")
                viewModel.proceedToNextPermissionInWorkflow()
            } else {
                Log.d(TAG, "handleAppSettingsResult() :: workflow already advanced (currentId=${currentState?.currentId}), skipping proceed")
            }
        }
        // 여전히 거부됨 - 바텀시트 유지 (사용자가 다시 시도하거나 취소할 수 있음)
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

        val runtimePermissionItem = viewModel.permissionItems.value
            .find { it.id == currentState.currentId }
            as? Item.PermissionItem.Runtime
            ?: return

        Log.d(TAG, """
            handleRuntimePermissionResult()
            grants=$grants
            permissionsWithResourcesRuntime=${runtimePermissionItem}
        """.trimIndent())

        // 권한 허용 상태 업데이트
        val isGranted = PermissionChecker
            .checkRuntimePermission(this, runtimePermissionItem.permissions)
            .isGranted

        viewModel.updatePermissionGrantedState(runtimePermissionItem, isGranted)

        if (isGranted) {
            viewModel.proceedToNextPermissionInWorkflow()
        } else {
            viewModel.finishWorkflow()
        }
    }

    private fun onPermissionItemClicked(item: Item.PermissionItem) {
        if (item.isGranted) {
            showToast("[${getString(item.titleResId)}] 권한이 이미 허용되었습니다")
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
        private const val EXTRA_CONFIGURATIONS = "configurations"

        internal fun start(
            context: Context,
            theme: PermissionTheme,
            requiredPermissions: List<Permission>,
            optionalPermissions: List<Permission>,
            configurations: AxPermissionGlobalConfigurations,
        ) {
            val intent = Intent(context, PermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(EXTRA_THEME, theme)
                putExtra(EXTRA_REQUIRED_PERMISSIONS, ArrayList(requiredPermissions))
                putExtra(EXTRA_OPTIONAL_PERMISSIONS, ArrayList(optionalPermissions))
                putExtra(EXTRA_CONFIGURATIONS, configurations)
            }

            context.startActivity(intent)
        }
    }
}