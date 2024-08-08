package kr.co.permission.ax_permission.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "AxPermissionPrefs"
        private const val STATE_IS_PERMISSION_BT = "STATE_IS_PERMISSION_BT"
        private const val REQUIRED_PERMISSIONS_KEY = "REQUIRED_PERMISSIONS_KEY"
        private const val OPTIONAL_PERMISSIONS_KEY = "OPTIONAL_PERMISSIONS_KEY"
        private const val SUBMIT_BUTTON_COLOR_KEY = "SUBMIT_BUTTON_COLOR_KEY"
        private const val SUBMIT_TEXT_COLOR_KEY = "SUBMIT_TEXT_COLOR_KEY"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setPermissionBt(value: Boolean) {
        sharedPreferences.edit().putBoolean(STATE_IS_PERMISSION_BT, value).apply()
    }

    fun getPermissionBt(): Boolean {
        return sharedPreferences.getBoolean(STATE_IS_PERMISSION_BT, false)
    }

    fun setRequiredPermissions(requiredPermissionsList: AxPermissionList) {
        val json = gson.toJson(requiredPermissionsList)
        sharedPreferences.edit().putString(REQUIRED_PERMISSIONS_KEY, json).apply()
    }

    fun getRequiredPermissions(): AxPermissionList {
        val json = sharedPreferences.getString(REQUIRED_PERMISSIONS_KEY, "")
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, object : TypeToken<AxPermissionList>() {}.type)
        } else {
            AxPermissionList()
        }
    }

    fun setOptionalPermissions(optionalPermissionsList: AxPermissionList) {
        val json = gson.toJson(optionalPermissionsList)
        sharedPreferences.edit().putString(OPTIONAL_PERMISSIONS_KEY, json).apply()
    }

    fun getOptionalPermissions(): AxPermissionList {
        val json = sharedPreferences.getString(OPTIONAL_PERMISSIONS_KEY, "")
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, object : TypeToken<AxPermissionList>() {}.type)
        } else {
            AxPermissionList()
        }
    }

    fun setSubmitButtonColors(buttonBackgroundColor: Int, textColor: Int) {
        sharedPreferences.edit().putInt(SUBMIT_BUTTON_COLOR_KEY, buttonBackgroundColor).apply()
        sharedPreferences.edit().putInt(SUBMIT_TEXT_COLOR_KEY, textColor).apply()
    }

    fun getSubmitButtonBackgroundColor(): Int {
        return sharedPreferences.getInt(SUBMIT_BUTTON_COLOR_KEY, 0) // 기본값 0
    }

    fun getSubmitTextColor(): Int {
        return sharedPreferences.getInt(SUBMIT_TEXT_COLOR_KEY, 0) // 기본값 0
    }
}