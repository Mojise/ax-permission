package com.ax.library.ax_permission

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.ui.PermissionActivity

/**
 * # AX 권한 라이브러리
 */
object AxPermission {

    internal var callback: Callback? = null

    @JvmStatic
    fun from(context: Context) = AxPermissionComposer(context)

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

    fun setPrimaryColor(@ColorRes colorResId: Int) = apply {
        // TODO: Not implemented yet
    }

    fun setTextTitleColor(@ColorRes colorResId: Int) = apply {
        // TODO: Not implemented yet
    }

    fun setTextDescriptionColor(@ColorRes colorResId: Int) = apply {
        // TODO: Not implemented yet
    }

    fun setBackgroundColor(@ColorRes colorResId: Int) = apply {
        // TODO: Not implemented yet
    }

    fun setAppName(@StringRes strResId: Int) = apply {
        // TODO: Not implemented yet
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