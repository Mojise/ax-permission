package com.ax.library.ax_permission.ax

import android.app.Activity
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.model.PermissionTheme
import com.ax.library.ax_permission.permission.PermissionBuilder
import com.ax.library.ax_permission.permission.PermissionChecker
import com.ax.library.ax_permission.ui.PermissionActivity
import com.ax.library.ax_permission.util.dp

/**
 * # AX 권한 라이브러리
 *
 * Android 앱에서 런타임 권한과 특별 권한을 UI 기반으로 요청할 수 있는 라이브러리입니다.
 * 빌더 패턴을 사용하여 권한 요청을 구성하고, 콜백을 통해 결과를 받습니다.
 *
 * ## 주요 기능
 * - **런타임 권한 요청**: 카메라, 마이크, 위치, 저장소 등 일반 런타임 권한
 * - **특별 권한 요청**: 오버레이, 알림 리스너, 배터리 최적화 무시 등 Settings 이동이 필요한 권한
 * - **필수/선택 권한 분리**: 필수 권한과 선택 권한을 구분하여 요청
 * - **테마 지원**: 라이트/다크/시스템 테마 지원
 * - **UI 커스터마이징**: 색상, 코너 반경, 아이콘 패딩 등 커스터마이징 가능
 *
 * @see AxPermissionComposer 권한 요청 구성 클래스
 * @see Callback 권한 요청 결과 콜백 인터페이스
 */
public object AxPermission {

    private const val ICON_PADDINGS_DP_DEFAULT = 10

    @JvmSynthetic
    internal var callback: Callback? = null

    @JvmSynthetic
    internal var configurations = AxPermissionGlobalConfigurations.Default

    /**
     * Activity로부터 권한 요청 빌더를 생성합니다.
     *
     * @param activity 권한을 요청할 Activity
     * @return 권한 요청을 구성하기 위한 [AxPermissionComposer] 인스턴스
     */
    @JvmStatic
    public fun from(activity: Activity): AxPermissionComposer = AxPermissionComposer(activity)

    @JvmSynthetic
    internal fun clear() {
        callback = null
        //configurations = AxPermissionGlobalConfigurations.Default
    }

    /**
     * 권한 요청 결과를 받기 위한 콜백 인터페이스
     */
    public interface Callback {
        /**
         * 모든 필수 권한이 허용되었을 때 호출됩니다.
         *
         * @param context Context 객체
         */
        public fun onRequiredPermissionsAllGranted(context: Context)

        /**
         * 필수 권한 중 하나라도 거부되었을 때 호출됩니다.
         */
        public fun onRequiredPermissionsAnyOneDenied()
    }
}

/**
 * 권한 요청을 구성하기 위한 빌더 클래스
 *
 * [AxPermission.from]을 통해 인스턴스를 생성하고, 메서드 체이닝을 통해 권한 요청을 구성합니다.
 */
public class AxPermissionComposer internal constructor(private val activity: Activity) {

    private var theme: PermissionTheme = PermissionTheme.Default
    private var requiredPermissions: List<Permission> = emptyList()
    private var optionalPermissions: List<Permission> = emptyList()

    init {
        AxPermission.callback = null
    }

    /**
     * 라이트 테마만 사용하도록 설정합니다.
     *
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setOnlyDayTheme(): AxPermissionComposer = apply {
        theme = PermissionTheme.Day
    }

    /**
     * 다크 테마만 사용하도록 설정합니다.
     *
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setOnlyNightTheme(): AxPermissionComposer = apply {
        theme = PermissionTheme.Night
    }

    /**
     * 시스템 테마를 따르도록 설정합니다. (기본 값)
     *
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setDayNightTheme(): AxPermissionComposer = apply {
        theme = PermissionTheme.DayNight
    }

    /**
     * 앱 이름을 설정합니다. (필수)
     *
     * 권한 요청 화면에서 "앱 이름 앱을 사용하려면..." 형태로 표시됩니다.
     *
     * @param strResId 앱 이름 문자열 리소스 ID
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setAppName(@StringRes strResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(appNameResId = strResId)
    }

    /**
     * 권한 아이콘 패딩 dp 값을 설정합니다. (기본 값: 10dp)
     *
     * 예: `setIconPaddingsDp(16)` - 아이콘 주위에 16dp 패딩 설정
     *
     * @param paddings 아이콘 패딩 값 (dp 단위)
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setIconPaddingsDp(paddings: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(iconPaddings = paddings.dp)
    }

    /**
     * UI 요소의 코너 반경을 설정합니다. (기본 값: 8dp)
     *
     * 권한 아이템, 버튼 등의 모서리 둥글기를 설정합니다.
     *
     * @param cornerRadius 코너 반경 값 (dp 단위)
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setCornerRadiusDp(cornerRadius: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(cornerRadius = cornerRadius.dp.toFloat())
    }

    /**
     * 바텀시트의 코너 반경을 설정합니다. (기본 값: 16dp)
     *
     * 권한 상세 설명 바텀시트의 모서리 둥글기를 설정합니다.
     *
     * @param cornerRadius 바텀시트 코너 반경 값 (dp 단위)
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setBottomSheetCornerRadiusDp(cornerRadius: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(bottomSheetCornerRadius = cornerRadius.dp.toFloat())
    }

    /**
     * Primary 색상을 설정합니다.
     *
     * 주요 버튼, 아이콘 배경 등에 사용되는 색상을 변경합니다.
     *
     * @param colorResId 색상 리소스 ID
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setPrimaryColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(primaryColorResId = colorResId)
    }

    /**
     * 허용된 권한 아이템의 배경색을 설정합니다.
     *
     * 이미 허용된 권한 아이템이 표시될 때 사용되는 배경색을 변경합니다.
     *
     * @param colorResId 색상 리소스 ID
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setGrantedItemBackgroundColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(grantedItemBackgroundColorResId = colorResId)
    }

    /**
     * 진행 중인 권한 아이템의 하이라이트 색상을 설정합니다.
     *
     * 현재 권한 요청이 진행 중인 아이템을 강조 표시할 때 사용되는 색상을 변경합니다.
     *
     * @param colorResId 색상 리소스 ID
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setHighlightColor(@ColorRes colorResId: Int): AxPermissionComposer = apply {
        AxPermission.configurations = AxPermission.configurations.copy(highlightColorResId = colorResId)
    }

    /**
     * 필수 권한을 설정합니다.
     *
     * @param builder 권한 빌더 DSL
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setRequiredPermissions(builder: PermissionBuilder.() -> Unit): AxPermissionComposer = apply {
        val permissionBuilder = PermissionBuilder().apply(builder)
        requiredPermissions = permissionBuilder.build()
    }

    /**
     * 선택 권한을 설정합니다.
     *
     * @param builder 권한 빌더 DSL
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setOptionalPermissions(builder: PermissionBuilder.() -> Unit): AxPermissionComposer = apply {
        val permissionBuilder = PermissionBuilder().apply(builder)
        optionalPermissions = permissionBuilder.build()
    }

    /**
     * 권한 요청 결과를 받을 콜백을 설정합니다.
     *
     * @param callback 권한 요청 결과를 받을 [AxPermission.Callback] 인스턴스
     * @return 메서드 체이닝을 위한 [AxPermissionComposer] 인스턴스
     */
    public fun setCallback(callback: AxPermission.Callback): AxPermissionComposer = apply {
        AxPermission.callback = callback
    }

    /**
     * 권한 상태를 확인하고 권한 요청 화면을 표시합니다.
     *
     * 모든 필수 권한이 이미 허용되어 있다면 [AxPermission.Callback.onRequiredPermissionsAllGranted]를 즉시 호출합니다.
     * 그렇지 않으면 권한 요청 화면을 표시합니다.
     *
     * @throws IllegalStateException 앱 이름이 설정되지 않은 경우
     */
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
