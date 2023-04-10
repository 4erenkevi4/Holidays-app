package com.elinext.holidays.android.ui

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.graphics.Color

object ProgressBar {
    fun initProgressBar(context: Context): Dialog {
        val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        progressBar.indeterminateDrawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
progressBar.setBackgroundColor(Color.TRANSPARENT)
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)

        val layout = RelativeLayout(context)
        layout.gravity = Gravity.CENTER
        layout.setBackgroundColor(Color.TRANSPARENT)
        layout.addView(progressBar, layoutParams)

        val dialog = Dialog(context, R.style.Widget_ProgressBar)
        dialog.setContentView(layout)
        dialog.setCancelable(true)
        return dialog
    }
}