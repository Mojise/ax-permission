package kr.co.permission.ax_permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kr.co.permission.ax_permission.listener.AxPermissionListener
import kr.co.permission.ax_permission.util.AxPermissionList
import java.util.ArrayList

@SuppressLint("StaticFieldLeak")
class AxPermission private constructor(private val context: Context) {
    private var requiredPermissionsList = AxPermissionList()
    private var optionalPermissionsList = AxPermissionList()
    private var intent: Intent = Intent(context, AxPermissionActivity::class.java)

    fun setPermissionListener(listener: AxPermissionListener): AxPermission = apply {
        permissionListener = listener
    }

    /**
     * registerEssentialPermissionGlobally
     * 필수 권한 한번 등록시 기록이 남음
     ***/
    fun setRequiredPermissions(requiredPermissionsList: AxPermissionList): AxPermission = apply {
        this.requiredPermissionsList = (requiredPermissionsList)
        registerRequiredPermissionGloballyList = this.requiredPermissionsList
    }

    /**
     * registerChoicePermissionGlobally
     * 선택 권한 한번 등록시 기록이 남음
     ***/
    fun setOptionalPermissions(optionalPermissionsList: AxPermissionList): AxPermission = apply {
        this.optionalPermissionsList = optionalPermissionsList
        registerOptionalPermissionGloballyList = this.optionalPermissionsList
    }

    fun setSubmitButtonColors(buttonBackgroundColor: Int, textColor: Int): AxPermission = apply {
        submitButtonBackgroundColor = buttonBackgroundColor
        submitTextColor = textColor
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
        intent.putExtra("requiredPermissions", registerRequiredPermissionGloballyList)
        intent.putExtra("optionalPermissions", registerOptionalPermissionGloballyList)

        intent.putExtra("submitButtonColor", submitButtonBackgroundColor)
        intent.putExtra("submitTextColor", submitTextColor)
        intent.putExtra("state", "restart")
        context.startActivity(intent)
        this.check()
    }

    companion object {
        var permissionListener: AxPermissionListener? = null
        private var registerRequiredPermissionGloballyList = AxPermissionList()
        private var registerOptionalPermissionGloballyList = AxPermissionList()
        private var submitButtonBackgroundColor: Int = 0
        private var submitTextColor: Int = 0

        fun create(context: Context) = AxPermission(context)
    }
}