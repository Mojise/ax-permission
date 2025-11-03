package kr.co.permission.ax_permission.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.ax.library.ax_permission.R
import kr.co.permission.ax_permission.customview.FloatingBottomSheetDialogFragment
import com.ax.library.ax_permission.databinding.FragmentAxPermissionCommonDialogBinding

internal class PermissionExitBottomSheet : FloatingBottomSheetDialogFragment<FragmentAxPermissionCommonDialogBinding>() {

    override val layoutResId: Int = R.layout.fragment_ax_permission_common_dialog

    private var callback: Callback? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            tvTitle.text = getString(R.string.ax_permission_exit_bottom_sheet_title)
            tvDescription.text = getString(R.string.ax_permission_exit_bottom_sheet_message)
            btnPrimary.text = getString(R.string.ax_permission_exit_bottom_sheet_button_text_continue)
            btnSecondary.text = getString(R.string.ax_permission_exit_bottom_sheet_button_text_exit)

            btnPrimary.setOnClickListener {
                callback?.onContinueButtonClicked()
                dismiss()
            }
            btnSecondary.setOnClickListener {
                callback?.onExitButtonClicked()
                dismiss()
            }
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    internal interface Callback {
        fun onExitButtonClicked()
        fun onContinueButtonClicked()
    }

    companion object {

        internal const val TAG = "PermissionExitBottomSheet"

        fun show(fragmentManager: FragmentManager): PermissionExitBottomSheet {
            return PermissionExitBottomSheet().apply {
                show(fragmentManager, TAG)
            }
        }
    }
}