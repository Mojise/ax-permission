package com.ax.library.ax_permission.permission

import android.content.Context
import android.content.SharedPreferences

/**
 * 권한 요청 이력 추적기
 *
 * SharedPreferences를 사용하여 showRational=true가 발생했던 권한을 추적합니다.
 * 이를 통해 "최초 상태"와 "영구 거부" 상태를 구분할 수 있습니다.
 */
internal object PermissionRequestTracker {

    private const val PREFS_NAME = "ax_permission_tracker"
    private const val KEY_PREFIX_RATIONALE_SHOWN = "rationale_shown_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * showRational=true가 감지되면 호출하여 기록합니다.
     *
     * @param permission 권한 문자열 (예: Manifest.permission.CAMERA)
     */
    fun markRationaleShown(context: Context, permission: String) {
        getPrefs(context).edit()
            .putBoolean("$KEY_PREFIX_RATIONALE_SHOWN$permission", true)
            .apply()
    }

    /**
     * 여러 권한에 대해 showRational=true가 감지되면 호출하여 기록합니다.
     *
     * @param permissions 권한 문자열 리스트
     */
    fun markRationaleShown(context: Context, permissions: List<String>) {
        val editor = getPrefs(context).edit()
        permissions.forEach { permission ->
            editor.putBoolean("$KEY_PREFIX_RATIONALE_SHOWN$permission", true)
        }
        editor.apply()
    }

    /**
     * 과거에 showRational=true가 발생했었는지 확인합니다.
     *
     * @param permission 권한 문자열
     * @return showRational=true가 한 번이라도 발생했으면 true
     */
    fun wasRationaleEverShown(context: Context, permission: String): Boolean {
        return getPrefs(context).getBoolean("$KEY_PREFIX_RATIONALE_SHOWN$permission", false)
    }

    /**
     * 특정 권한의 추적 데이터를 초기화합니다.
     * (주로 테스트용)
     */
    fun clear(context: Context, permission: String) {
        getPrefs(context).edit()
            .remove("$KEY_PREFIX_RATIONALE_SHOWN$permission")
            .apply()
    }

    /**
     * 모든 추적 데이터를 초기화합니다.
     * (주로 테스트용)
     */
    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
