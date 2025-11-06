package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

public data class PermissionsWithResources(
    val permissions: List<Permission2>,

    @field:DrawableRes
    val iconResId: Int?,
    @field:StringRes
    val titleResId: Int,
    @field:StringRes
    val descriptionResId: Int,
) : PermissionFoo

/**
 * ### 권한에 리소스 아이디들을 추가하여 [PermissionsWithResources] 객체로 변환합니다.
 */
@JvmSynthetic
internal fun Permission2.withResourcesInternal(
    @DrawableRes iconResId: Int? = null,
    @StringRes titleResId: Int? = null,
    @StringRes descriptionResId: Int? = null,
): PermissionsWithResources {
    
    val resources = getDefaultResources()
    
    return PermissionsWithResources(
        permissions = listOf(this),
        iconResId = iconResId ?: resources.iconResId,
        titleResId = titleResId ?: resources.titleResId,
        descriptionResId = descriptionResId ?: resources.descriptionResId,
    )
}

/**
 * ### 권한 목록에 리소스 아이디들을 추가하여 [PermissionsWithResources] 객체로 변환합니다.
 */
@JvmSynthetic
internal fun PermissionGroup.withResourcesInternal(
    @DrawableRes iconResId: Int? = null,
    @StringRes titleResId: Int? = null,
    @StringRes descriptionResId: Int? = null,
): PermissionsWithResources {

    return when {
        iconResId != null && titleResId != null && descriptionResId != null -> {
            PermissionsWithResources(
                permissions = permissions,
                iconResId = iconResId,
                titleResId = titleResId,
                descriptionResId = descriptionResId,
            )
        }
        else -> {
            val resources = getDefaultResourcesOrThrow()

            PermissionsWithResources(
                permissions = permissions,
                iconResId = resources.iconResId,
                titleResId = resources.titleResId,
                descriptionResId = resources.descriptionResId,
            )
        }
    }
}