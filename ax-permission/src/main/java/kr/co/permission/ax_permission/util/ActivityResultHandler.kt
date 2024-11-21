package kr.co.permission.ax_permission.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kr.co.permission.ax_permission.AxPermissionActivity
import kr.co.permission.ax_permission.R
import kr.co.permission.ax_permission.ext.showToast
import kr.co.permission.ax_permission.model.AxPermissionModel

class ActivityResultHandler(private val context: Context , private val listener: PermissionResultListener) {
    private var currentPermissionModel: AxPermissionModel? = null

    @SuppressLint("BatteryLife")
    fun requestPermissionWithPackageName(
        launcher: ActivityResultLauncher<Intent>?, permissionModel: AxPermissionModel?
    ) {
        currentPermissionModel = permissionModel

        var intent = Intent(permissionModel?.permission)

        if(permissionModel?.permission == Settings.ACTION_ACCESSIBILITY_SETTINGS) {
            intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                launcher?.launch(intent)
            }
        }else if(permissionModel?.permission == Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS){

            if(CheckPermission().isIgnoringBatteryOptimizations(context)){

                intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    launcher?.launch(intent)
                }

                Toast.makeText(context , "배터리 최적화 권한이 이미 허용되어 있습니다." , Toast.LENGTH_SHORT).show()
            }else{
                intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    launcher?.launch(intent)
                }
            }

        }else if(permissionModel?.permission == Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS){
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            launcher!!.launch(intent)
        }else{

            if (intent.resolveActivity(context.packageManager) != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                intent.data = Uri.parse("package:" + context.packageName)
                launcher!!.launch(intent)
            }else{
                intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    data = Uri.parse("package:" + context.packageName)
                }
                launcher?.launch(intent)
            }
        }
    }


    fun permissionActionLauncher(): ActivityResultLauncher<Intent> {
        return (context as AxPermissionActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val isGranted = checkPermission(currentPermissionModel?.permission)
            if (isGranted) {
                currentPermissionModel?.perTitle?.let { title -> context.showToast(context.getString(R.string.toast_permission_granted_format, title)) }
            } else {
                currentPermissionModel?.perTitle?.let { title -> context.showToast(context.getString(R.string.toast_permission_denied_format, title)) }
            }
            listener.onPermissionLauncherResult(currentPermissionModel?.permission, isGranted)
        }
    }

    @SuppressLint("BatteryLife")
    private fun checkPermission(permission: String?): Boolean {
        return when (permission) {
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION -> CheckPermission().isOverlayPermissionGranted(context)
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> CheckPermission().isIgnoringBatteryOptimizations(context)
            Settings.ACTION_NFC_SETTINGS -> CheckPermission().isNfcPermissionGranted(context)
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS -> CheckPermission().isNotificationListenerSettingsPermissionGranted(context)
            Settings.ACTION_ACCESSIBILITY_SETTINGS -> CheckPermission().isAccessibilityServiceEnabled(context)
            Manifest.permission.CHANGE_WIFI_STATE ->CheckPermission().isWifiEnabled(context)
            Settings.ACTION_USAGE_ACCESS_SETTINGS -> CheckPermission().isUsageAccessPermissionGranted(context)
            else -> false
        }
    }
    interface PermissionResultListener {
        fun onPermissionLauncherResult(permission: String?, isGranted: Boolean)
    }

}