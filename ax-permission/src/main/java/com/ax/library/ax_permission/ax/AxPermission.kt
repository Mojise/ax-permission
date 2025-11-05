package com.ax.library.ax_permission.ax

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.ui.PermissionActivity
import com.ax.library.ax_permission.util.dp

/**
 * # AX 권한 라이브러리
 */
object AxPermission {

    private const val ICON_PADDINGS_DP_DEFAULT = 10

    internal var callback: Callback? = null

    internal var configurations = AxPermissionGlobalConfigurations.Default

    @JvmStatic
    fun from(context: Context) = AxPermissionComposer(context)

    internal fun clear() {
        callback = null
        configurations = AxPermissionGlobalConfigurations.Default
    }

    interface Callback {
        fun onRequiredPermissionsAllGranted(context: Context)
        fun onRequiredPermissionsAnyOneDenied()
    }
}

class AxPermissionComposer internal constructor(private val context: Context) {

    private var theme: PermissionTheme = PermissionTheme.Default
    private var requiredPermissions: List<Permission> = emptyList()
    private var optionalPermissions: List<Permission> = emptyList()

    init {
        AxPermission.callback = null
    }

    fun setOnlyDayTheme() = apply {
        theme = PermissionTheme.Day
    }

    fun setOnlyNightTheme() = apply {
        theme = PermissionTheme.Night
    }

    fun setDayNightTheme() = apply {
        theme = PermissionTheme.DayNight
    }

    fun setAppName(@StringRes strResId: Int) = apply {
        // TODO: Not implemented yet
    }

    /**
     * 권한 아이콘 패딩 dp 값 설정 (기본 값: 10dp)
     *
     * ex) `setIconPaddingsDp(16)`: 아이콘 주위에 16dp 패딩 설정
     */
    fun setIconPaddingsDp(paddings: Int) = apply {
        AxPermission.configurations = AxPermission.configurations.copy(iconPaddings = paddings.dp)
    }

//    fun setCornerRadius() = apply {
//        // TODO: Not implemented yet (아직은 구현하기 어려움)
//    }

    fun setPrimaryColor(@ColorRes colorResId: Int) = apply {
        AxPermission.configurations = AxPermission.configurations.copy(primaryColorResId = colorResId)
    }

    fun setTextTitleColor(@ColorRes colorResId: Int) = apply {
        AxPermission.configurations = AxPermission.configurations.copy(textTitleColorResId = colorResId)
    }

    fun setTextDescriptionColor(@ColorRes colorResId: Int) = apply {
        AxPermission.configurations = AxPermission.configurations.copy(textDescriptionColorResId = colorResId)
    }

    fun setBackgroundColor(@ColorRes colorResId: Int) = apply {
        AxPermission.configurations = AxPermission.configurations.copy(backgroundColorResId = colorResId)
    }

    fun setRequiredPermissions(vararg permissions: Permission) = apply {
        this.requiredPermissions = permissions
            .distinct()
            .filterNot(Permission::isEmptyPermissions)
    }

    fun setOptionalPermissions(vararg permissions: Permission) = apply {
        this.optionalPermissions = permissions
            .distinct()
            .filterNot(Permission::isEmptyPermissions)
    }

    fun setCallback(callback: AxPermission.Callback) = apply {
        AxPermission.callback = callback
    }

    fun checkAndShow() {
        // 모든 필수 권한이 허용되었는지 확인
        val allRequiredPermissionsGranted = requiredPermissions.all { permission ->
            PermissionChecker.check(context, permission)
        }

        if (allRequiredPermissionsGranted) {
            AxPermission.callback?.onRequiredPermissionsAllGranted(context)
            AxPermission.clear()
            return
        }

        PermissionActivity
            .start(
                context = context,
                theme = theme,
                requiredPermissions = requiredPermissions,
                optionalPermissions = optionalPermissions,
            )
    }
}