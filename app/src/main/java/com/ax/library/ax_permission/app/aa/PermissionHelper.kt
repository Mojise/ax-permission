package com.ax.library.ax_permission.app.aa

import android.Manifest
import android.os.Build

// presentation/permission/PermissionHelper.kt
internal object PermissionHelper {
    
    /**
     * 위치 권한 요청
     * Android 버전에 따라 적절한 권한 목록 반환
     */
    @JvmSynthetic
    fun getLocationPermissions(includeBackground: Boolean = false): List<String> {
        val permissions = mutableListOf<String>()
        permissions.addAll(RuntimePermissionGroups.LOCATION.filter { 
            it != Manifest.permission.ACCESS_BACKGROUND_LOCATION
        })
        
        // 백그라운드 위치는 별도로 요청해야 함 (Android 10+)
        if (includeBackground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        
        return permissions
    }
    
    /**
     * 저장소 권한 요청
     * Android 13 이상에서는 세분화된 권한 사용
     */
    @JvmSynthetic
    fun getStoragePermissions(
        needImages: Boolean = false,
        needVideos: Boolean = false,
        needAudio: Boolean = false
    ): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ : 세분화된 미디어 권한
            buildList {
                if (needImages) add(Manifest.permission.READ_MEDIA_IMAGES)
                if (needVideos) add(Manifest.permission.READ_MEDIA_VIDEO)
                if (needAudio) add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            // Android 12 이하: 레거시 저장소 권한
            RuntimePermissionGroups.STORAGE_LEGACY
        }
    }
    
    /**
     * Bluetooth 권한 요청
     * Android 12 이상에서는 NEARBY_DEVICES 그룹 사용
     */
    @JvmSynthetic
    fun getBluetoothPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+: NEARBY_DEVICES 그룹
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            // Android 11 이하: Normal 권한 (자동 승인)
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }
}