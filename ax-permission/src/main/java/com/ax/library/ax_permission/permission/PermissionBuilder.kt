package com.ax.library.ax_permission.permission

public interface PermissionBuilder {

    public fun add(permission: String)

    public fun addGroup(vararg permissions: String)

    public fun String.withFoo(): String {
        TODO()
    }
}