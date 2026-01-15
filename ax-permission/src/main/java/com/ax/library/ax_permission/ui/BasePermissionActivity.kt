package com.ax.library.ax_permission.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.ax.library.ax_permission.util.overrideCloseActivityTransitionCompat
import com.ax.library.ax_permission.util.overrideOpenActivityTransitionCompat

internal abstract class BasePermissionActivity<Binding : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: Binding
        private set

    protected abstract fun inflateBinding(inflater: LayoutInflater): Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate() :: savedInstanceState=$savedInstanceState")
        overrideOpenActivityTransitionCompat()
        enableEdgeToEdge()

        binding = inflateBinding(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun finish() {
        super.finish()
        overrideCloseActivityTransitionCompat()
    }

    override fun onStart() {
        super.onStart()
        //Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        //Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        //Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        //Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.e(TAG, "onDestroy() :: isFinishing=$isFinishing")
    }

    companion object {
        internal const val TAG = "PermissionActivity.kt"
    }
}