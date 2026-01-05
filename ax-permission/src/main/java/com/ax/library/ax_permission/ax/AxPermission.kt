package com.ax.library.ax_permission.ax

import android.app.Activity
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.model.PermissionsWithResources
import com.ax.library.ax_permission.permission.PermissionBuilder
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.ui.PermissionActivity
import com.ax.library.ax_permission.util.dp

/**
 * # AX 권한 라이브러리
 */
public object AxPermission {

    private const val ICON_PADDINGS_DP_DEFAULT = 10

    @JvmSynthetic
    internal var callback: Callback? = null

    @JvmSynthetic
    internal var configurations = AxPermissionGlobalConfigurations.Default

    @JvmStatic
    public fun from(activity: Activity): AxPermissionComposer = AxPermissionComposer(activity)

    @JvmSynthetic
    internal fun clear() {
        callback = null
        //configurations = AxPermissionGlobalConfigurations.Default
    }

    public interface Callback {
        public fun onRequiredPermissionsAllGranted(context: Context)
        public fun onRequiredPermissionsAnyOneDenied()
    }
}

public class AxPermissionComposer internal constructor(private val activity: Activity) {

    private var theme: PermissionTheme = PermissionTheme.Default
    private var requiredPermissions: List<PermissionsWithResources> = emptyList()
    private var optionalPermissions: List<PermissionsWithResources> = emptyList()

    init {
        AxPermission.callback = null
    }

    public fun setOnlyDayTheme(): AxPermissionComposer = apply {
        theme = PermissionTheme.Day
    }

    public fun setOnlyNightTheme(): AxPermissionComposer = apply {
        theme = PermissionTheme.Night
    }

    public fun setDayNightTheme(): AxPermissionComposer = apply {
        theme = PermissionTheme.DayNight
    }

    public fun setAppName(@StringRes strResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(appNameResId = strResId)
    }

    /**
     * 권한 아이콘 패딩 dp 값 설정 (기본 값: 10dp)
     *
     * ex) `setIconPaddingsDp(16)`: 아이콘 주위에 16dp 패딩 설정
     */
    public fun setIconPaddingsDp(paddings: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(iconPaddings = paddings.dp)
    }

    public fun setCornerRadiusDp(cornerRadius: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(cornerRadius = cornerRadius.dp.toFloat())
    }

    public fun setPrimaryColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(primaryColorResId = colorResId)
    }

    public fun setTextTitleColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(textTitleColorResId = colorResId)
    }

    public fun setTextDescriptionColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(textDescriptionColorResId = colorResId)
    }

    public fun setBackgroundColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(backgroundColorResId = colorResId)
    }

    public fun setRequiredPermissions(builder: PermissionBuilder.() -> Unit): AxPermissionComposer = apply {
        val permissionBuilder = PermissionBuilder(activity).apply(builder)
        permissionBuilder.build()
    }

    public fun setOptionalPermissions(builder: PermissionBuilder.() -> Unit): AxPermissionComposer = apply {

    }

    public fun setCallback(callback: AxPermission.Callback): AxPermissionComposer = apply {
        AxPermission.callback = callback
    }

    public fun checkAndShow() {
        check(AxPermission.configurations.appNameResId != 0) {
            "앱 이름이 설정되지 않았습니다. `setAppName()` 메서드를 사용하여 앱 이름을 설정하세요."
        }

        // 모든 필수 권한이 허용되었는지 확인
        val allRequiredPermissionsGranted = requiredPermissions.all { permission ->
            PermissionChecker.check(activity, permission).isGranted
        }

        if (allRequiredPermissionsGranted) {
            AxPermission.callback?.onRequiredPermissionsAllGranted(activity)
            AxPermission.clear()
            return
        }

        PermissionActivity
            .start(
                context = activity,
                theme = theme,
                requiredPermissions = requiredPermissions,
                optionalPermissions = optionalPermissions,
                configurations = AxPermission.configurations,
            )
    }
}