package com.ax.library.ax_permission.app.aa

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Android 런타임 권한 그룹
 *
 * ⚠️ 주의: 이 그룹핑은 Android 버전에 따라 변경될 수 있습니다.
 * 항상 개별 권한을 명시적으로 체크하고 요청해야 합니다.
 *
 * 마지막 업데이트: Android 14 (API 34) 기준
 */
internal object RuntimePermissionGroups {

    /**
     * 1. CALENDAR (캘린더)
     * 캘린더 이벤트 읽기/쓰기
     */
    @JvmSynthetic
    internal val CALENDAR = listOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    /**
     * 2. CALL_LOG (통화 기록)
     * Android 9 (API 28)부터 PHONE 그룹에서 분리됨
     */
    @JvmSynthetic
    internal val CALL_LOG = listOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.PROCESS_OUTGOING_CALLS  // Deprecated in API 29
    )

    /**
     * 3. CAMERA (카메라)
     */
    @JvmSynthetic
    internal val CAMERA = listOf(
        Manifest.permission.CAMERA
    )

    /**
     * 4. CONTACTS (연락처)
     */
    @JvmSynthetic
    internal val CONTACTS = listOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS
    )

    /**
     * 5. LOCATION (위치)
     */
    @JvmSynthetic
    internal val LOCATION = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION  // API 29+, 별도 요청 필요
    )

    /**
     * 6. MICROPHONE (마이크)
     */
    @JvmSynthetic
    internal val MICROPHONE = listOf(
        Manifest.permission.RECORD_AUDIO
    )

    /**
     * 7. PHONE (전화)
     */
    @JvmSynthetic
    internal val PHONE = listOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_PHONE_NUMBERS,  // API 26+
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS,  // API 26+
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.USE_SIP,
        Manifest.permission.ACCEPT_HANDOVER  // API 28+
    )

    /**
     * 8. SENSORS (센서)
     */
    @JvmSynthetic
    internal val SENSORS = listOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.BODY_SENSORS_BACKGROUND  // API 33+
    )

    /**
     * 9. ACTIVITY_RECOGNITION (활동 인식)
     * Android 10 (API 29)부터 추가됨
     */
    @JvmSynthetic
    internal val ACTIVITY_RECOGNITION = listOf(
        Manifest.permission.ACTIVITY_RECOGNITION  // API 29+
    )

    /**
     * 10. SMS (문자 메시지)
     */
    @JvmSynthetic
    internal val SMS = listOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS
    )

    /**
     * 11. STORAGE (저장소)
     * Android 13 (API 33)부터 세분화됨
     */
    @JvmSynthetic
    internal val STORAGE_LEGACY = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,   // API 32 이하
        Manifest.permission.WRITE_EXTERNAL_STORAGE   // API 32 이하
    )

    /**
     * 12. READ_MEDIA_IMAGES (이미지)
     * Android 13 (API 33)부터 STORAGE 대신 사용
     */
    @JvmSynthetic
    internal val READ_MEDIA_IMAGES = listOf(
        Manifest.permission.READ_MEDIA_IMAGES  // API 33+
    )

    /**
     * 13. READ_MEDIA_VIDEO (비디오)
     * Android 13 (API 33)부터 STORAGE 대신 사용
     */
    @JvmSynthetic
    internal val READ_MEDIA_VIDEO = listOf(
        Manifest.permission.READ_MEDIA_VIDEO  // API 33+
    )

    /**
     * 14. READ_MEDIA_AUDIO (오디오)
     * Android 13 (API 33)부터 STORAGE 대신 사용
     */
    @JvmSynthetic
    internal val READ_MEDIA_AUDIO = listOf(
        Manifest.permission.READ_MEDIA_AUDIO  // API 33+
    )

    /**
     * 15. NEARBY_DEVICES (근처 기기)
     * Android 12 (API 31)부터 Bluetooth 관련 권한이 이 그룹으로 이동
     */
    @JvmSynthetic
    @RequiresApi(Build.VERSION_CODES.S)
    internal val NEARBY_DEVICES = listOf(
        Manifest.permission.BLUETOOTH_SCAN,      // API 31+
        Manifest.permission.BLUETOOTH_CONNECT,   // API 31+
        Manifest.permission.BLUETOOTH_ADVERTISE, // API 31+
        Manifest.permission.UWB_RANGING          // API 31+
    )

    /**
     * 16. NOTIFICATIONS (알림)
     * Android 13 (API 33)부터 추가됨
     */
    @JvmSynthetic
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    internal val NOTIFICATIONS = listOf(
        Manifest.permission.POST_NOTIFICATIONS  // API 33+
    )
}