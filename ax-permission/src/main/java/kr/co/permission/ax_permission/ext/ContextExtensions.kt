package kr.co.permission.ax_permission.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

internal fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

internal fun Context.showToast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageResId, duration).show()
}