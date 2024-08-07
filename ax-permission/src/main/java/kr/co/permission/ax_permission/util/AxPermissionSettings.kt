package kr.co.permission.ax_permission.util

import android.Manifest
import android.annotation.SuppressLint
import android.provider.Settings
import kr.co.permission.ax_permission.R
import kr.co.permission.ax_permission.model.AxPermissionModel

class AxPermissionSettings {
    
    private var perMap: HashMap<String, AxPermissionModel> = hashMapOf()

    @SuppressLint("BatteryLife")
    fun setPermission(perList: AxPermissionList): List<AxPermissionModel> {
        val perData = mutableListOf<AxPermissionModel>()
        val addedPermissions = mutableSetOf<String>()

        perList.forEach { data ->
            val permission = perMap[data.permission]
            if (permission != null) {
                // READ_MEDIA_IMAGES 또는 READ_MEDIA_VIDEO 권한일 경우 하나로 묶기
                val isMediaPermission = data.permission == Manifest.permission.READ_MEDIA_IMAGES ||
                        data.permission == Manifest.permission.READ_MEDIA_VIDEO

                if (isMediaPermission) {
                    if (!addedPermissions.contains("READ_MEDIA")) {
                        if (data.title.isNotEmpty()) {
                            permission.perTitle = data.title
                        }
                        if (data.description.isNotEmpty()) {
                            permission.perContent = data.description
                        }
                        perData.add(permission)
                        addedPermissions.add("READ_MEDIA")
                    }
                } else {
                    if (data.title.isNotEmpty()) {
                        permission.perTitle = data.title
                    }
                    if (data.description.isNotEmpty()) {
                        permission.perContent = data.description
                    }
                    perData.add(permission)
                }
            }
        }
        return perData
    }

    init {
        /*접근 권한*/
        perMap[Settings.ACTION_MANAGE_OVERLAY_PERMISSION] = AxPermissionModel(
            "앱 위에 그리기",
            "앱이 다른 앱 위에 표시되도록 허용하여, 더욱 편리한 사용자 경험을 제공합니다.",
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            false,"action", R.drawable.android_draw
        )
        perMap[Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS] = AxPermissionModel(
            "배터리 최적화 무시 설정",
            "앱이 백그라운드에서 원활하게 실행될 수 있도록 배터리 최적화에서 제외하여, 실시간 알림이나 업데이트 등을 놓치지 않도록 합니다.",
            Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,
            false,
            "action",R.drawable.ignore_battery
        )
        perMap[Settings.ACTION_NFC_SETTINGS] = AxPermissionModel(
            "NFC 설정",
            "NFC 기능을 사용하여 간편하게 기기 간 데이터를 주고받거나 결제 등을 할 수 있도록 합니다.",
            Settings.ACTION_NFC_SETTINGS,
            false,
            "action",R.drawable.android_nfc
        )
        perMap[Settings.ACTION_ACCESSIBILITY_SETTINGS] = AxPermissionModel(
            "접근성 설정",
            "앱이 시스템 설정에 접근하여 사용자의 접근성을 향상시키는 기능을 제공합니다. 예를 들어, 시각 장애인을 위한 화면 읽기 기능을 지원하거나, 특정 기능을 자동화할 수 있습니다.",
            Settings.ACTION_ACCESSIBILITY_SETTINGS,
            false,
            "action",R.drawable.android_access
        )
        perMap[Manifest.permission.CHANGE_WIFI_STATE] = AxPermissionModel(
            "WiFi 상태 변경",
            "WiFi 상태를 변경하기 위해 필요한 권한입니다.",
            Manifest.permission.CHANGE_WIFI_STATE,
            false,
            "action",R.drawable.android_wifi // 권한 허용 화면으로 이동
        )

        perMap[Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS] = AxPermissionModel(
            "알람 접근 설정",
            "앱이 알림을 접근하고 관리하기 위해 필요한 권한입니다.이 권한을 통해 앱은 다른 앱에서 오는 알림을 읽고, 특정 알림에 기반한 자동 응답 또는 작업을 수행할 수 있습니다.",
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS,
            false,
            "action",R.drawable.android_alarm
        )
        perMap[Settings.ACTION_USAGE_ACCESS_SETTINGS] = AxPermissionModel(
            "사용정보 접근 설정",
            "스마트폰 사용 정보를 읽어오기 위해 사용정보 접근 권한이 필요합니다. \n 권한을 동의하지 않을 경우, 스마트폰 사용정보 를 읽어오실 수 없습니다.",
            Settings.ACTION_USAGE_ACCESS_SETTINGS,
            false,
            "action",R.drawable.android_condition
        )


        /*팝업 알림*/
        perMap[Manifest.permission.CALL_PHONE] = AxPermissionModel(
            "전화 걸기",
            "앱이 사용자의 기기에서 직접 전화를 걸 수 있도록 허용하는 권한입니다. 예를 들어, 저장된 연락처에 바로 전화를 걸거나, 특정 번호로 자동 연결하는 기능을 사용할 수 있습니다.",
            Manifest.permission.CALL_PHONE,
            false,"access",0
        )
        perMap[Manifest.permission.POST_NOTIFICATIONS] = AxPermissionModel(
            "알림",
            "앱이 사용자에게 중요한 정보를 알리기 위해 알림을 표시할 수 있도록 허용하는 권한입니다. 이 권한을 통해 새로운 메시지, 이벤트, 업데이트 등 중요한 정보를 알림으로 전달합니다. 예를 들어, 새로운 메시지가 도착했을 때 알림창을 통해 알려줍니다.",
            Manifest.permission.POST_NOTIFICATIONS,
            false,"access",0
        )
        perMap[Manifest.permission.PACKAGE_USAGE_STATS] = AxPermissionModel(
            "사용자 기기 상태 접근",
            "앱이 사용자가 설치한 다른 앱들의 사용 시간, 빈도 등의 통계 정보를 수집할 수 있도록 허용하는 권한입니다. 이 권한을 통해 사용자의 앱 사용 패턴을 분석하여 더욱 개인화된 서비스를 제공합니다. 예를 들어, 사용자가 자주 사용하는 앱을 기반으로 맞춤형 추천 기능을 제공할 수 있습니다.",
            Manifest.permission.PACKAGE_USAGE_STATS,
            false,"access",0
        )
        perMap[Manifest.permission.CAMERA] = AxPermissionModel(
            "카메라",
            "앱이 카메라를 사용하여 사진이나 동영상을 촬영하거나 스캔 기능을 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.CAMERA,
            false,"access",0
        )
        perMap[Manifest.permission.WRITE_EXTERNAL_STORAGE] = AxPermissionModel(
            "저장소 쓰기",
            "앱이 사용자의 사진, 동영상, 파일 등을 저장하거나 불러오기 위해 필요한 권한입니다. 예를 들어, 갤러리 앱에서 사진을 편집하거나, 파일 관리 앱에서 파일을 이동할 때 사용됩니다.",
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            false,"access",0
        )
        perMap[Manifest.permission.READ_EXTERNAL_STORAGE] = AxPermissionModel(
            "저장소 읽기",
            "앱이 사용자의 사진, 동영상, 파일 등을 저장하거나 불러오기 위해 필요한 권한입니다. 예를 들어, 갤러리 앱에서 사진을 편집하거나, 파일 관리 앱에서 파일을 이동할 때 사용됩니다.",
            Manifest.permission.READ_EXTERNAL_STORAGE,
            false,"access",0
        )
        perMap[Manifest.permission.READ_MEDIA_IMAGES] = AxPermissionModel(
            "사진 및 동영상",
            "앱이 사용자의 사진이나 동영상 파일을 읽어서 보여주거나 편집하기 위해 필요한 권한입니다.",
            Manifest.permission.READ_MEDIA_IMAGES,
            false,"access",0
        )
        perMap[Manifest.permission.READ_MEDIA_VIDEO] = AxPermissionModel(
            "사진 및 동영상",
            "앱이 사용자의 사진이나 동영상 파일을 읽어서 보여주거나 편집하기 위해 필요한 권한입니다.",
            Manifest.permission.READ_MEDIA_VIDEO,
            false,"access",0
        )
        perMap[Manifest.permission.VIBRATE] = AxPermissionModel(
            "진동 사용",
            "앱이 진동 기능을 사용하여 알림이나 사용자에게 특정 상황을 알리기 위해 필요한 권한입니다.",
            Manifest.permission.VIBRATE,
            false,"access",0
        )
        perMap[Manifest.permission.RECORD_AUDIO] = AxPermissionModel(
            "오디오",
            "앱이 마이크를 사용하여 음성을 녹음하거나 음성 인식 기능을 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.RECORD_AUDIO,
            false,"access",0
        )
        perMap[Manifest.permission.READ_PHONE_NUMBERS] = AxPermissionModel(
            "전화번호 가져오기",
            "앱이 사용자의 전화번호 정보를 읽거나 통화 상태를 확인하여 관련 기능을 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.READ_PHONE_NUMBERS,
            false,"access",0
        )
        perMap[Manifest.permission.READ_PHONE_STATE] = AxPermissionModel(
            "전화번호 정보 읽기",
            "앱이 사용자의 전화번호 정보를 읽거나 통화 상태를 확인하여 관련 기능을 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.READ_PHONE_STATE,
            false,"access",0
        )
        perMap[Manifest.permission.ACCESS_MEDIA_LOCATION] = AxPermissionModel(
            "미디어 위치 접근",
            "앱이 사용자의 정확한 위치 정보를 얻어서 위치 기반 서비스를 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            false,"access",0
        )
        perMap[Manifest.permission.BLUETOOTH_CONNECT] = AxPermissionModel(
            "블루투스 연결",
            "앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.",
            Manifest.permission.BLUETOOTH_CONNECT,
            false,"access",0
        )
        perMap[Manifest.permission.BLUETOOTH_SCAN] = AxPermissionModel(
            "블루투스 스캔",
            "앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.",
            Manifest.permission.BLUETOOTH_SCAN,
            false,"access",0
        )
        // Bluetooth 권한
        perMap[Manifest.permission.BLUETOOTH_ADMIN] = AxPermissionModel(
            "블루투스 연결",
            "앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.",
            Manifest.permission.BLUETOOTH_ADMIN,
            false,
            "access" ,0// 접근 권한
        )
        perMap[Manifest.permission.BLUETOOTH] = AxPermissionModel(
            "블루투스 스캔",
            "앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.",
            Manifest.permission.BLUETOOTH,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.READ_CONTACTS] = AxPermissionModel(
            "연락처 읽기",
            "연락처 정보를 읽기 위해 필요한 권한입니다.",
            Manifest.permission.READ_CONTACTS,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.SEND_SMS] = AxPermissionModel(
            "SMS 전송",
            "앱이 사용자의 기기에서 SMS 메시지를 보낼 수 있도록 허용하는 권한입니다. 예를 들어, 문자 메시지로 인증번호를 받거나 친구에게 메시지를 보낼 수 있습니다.",
            Manifest.permission.SEND_SMS,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.MODIFY_AUDIO_SETTINGS] = AxPermissionModel(
            "오디오 설정 수정",
            "앱이 시스템의 오디오 설정을 변경하기 위해 필요한 권한입니다.",
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.ACCESS_NOTIFICATION_POLICY] = AxPermissionModel(
            "알림 정책 접근",
            "알림 정책에 접근하기 위해 필요한 권한입니다.",
            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.WRITE_SETTINGS] = AxPermissionModel(
            "설정 쓰기",
            "앱이 시스템 알림 설정을 변경하거나 시스템 설정에 접근하여 특정 기능을 수행하기 위해 필요한 권한입니다.\n",
            Manifest.permission.WRITE_SETTINGS,
            false,
            "action",0 // 권한 허용 화면으로 이동
        )
        perMap[Manifest.permission.ACCESS_WIFI_STATE] = AxPermissionModel(
            "WiFi 상태 접근",
            "앱이 Wi-Fi 연결 상태를 확인하여 더욱 안정적인 서비스를 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.ACCESS_WIFI_STATE,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.ACTIVITY_RECOGNITION] = AxPermissionModel(
            "활동 인식",
            "앱이 사용자의 활동을 인식하여 맞춤형 서비스를 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.ACTIVITY_RECOGNITION,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.SET_ALARM] = AxPermissionModel(
            "알람 설정",
            "앱이 사용자 기기에서 알람을 설정하거나 정확한 시간에 작업을 예약할 수 있도록 허용하는 권한입니다. 이 권한을 통해 알람, 타이머, 일정 관리 등의 기능을 제공합니다.",
            Manifest.permission.SET_ALARM,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.SCHEDULE_EXACT_ALARM] = AxPermissionModel(
            "정확한 알람 일정",
            "앱이 사용자 기기에서 알람을 설정하거나 정확한 시간에 작업을 예약할 수 있도록 허용하는 권한입니다. 이 권한을 통해 알람, 타이머, 일정 관리 등의 기능을 제공합니다.",
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            false,
            "access",0 // 접근 권한
        )
        perMap[Manifest.permission.ACCESS_FINE_LOCATION] = AxPermissionModel(
            "위치 정보",
            "앱이 사용자의 정확한 위치 정보를 얻어서 위치 기반 서비스를 제공하기 위해 필요한 권한입니다.",
            Manifest.permission.ACCESS_FINE_LOCATION,
            false,
            "access",0 // 접근 권한
        )
        /*perMap[Manifest.permission.ACCESS_COARSE_LOCATION] = AxPermissionModel(
            "대략적인 위치 정보",
            "이 권한은 사용자의 대략적인 위치를 얻기 위해 필요합니다.",
            Manifest.permission.ACCESS_COARSE_LOCATION,
            false,
            "access" // 접근 권한
        )*/
    }
}
