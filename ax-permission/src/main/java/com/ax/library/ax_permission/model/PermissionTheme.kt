package com.ax.library.ax_permission.model

import java.io.Serializable

internal enum class PermissionTheme : Serializable {
    Day,
    Night,
    DayNight;

    companion object {

        internal val Default = DayNight
    }
}