package kr.co.permission.ax_permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kr.co.permission.ax_permission.listener.AxPermissionListener
import kr.co.permission.ax_permission.util.AxPermissionList
import kr.co.permission.ax_permission.util.PreferenceManager
import java.util.ArrayList

@SuppressLint("StaticFieldLeak")
class AxPermission private constructor(private val context: Context) {
    private var requiredPermissionsList = AxPermissionList()
    private var optionalPermissionsList = AxPermissionList()
    private var intent: Intent = Intent(context, AxPermissionActivity::class.java)
    private val preferenceManager = PreferenceManager(context)

    fun setPermissionListener(listener: AxPermissionListener): AxPermission = apply {
        if(listener != null){
            permissionListener = listener
        }
    }

    /**
     * registerEssentialPermissionGlobally
     * 필수 권한 한번 등록시 기록이 남음
     ***/
    fun setRequiredPermissions(requiredPermissionsList: AxPermissionList): AxPermission = apply {
        if(requiredPermissionsList.getPermissions().isNotEmpty()){
            this.requiredPermissionsList = (requiredPermissionsList)
            preferenceManager.setRequiredPermissions(requiredPermissionsList)
        }
    }

    /**
     * registerChoicePermissionGlobally
     * 선택 권한 한번 등록시 기록이 남음
     ***/
    fun setOptionalPermissions(optionalPermissionsList: AxPermissionList): AxPermission = apply {
        if(optionalPermissionsList.getPermissions().isNotEmpty()){
            this.optionalPermissionsList = optionalPermissionsList
            preferenceManager.setOptionalPermissions(optionalPermissionsList)
        }
    }

    fun setSubmitButtonColors(buttonBackgroundColor: Int, textColor: Int): AxPermission = apply {
        if(buttonBackgroundColor != null && textColor != null){
            submitButtonBackgroundColor = buttonBackgroundColor
            submitTextColor = textColor
            preferenceManager.setSubmitButtonColors(buttonBackgroundColor , textColor)
        }
    }

    fun check(): AxPermission {
        intent.putExtra("requiredPermissions", requiredPermissionsList)
        intent.putExtra("optionalPermissions", optionalPermissionsList)
        intent.putExtra("submitButtonColor", submitButtonBackgroundColor)
        intent.putExtra("submitTextColor", submitTextColor)
        intent.putExtra("state", "check")
        context.startActivity(intent)
        return this
    }

    fun onReStart(): AxPermission = apply {
        intent.putExtra("requiredPermissions", preferenceManager.getRequiredPermissions())
        intent.putExtra("optionalPermissions", preferenceManager.getOptionalPermissions())
        intent.putExtra("submitButtonColor", preferenceManager.getSubmitButtonBackgroundColor())
        intent.putExtra("submitTextColor", preferenceManager.getSubmitTextColor())
        intent.putExtra("state", "restart")
        context.startActivity(intent)
    }

    companion object {
        var permissionListener: AxPermissionListener? = null
        private var submitButtonBackgroundColor: Int = 0
        private var submitTextColor: Int = 0

        fun create(context: Context) = AxPermission(context)
    }
}