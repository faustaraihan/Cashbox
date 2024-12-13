package com.cashbox.android.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

@SuppressLint("ClickableViewAccessibility")
object AnimationHelper {
    fun applyTouchAnimation(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> scaleDownAnimation(v)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> scaleUpAnimation(v)
            }
            false
        }
    }

    private fun scaleDownAnimation(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .start()
    }

    private fun scaleUpAnimation(view: View) {
        view.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(100)
            .start()
    }
}