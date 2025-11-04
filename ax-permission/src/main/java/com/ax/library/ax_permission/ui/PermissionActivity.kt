package com.ax.library.ax_permission.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ax.library.ax_permission.AxPermission
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.databinding.ActivityAxPermissionBinding
import com.ax.library.ax_permission.util.overrideCloseActivityTransitionCompat
import com.ax.library.ax_permission.util.repeatOnStarted
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.util.overrideOpenActivityTransitionCompat
import com.ax.library.ax_permission.util.showToast
import kotlin.getValue

internal class PermissionActivity : AppCompatActivity() {

    private val binding: ActivityAxPermissionBinding by lazy { ActivityAxPermissionBinding.inflate(layoutInflater) }

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

    private val isAllPermissionsGranted: Boolean
        get() = viewModel.isAllPermissionsGranted.value

    private val isRequiredPermissionsAllGranted: Boolean
        get() = viewModel.isRequiredPermissionsAllGranted.value

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        overrideOpenActivityTransitionCompat()
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

    override fun finish() {
        super.finish()
        overrideCloseActivityTransitionCompat()
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
                    showPermissionBottomSheet()
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

    private fun onPermissionItemClicked(item: Item.PermissionItem) {
        if (item.isGranted) {
            showToast("[${item.name}] 권한이 이미 허용되었습니다")
        } else {
            showPermissionBottomSheet(item.id)
        }
    }

    private fun showPermissionBottomSheet(targetPermissionItemId: Int? = null) {
        if (isAllPermissionsGranted) {
            return
        }

        PermissionBottomSheetFragment
            .show(supportFragmentManager, targetPermissionItemId)
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