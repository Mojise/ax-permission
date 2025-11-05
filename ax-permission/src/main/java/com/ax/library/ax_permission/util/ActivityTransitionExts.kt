package com.ax.library.ax_permission.util

import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.os.Build
import com.ax.library.ax_permission.R

/**
 * 액티비티 전환 애니메이션 설정 (Open).
 */
internal fun Activity.overrideOpenActivityTransitionCompat() {
    when {
        Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            // Open: 이 Activity가 시작될 때
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.ax_permission_move_right_in_activity_for_starting,
                R.anim.ax_permission_move_left_out_activity_for_starting,
            )
            // Close: 다른 Activity(Settings)가 닫히고 이 Activity로 돌아올 때
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.ax_permission_move_left_in_activity_for_finishing,
                R.anim.ax_permission_move_right_out_activity_for_finishing,
            )
        }
        Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            @Suppress("DEPRECATION")
            overridePendingTransition(
                R.anim.ax_permission_move_right_in_activity_for_starting,
                R.anim.ax_permission_move_left_out_activity_for_starting,
            )
        }
        else -> { /* No Transition */ }
    }
}

/**
 * 액티비티 전환 애니메이션 설정 (Close).
 */
internal fun Activity.overrideCloseActivityTransitionCompat() {
    when {
        Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.ax_permission_move_left_in_activity_for_finishing,
                R.anim.ax_permission_move_right_out_activity_for_finishing,
            )
        }
        Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            @Suppress("DEPRECATION")
            overridePendingTransition(
                R.anim.ax_permission_move_left_in_activity_for_finishing,
                R.anim.ax_permission_move_right_out_activity_for_finishing,
            )
        }
        else -> { /* No Transition */ }
    }
}