package com.ax.library.ax_permission

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.model.PermissionType
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.ui.PermissionActivity

/**
 * ### AX 권한 라이브러리
 */
object AxPermission {

    internal var callback: Callback? = null

    @JvmStatic
    fun from(context: Context) = AxPermissionComposer(context)

    interface Callback {
        fun onRequiredPermissionsAllGranted()
        fun onRequiredPermissionsAnyOneDenied()
    }
}

class AxPermissionComposer internal constructor(private val context: Context) {

    private var theme: PermissionTheme = PermissionTheme.Default

    init {
        AxPermission.callback = null
    }

    fun setOnlyDayTheme() = apply {
        theme = PermissionTheme.Day
    }

    fun setOnlyNightTheme() = apply {
        theme = PermissionTheme.Night
    }

    fun setDayAndNightTheme() = apply {
        theme = PermissionTheme.DayAndNight
    }

    fun setRequiredPermissions() = apply {

    }

    fun setOptionalPermissions() = apply {

    }

    fun setCallback(callback: AxPermission.Callback) = apply {
        AxPermission.callback = callback
    }

    fun checkAndShow() {
        if (PermissionChecker.check(context, PermissionType.DrawOverlays) &&
            PermissionChecker.check(context, PermissionType.AccessNotifications) &&
            PermissionChecker.check(context, PermissionType.IgnoreBatteryOptimizations)
        ) {
            AxPermission.callback?.onRequiredPermissionsAllGranted()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            PermissionActivity
                .start(
                    context = context,
                    theme = theme,
                )
        }, 500)
    }
}