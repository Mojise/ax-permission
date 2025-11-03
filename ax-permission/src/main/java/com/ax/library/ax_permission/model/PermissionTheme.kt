package com.ax.library.ax_permission.model

import java.io.Serializable

internal enum class PermissionTheme : Serializable {
    Day,
    Night,
    DayAndNight;

    companion object {

        internal val Default = DayAndNight
    }
}