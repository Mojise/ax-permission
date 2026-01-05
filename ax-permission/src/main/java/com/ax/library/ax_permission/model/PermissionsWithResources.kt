package com.ax.library.ax_permission.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.Serializable

/**
 * ### 리소스 아이디들이 추가된 권한 객체
 */
public sealed interface PermissionsWithResources : PermissionFoo, Serializable {

    @get:DrawableRes
    public val iconResId: Int?

    @get:StringRes
    public val titleResId: Int

    @get:StringRes
    public val descriptionResId: Int

    public data class Special(
        val permission: Permission.Special,

        @field:DrawableRes
        override val iconResId: Int?,
        @field:StringRes
        override val titleResId: Int,
        @field:StringRes
        override val descriptionResId: Int,
    ) : PermissionsWithResources, Serializable

    public data class Runtime(
        val permissions: List<Permission.Runtime>,

        @field:DrawableRes
        override val iconResId: Int?,
        @field:StringRes
        override val titleResId: Int,
        @field:StringRes
        override val descriptionResId: Int,
    ) : PermissionsWithResources, Serializable
}

/**
 * ### 권한에 리소스 아이디들을 추가하여 [PermissionsWithResources] 객체로 변환합니다.
 */
@JvmSynthetic
internal fun Permission.withResourcesInternal(
    @DrawableRes iconResId: Int? = null,
    @StringRes titleResId: Int? = null,
    @StringRes descriptionResId: Int? = null,
): PermissionsWithResources {
    
    val resources = getDefaultResources()

    return when (this) {
        is Permission.Special -> PermissionsWithResources.Special(
            permission = this,
            iconResId = iconResId ?: resources.iconResId,
            titleResId = titleResId ?: resources.titleResId,
            descriptionResId = descriptionResId ?: resources.descriptionResId,
        )
        is Permission.Runtime -> PermissionsWithResources.Runtime(
            permissions = listOf(this),
            iconResId = iconResId ?: resources.iconResId,
            titleResId = titleResId ?: resources.titleResId,
            descriptionResId = descriptionResId ?: resources.descriptionResId,
        )
    }
}

/**
 * ### 권한 목록에 리소스 아이디들을 추가하여 [PermissionsWithResources] 객체로 변환합니다.
 */
@JvmSynthetic
internal fun PermissionRuntimeGroup.withResourcesInternal(
    @DrawableRes iconResId: Int? = null,
    @StringRes titleResId: Int? = null,
    @StringRes descriptionResId: Int? = null,
): PermissionsWithResources.Runtime {

    return when {
        iconResId != null && titleResId != null && descriptionResId != null -> {
            PermissionsWithResources.Runtime(
                permissions = permissions,
                iconResId = iconResId,
                titleResId = titleResId,
                descriptionResId = descriptionResId,
            )
        }
        else -> {
            val resources = getDefaultResourcesOrThrow()

            PermissionsWithResources.Runtime(
                permissions = permissions,
                iconResId = iconResId ?: resources.iconResId,
                titleResId = titleResId ?: resources.titleResId,
                descriptionResId = descriptionResId ?: resources.descriptionResId,
            )
        }
    }
}