package com.ax.library.ax_permission.permission

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.model.Permission

@DslMarker
internal annotation class AxPermissionBuilderDsl

@AxPermissionBuilderDsl
public class PermissionBuilder(
    private val context: Context,
) {

    private val permissions = mutableListOf<String>()

    public fun add(
        vararg permissions: String,
        @DrawableRes iconResId: Int? = null,
        @StringRes titleResId: Int? = null,
        @StringRes descriptionResId: Int? = null,
    ): PermissionBuilder = apply {

    }

    public fun addWithStrings(
        vararg permissions: String,
        @DrawableRes iconResId: Int? = null,
        title: String? = null,
        description: String? = null,
    ): PermissionBuilder = apply {

    }

    internal fun build(): List<Permission> {
        TODO("특별 권한인지, 런타임 단일 권한인지, 런타임 권한 그룹인지 구분하여 Permission 객체 생성 후 반환")
    }
}