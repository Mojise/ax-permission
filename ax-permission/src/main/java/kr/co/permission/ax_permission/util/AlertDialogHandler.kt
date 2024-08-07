package kr.co.permission.ax_permission.util

import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kr.co.permission.ax_permission.R
import kr.co.simplebestapp.defaultpack.ax_designsystem.interactive.AxInteractiveConstraintLayout

class AlertDialogHandler(private val context: Context) {

    fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        onPositiveClick: ((dialog: AlertDialog) -> Unit)? = null,
        onNegativeClick: ((dialog: AlertDialog) -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(positiveButtonText) { dialogInterface, _ ->
            onPositiveClick?.invoke(dialogInterface as AlertDialog)
        }

        builder.setNegativeButton(negativeButtonText) { dialogInterface, _ ->
            onNegativeClick?.invoke(dialogInterface as AlertDialog)
        }

        val dialog = builder.create()
        dialog.show()

        // Set button text colors programmatically
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val buttonTextColor = if (isDarkMode) {
            ContextCompat.getColor(context, R.color.white)
        } else {
            ContextCompat.getColor(context, R.color.black)
        }

        positiveButton.setTextColor(buttonTextColor)
        negativeButton.setTextColor(buttonTextColor)
    }

    fun showCustomDialog(
        icon:Int,
        content: String,
        onPositiveClick: ((dialog: AlertDialog) -> Unit)? = null,
        onNegativeClick: ((dialog: AlertDialog) -> Unit)? = null
    ) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_permission_item, null)

        val perIcon:ImageView = view.findViewById(R.id.per_Icon)
        val perContent: TextView = view.findViewById(R.id.perContent)
        val positiveButton: AxInteractiveConstraintLayout = view.findViewById(R.id.per_OkBtn)
        val negativeButton: AxInteractiveConstraintLayout = view.findViewById(R.id.per_CancelBtn)

        perIcon.setImageResource(icon)
        perContent.text = content

        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        // 다이얼로그를 하단에 배치하기 위한 Window 속성 설정
        val window = dialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val params = window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = params

        positiveButton.setOnClickListener {
            onPositiveClick?.invoke(dialog)
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            onNegativeClick?.invoke(dialog)
            dialog.dismiss()
        }
    }
}