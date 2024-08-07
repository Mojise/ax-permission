package kr.co.permission.ax_permission.util

import android.os.Parcel
import android.os.Parcelable

data class AxPermission(
    val permission: String,
    val title:String,
    val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(permission)
        parcel.writeString(title)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AxPermission> {
        override fun createFromParcel(parcel: Parcel): AxPermission {
            return AxPermission(parcel)
        }

        override fun newArray(size: Int): Array<AxPermission?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "AxPermission(permission='$permission', description='$description')"
    }
}

class AxPermissionList() : Parcelable {

    private val permissions = mutableListOf<AxPermission>()

    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(permissions, AxPermission.CREATOR)
    }

    fun add(permission: String) {
        add(permission, "","")
    }

    fun add(permission: String, title:String = "" , description: String = "") {
        permissions.add(AxPermission(permission, title ,description))
    }

    fun getPermissions(): List<AxPermission> {
        return permissions
    }

    fun forEach(action: (AxPermission) -> Unit) {
        permissions.forEach(action)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(permissions)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return permissions.joinToString(separator = "\n") { "Permission: ${it.permission} ,title: ${it.title}, Description: ${it.description}" }
    }

    companion object CREATOR : Parcelable.Creator<AxPermissionList> {
        override fun createFromParcel(parcel: Parcel): AxPermissionList {
            return AxPermissionList(parcel)
        }

        override fun newArray(size: Int): Array<AxPermissionList?> {
            return arrayOfNulls(size)
        }
    }
}