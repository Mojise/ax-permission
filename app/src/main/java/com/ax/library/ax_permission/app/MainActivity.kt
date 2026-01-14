package com.ax.library.ax_permission.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ax.library.ax_permission.app.util.overrideCloseActivityTransitionCompat
import com.ax.library.ax_permission.app.util.overrideOpenActivityTransitionCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overrideOpenActivityTransitionCompat()
        setContentView(R.layout.activity_main)
    }

    override fun finish() {
        super.finish()
        overrideCloseActivityTransitionCompat()
    }
}