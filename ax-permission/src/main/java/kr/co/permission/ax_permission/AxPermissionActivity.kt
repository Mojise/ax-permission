package kr.co.permission.ax_permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.permission.ax_permission.AxPermission.Companion.permissionListener
import kr.co.permission.ax_permission.listener.AxPermissionItemClickListener
import kr.co.permission.ax_permission.listener.AxPermissionListener
import kr.co.permission.ax_permission.model.AxPermissionModel
import kr.co.permission.ax_permission.util.ActivityResultHandler
import kr.co.permission.ax_permission.util.AlertDialogHandler
import kr.co.permission.ax_permission.util.AxPermissionList
import kr.co.permission.ax_permission.util.AxPermissionSettings
import kr.co.permission.ax_permission.util.CheckPermission
import kr.co.permission.ax_permission.util.PreferenceManager
import kotlin.system.exitProcess

class AxPermissionActivity : AppCompatActivity(), AxPermissionItemClickListener ,
    ActivityResultHandler.PermissionResultListener {
    private var requiredPermissionsItemList: List<AxPermissionModel>? = listOf()
    private var optionalPermissionsItemList: List<AxPermissionModel>? = listOf()

    private lateinit var perMissionRecyclerView: RecyclerView
    private lateinit var permissionBt: TextView
    private lateinit var toolbar_arrowLayout: ConstraintLayout
    private val perMissionAdapter: AxPermissionAdapter by lazy { AxPermissionAdapter(this, this) }
    private lateinit var axPermissionSettings: AxPermissionSettings
    private var currentPermissionModel: AxPermissionModel? = null
    private val activityResultHandler by lazy { ActivityResultHandler(this, this) }
    private var permissionActionLauncher: ActivityResultLauncher<Intent>? = null
    private var axPermissionListener: AxPermissionListener? = null

    /*상태값 저장*/
    private lateinit var preferenceManager: PreferenceManager

    // 현재 요청 중인 권한의 인덱스를 추적하는 변수
    private var currentPermissionIndex: Int = 0
    private var isHandlingEssentialPermissions: Boolean = true
    private var isPermissionBt: Boolean = false



    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private val settingsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            if (axPermissionListener == null) {
                if (!areAllPermissionsGranted()) {
                    Toast.makeText(
                        this@AxPermissionActivity,
                        "필수 권한이 있어야 앱을 실행할 수 있습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finishAffinity()
                    exitProcess(0)
                } else {
                    finish()
                }
            } else {
                currentPermissionModel?.let {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            it.permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        handlePermissionGranted()
                    } else {
                        handlePermissionDenied()
                    }
                }
            }
        }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        perMissionRecyclerView = findViewById(R.id.permissionRv)
        permissionBt = findViewById(R.id.permissionBt)
        toolbar_arrowLayout = findViewById(R.id.toolbar_arrowLayout)
        perMissionRecyclerView.adapter = perMissionAdapter
        axPermissionSettings = AxPermissionSettings()

        preferenceManager = PreferenceManager(this)
        isPermissionBt = preferenceManager.getPermissionBt()

        /*완료 버튼 색상 변경*/
        val submitButtonColor = intent.getIntExtra("submitButtonColor", 0)
        if (submitButtonColor != 0) {
            permissionBt.backgroundTintList =
                ContextCompat.getColorStateList(this, submitButtonColor)
        } else {
            permissionBt.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorAccent)
        }
        /*완료 버튼 텍스트 색상 변경*/
        val submitTextColor = intent.getIntExtra("submitTextColor", 0)
        if (submitTextColor != 0) {
            permissionBt.setTextColor(ContextCompat.getColorStateList(this, submitTextColor))
        } else {
            permissionBt.setTextColor(ContextCompat.getColorStateList(this, R.color.white))
        }

        /*필수 권한*/
        val requiredPermissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("requiredPermissions", AxPermissionList::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<AxPermissionList>("requiredPermissions")
        }


        /*선택 권한*/
        val optionalPermissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("optionalPermissions", AxPermissionList::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<AxPermissionList>("optionalPermissions")
        }

        axPermissionListener = permissionListener

        requiredPermissionsItemList = requiredPermissionsList?.let { list ->
            axPermissionSettings.setPermission(list)
        }

        optionalPermissionsItemList = optionalPermissionsList?.let { list ->
            axPermissionSettings.setPermission(list)
        }

        val perItemMap = HashMap<String, MutableList<AxPermissionModel>>()

        requiredPermissionsItemList?.let {
            perItemMap["* 필수 권한 *"] = it.toMutableList()
        }

        optionalPermissionsItemList?.let {
            perItemMap["* 선택 권한 *"] = it.toMutableList()
        }

        perMissionAdapter.setPerItemMap(perItemMap)
        permissionActionLauncher = activityResultHandler.permissionActionLauncher()


        /*확인버튼*/
        permissionBt.setOnClickListener {

            if (areAllPermissionsGranted()) {
                /*여기에 성공 콜백 들어가야함*/
                handlePermissionGranted()
                // 클릭 여부 상태 저장
                isPermissionBt = true
                preferenceManager.setPermissionBt(isPermissionBt)
                finish()
            } else {
                Toast.makeText(this, "필수 권한을 허용 해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        /*툴바 뒤로가기 버튼*/
        toolbar_arrowLayout.setOnClickListener {
            if (areAllPermissionsGranted()) {
                if (areAllPermissionsGranted()) {
                    handlePermissionGranted()
                } else {
                    handlePermissionDenied()
                }
                finish()
            } else {
                showBackPressedDialog()
            }
        }

        //뒤로 가기
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (areAllPermissionsGranted()) {
                        /*여기에 콜백 들어가야함*/
                        handlePermissionGranted()
                        finish()
                    } else {
                        showBackPressedDialog()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun showBackPressedDialog() {
        val alertDialogHandler = AlertDialogHandler(this)
        alertDialogHandler.showDialog(
            title = "권한 필요",
            message = "필수 권한을 허용하지 않으셨습니다. 앱을 종료 하시겠습니까?",
            positiveButtonText = "예",
            negativeButtonText = "아니요",
            onPositiveClick = {
                handlePermissionDenied()
                finish()
            },
            onNegativeClick = {
                it.dismiss()
            }
        )
    }

    /*성공 콜백*/
    private fun handlePermissionGranted() {
        axPermissionListener?.onPermissionGranted()

    }

    /*실패 콜백*/
    private fun handlePermissionDenied() {
        axPermissionListener?.onPermissionDenied()
    }


    // 권한 리스트 아이템 클릭
    override fun onPerClick(permissionModel: AxPermissionModel, adapterPosition: Int) {
        CheckPermission().checkSelfPermission(this, requiredPermissionsItemList)
        CheckPermission().checkSelfPermission(this, optionalPermissionsItemList)

        currentPermissionModel = permissionModel
        when (permissionModel.perType) {
            "action" -> {
                activityResultHandler.requestPermissionWithPackageName(
                    permissionActionLauncher,
                    permissionModel
                )
                /*val alertDialogHandler = AlertDialogHandler(this)
                alertDialogHandler.showCustomDialog(
                    icon = permissionModel.perIcon,
                    content = permissionModel.perContent,
                    onPositiveClick = {
                        activityResultHandler.requestPermissionWithPackageName(
                            permissionActionLauncher,
                            permissionModel
                        )
                        it.dismiss()
                    },
                    onNegativeClick = {
                        it.dismiss()
                    }
                )*/
            }

            "access" -> {
                if (ContextCompat.checkSelfPermission(
                        this@AxPermissionActivity,
                        permissionModel.permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showPermissionAlreadyGrantedDialog()
                } else {
                    if (requiredPermissionsItemList?.contains(permissionModel) == true) {
                        isHandlingEssentialPermissions = true
                        currentPermissionIndex =
                            requiredPermissionsItemList!!.indexOf(permissionModel)
                    } else if (optionalPermissionsItemList?.contains(permissionModel) == true) {
                        isHandlingEssentialPermissions = false
                        currentPermissionIndex =
                            optionalPermissionsItemList!!.indexOf(permissionModel)
                    }
                }
            }
        }
        requestNextPermission()
    }

    private fun requestNextPermission() {
        val permissionList = if (isHandlingEssentialPermissions) {
            requiredPermissionsItemList
        } else {
            optionalPermissionsItemList
        }

        permissionList?.let { it ->
            if (currentPermissionIndex < it.size) {
                val permissionModel = it[currentPermissionIndex]
                if (permissionModel.perType == "access" &&
                    ContextCompat.checkSelfPermission(
                        this,
                        permissionModel.permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(permissionModel.permission),
                        PERMISSION_REQUEST_CODE
                    )
                } else if (permissionModel.perType == "action") {

                    if (permissionModel.perState) {
                        currentPermissionIndex++
                        requestNextPermission()
                    } else {
                        val alertDialogHandler = AlertDialogHandler(this)
                        alertDialogHandler.showCustomDialog(
                            icon = permissionModel.perIcon,
                            content = permissionModel.perContent,
                            onPositiveClick = {
                                activityResultHandler.requestPermissionWithPackageName(
                                    permissionActionLauncher,
                                    permissionModel
                                )
                                it.dismiss()
                            },
                            onNegativeClick = {
                                it.dismiss()
                                currentPermissionIndex++
                                requestNextPermission() // Request the next permission
                            }
                        )
                    }
                } else {
                    currentPermissionIndex++
                    requestNextPermission() // 이미 권한이 부여된 경우 다음 권한 요청
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updatePermissionStatus()
                permissionBt.isVisible = areAllPermissionsGranted()
            } else {
                showPermissionDeniedDialog()
            }
            // 필수 권한 선택 다음 항목으로 이동
            if (isHandlingEssentialPermissions) {
                currentPermissionIndex++
                requestNextPermission()
            }
        }
    }

    private fun updatePermissionStatus() {
        // 필수 권한 리스트 업데이트 및 RecyclerView 갱신
        requiredPermissionsItemList?.forEachIndexed { index, essentialModel ->
            if (ContextCompat.checkSelfPermission(
                    this@AxPermissionActivity,
                    essentialModel.permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                essentialModel.perState = true
            }
            // 헤더가 있는 경우 인덱스에 +1을 해야 정확한 위치가 됩니다
            perMissionAdapter.notifyItemChanged(index + 1)
        }

        // 선택 권한 리스트 업데이트 및 RecyclerView 갱신
        optionalPermissionsItemList?.forEachIndexed { index, choicePerModel ->
            if (ContextCompat.checkSelfPermission(
                    this@AxPermissionActivity,
                    choicePerModel.permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                choicePerModel.perState = true
            }
            // 필수 권한과 필수 권한의 헤더 수를 더한 인덱스를 사용해야 합니다
            val position = (requiredPermissionsItemList?.size
                ?: 0) + 1 + index + 1 // 필수 권한 헤더 + 필수 권한 아이템 + 선택 권한 헤더
            perMissionAdapter.notifyItemChanged(position)
        }
    }

    private fun showPermissionDeniedDialog() {
        val alertDialogHandler = AlertDialogHandler(this)
        alertDialogHandler.showDialog(
            title = "권한 필요",
            message = "다음 권한이 거부되었습니다: ${currentPermissionModel?.perTitle}\n권한을 다시 요청하시겠습니까?",
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
        intent.data = Uri.parse("package:$packageName")
        try {
            settingsLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    /*이미 권한 부여 되어있을때 재설정 확인 다이얼로그*/
    private fun showPermissionAlreadyGrantedDialog() {
        val alertDialogHandler = AlertDialogHandler(this)
        alertDialogHandler.showDialog(
            title = "권한 이미 부여됨",
            message = "권한이 이미 설정되었습니다: \n 다시 권한을 설정하시겠습니까?",
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

    /*아이템 상태 업데이트*/
    private fun updateAndRefreshPermissions(permission: String, isGranted: Boolean) {
        // 필수 권한 리스트 업데이트 및 RecyclerView 갱신
        requiredPermissionsItemList?.forEachIndexed { index, essentialModel ->
            if (essentialModel.permission == permission) {
                essentialModel.perState = isGranted
            }
            // 헤더가 있는 경우 인덱스에 +1을 해야 정확한 위치가 됩니다
            perMissionAdapter.notifyItemChanged(index + 1)
        }

        // 선택 권한 리스트 업데이트 및 RecyclerView 갱신
        optionalPermissionsItemList?.forEachIndexed { index, choicePerModel ->
            if (choicePerModel.permission == permission) {
                choicePerModel.perState = isGranted
            }
            // 필수 권한과 필수 권한의 헤더 수를 더한 인덱스를 사용해야 합니다
            val position = (requiredPermissionsItemList?.size
                ?: 0) + 1 + index + 1 // 필수 권한 헤더 + 필수 권한 아이템 + 선택 권한 헤더
            perMissionAdapter.notifyItemChanged(position)
        }
    }

    // 모든 권한이 부여되었는지 확인하는 메서드
    private fun areAllPermissionsGranted(): Boolean {
        requiredPermissionsItemList?.forEach {
            if (!it.perState) {
                return false
            }
        }
        return true
    }

    /*권한 체크 상태*/
    private fun checkPermission() {
        CheckPermission().checkSelfPermission(this, requiredPermissionsItemList)
        CheckPermission().checkSelfPermission(this, optionalPermissionsItemList)
        permissionBt.isVisible = areAllPermissionsGranted()
        isPermissionBt = preferenceManager.getPermissionBt()

        val state = intent.getStringExtra("state")
        when (state) {
            "check" -> {
                if (areAllPermissionsGranted()) {
                    if (isPermissionBt) {
                        handlePermissionGranted()
                        finish()
                    }
                }else{
                    requestNextPermission()
                }
            }

            "restart" -> {
                permissionBt.isVisible = areAllPermissionsGranted()
            }
        }
    }

    override fun onPermissionLauncherResult(permission: String?, isGranted: Boolean) {
        if (permission != null) {
            updateAndRefreshPermissions(permission, isGranted)
        }
    }
}