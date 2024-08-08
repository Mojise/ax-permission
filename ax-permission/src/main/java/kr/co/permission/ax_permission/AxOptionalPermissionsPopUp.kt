package kr.co.permission.ax_permission

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kr.co.permission.ax_permission.util.AlertDialogHandler

class AxOptionalPermissionsPopUp private constructor(private val context: Context) {

    companion object {
        private const val REQUEST_CODE = 1001

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AxOptionalPermissionsPopUp? = null

        fun getInstance(context: Context): AxOptionalPermissionsPopUp {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AxOptionalPermissionsPopUp(context).also { INSTANCE = it }
            }
        }
    }

    private var onOptionalPermissionGranted: (() -> Unit)? = null
    private var onOptionalPermissionDenied: (() -> Unit)? = null

    fun optionalPermissionsPopUp(
        permissionList: List<String>,
        onOptionalPermissionGranted: () -> Unit,
        onOptionalPermissionDenied: () -> Unit
    ) {
        this.onOptionalPermissionGranted = onOptionalPermissionGranted
        this.onOptionalPermissionDenied = {
            showPermissionDeniedDialog()
            onOptionalPermissionDenied.invoke()
        }

        val permissionsToRequest = permissionList.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            onOptionalPermissionGranted.invoke()
        } else {
            val fragment = PermissionFragment.newInstance(permissionsToRequest.toTypedArray(), REQUEST_CODE)
            fragment.setPermissionResultCallback { granted ->
                if (granted) {
                    onOptionalPermissionGranted.invoke()
                } else {
                    this.onOptionalPermissionDenied?.invoke()
                }
            }
            val fragmentActivity = context as FragmentActivity
            fragmentActivity.supportFragmentManager.beginTransaction()
                .add(fragment, PermissionFragment::class.java.simpleName)
                .commitAllowingStateLoss()
        }
    }

    private fun showPermissionDeniedDialog() {
        val alertDialogHandler = AlertDialogHandler(context)
        alertDialogHandler.showDialog(
            title = "권한 필요",
            message = "다음 권한이 거부되었습니다: 해당 권한이 없으면 기능을 사용하실 수 없습니다.\n권한을 다시 요청하시겠습니까?",
            positiveButtonText = "예",
            negativeButtonText = "아니요",
            onPositiveClick = {
                startPerSettingActivity()
                it.dismiss()
            },
            onNegativeClick = {
                it.dismiss()
            }
        )
    }

    /*앱 자체 권한창 으로 이동*/
    private fun startPerSettingActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    class PermissionFragment : Fragment() {

        private var permissions: Array<String>? = null
        private var callback: ((Boolean) -> Unit)? = null

        companion object {
            private const val ARG_PERMISSIONS = "permissions"

            fun newInstance(permissions: Array<String>, REQUEST_CODE: Int): PermissionFragment {
                val fragment = PermissionFragment()
                val args = Bundle()
                args.putStringArray(ARG_PERMISSIONS, permissions)
                fragment.arguments = args
                return fragment
            }
        }

        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val allGranted = results.values.all { it }
            callback?.invoke(allGranted)
            parentFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                permissions = it.getStringArray(ARG_PERMISSIONS)
            }
            requestPermissions()
        }

        private fun requestPermissions() {
            permissions?.let {
                requestPermissionLauncher.launch(it)
            }
        }

        fun setPermissionResultCallback(callback: (Boolean) -> Unit) {
            this.callback = callback
        }
    }
}