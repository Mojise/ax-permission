package kr.co.permission.ax_permission.util

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
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.ax_permission_move_right_in_activity_for_starting,
                R.anim.ax_permission_move_left_out_activity_for_starting,
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
                R.anim.ax_permission_move_right_in_activity_for_starting,
                R.anim.ax_permission_move_left_out_activity_for_starting,
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