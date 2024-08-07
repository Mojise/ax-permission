package kr.co.permission.ax_permission.listener

import android.os.Parcelable

interface AxPermissionListener{
    fun onPermissionGranted()
    fun onPermissionDenied()
}