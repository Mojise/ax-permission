package com.ax.library.ax_permission.app.aa

import android.os.Build

/**
 * Android 버전별 권한 변경사항 참고사항
 */
internal object PermissionVersionNotes {
    
    /**
     * Android 6.0 (API 23) - Marshmallow
     * - 런타임 권한 모델 도입
     */
    @JvmSynthetic
    internal const val RUNTIME_PERMISSION_INTRODUCED = Build.VERSION_CODES.M
    
    /**
     * Android 9.0 (API 28) - Pie
     * - CALL_LOG 그룹이 PHONE 그룹에서 분리됨
     */
    @JvmSynthetic
    internal const val CALL_LOG_SEPARATED = Build.VERSION_CODES.P
    
    /**
     * Android 10 (API 29) - Q
     * - ACTIVITY_RECOGNITION 추가
     * - ACCESS_BACKGROUND_LOCATION 별도 요청 필요
     * - PROCESS_OUTGOING_CALLS Deprecated
     */
    @JvmSynthetic
    internal const val BACKGROUND_LOCATION_SEPARATED = Build.VERSION_CODES.Q
    
    /**
     * Android 12 (API 31) - S
     * - NEARBY_DEVICES 그룹 추가 (Bluetooth 권한 변경)
     * - 기존 BLUETOOTH, BLUETOOTH_ADMIN은 normal 권한으로 변경됨
     */
    @JvmSynthetic
    internal const val BLUETOOTH_PERMISSIONS_CHANGED = Build.VERSION_CODES.S
    
    /**
     * Android 13 (API 33) - Tiramisu
     * - STORAGE 권한이 세분화됨
     *   (READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO)
     * - POST_NOTIFICATIONS 추가
     * - BODY_SENSORS_BACKGROUND 추가
     */
    @JvmSynthetic
    internal const val STORAGE_GRANULAR_PERMISSIONS = Build.VERSION_CODES.TIRAMISU
}