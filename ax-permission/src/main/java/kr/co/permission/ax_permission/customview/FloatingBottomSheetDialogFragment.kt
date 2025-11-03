package kr.co.permission.ax_permission.customview

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ax.library.ax_permission.R
import kr.co.permission.ax_permission.util.dp

internal abstract class FloatingBottomSheetDialogFragment<Binding : ViewDataBinding> : BottomSheetDialogFragment() {

    @get:LayoutRes
    protected abstract val layoutResId: Int

    protected lateinit var binding: Binding
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                updateBottomSheetDesign()
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        updateNavigationBarColor()
        updateBottomSheetBackground(binding.root)

        return binding.root
    }


    private fun updateBottomSheetDesign() {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            it.setPadding(10.dp)
        }
    }

    private fun updateNavigationBarColor() {
        dialog?.window?.let {
            it.navigationBarColor = requireContext().getColor(R.color.ax_permission_floating_bottom_sheet_outside_color)
            WindowInsetsControllerCompat(it, it.decorView).isAppearanceLightNavigationBars = false
        }
    }

    private fun updateBottomSheetBackground(contentRootLayout: View) {
        contentRootLayout.background = GradientDrawable().also { drawable ->
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.setColor(requireContext().getColor(R.color.ax_permission_floating_bottom_sheet_background_color_dialog))
            drawable.cornerRadius = 12f.dp
        }
    }
}