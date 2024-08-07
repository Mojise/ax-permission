package kr.co.permission.ax_permission.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "AxPermissionPrefs"
        private const val STATE_IS_PERMISSION_BT = "STATE_IS_PERMISSION_BT"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setPermissionBt(value: Boolean) {
        sharedPreferences.edit().putBoolean(STATE_IS_PERMISSION_BT, value).apply()
    }

    fun getPermissionBt(): Boolean {
        return sharedPreferences.getBoolean(STATE_IS_PERMISSION_BT, false)
    }
}