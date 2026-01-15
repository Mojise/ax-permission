package com.ax.library.ax_permission.customview

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.setPadding
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ax.library.ax_permission.R
import com.ax.library.ax_permission.ax.AxPermission
import com.ax.library.ax_permission.util.dp

internal abstract class FloatingBottomSheetDialogFragment<Binding : ViewBinding> : BottomSheetDialogFragment() {

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): Binding

    protected lateinit var binding: Binding
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflateBinding(inflater, container)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBehavior()
    }

    /**
     * BottomSheet Behavior 설정
     *
     * - collapsed 상태를 스킵하고 바로 expanded 상태로 시작
     * - 드래그로 닫을 때 collapsed 없이 바로 hidden으로 전환
     */
    private fun setupBottomSheetBehavior() {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
            BottomSheetBehavior.from(bottomSheet).apply {
                skipCollapsed = true
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun updateBottomSheetDesign() {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            it.setPadding(16.dp)
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
            drawable.cornerRadius = AxPermission.configurations.bottomSheetCornerRadius
        }
    }
}