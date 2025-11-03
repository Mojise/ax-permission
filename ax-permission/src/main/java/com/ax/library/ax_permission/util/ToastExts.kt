package com.ax.library.ax_permission.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

private var toast: Toast? = null

internal fun Activity.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, message, duration).apply(Toast::show)
}

internal fun Activity.showToast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, messageResId, duration).apply(Toast::show)
}

internal fun Fragment.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(requireContext(), message, duration).apply(Toast::show)
}

internal fun Fragment.showToast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(requireContext(), messageResId, duration).apply(Toast::show)
}

internal fun Context.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, message, duration).apply(Toast::show)
}

internal fun Context.showToast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, messageResId, duration).apply(Toast::show)
}