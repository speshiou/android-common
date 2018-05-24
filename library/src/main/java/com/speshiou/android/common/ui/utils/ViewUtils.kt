package com.speshiou.android.common.ui.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.MenuItem
import android.widget.TextView

class ViewUtils() {
    companion object {
        fun tintMenuItem(menuItem: MenuItem, color: Int) {
            val drawable = DrawableCompat.wrap(menuItem.icon)
            DrawableCompat.setTint(drawable, color)
            menuItem.icon = drawable
        }

        fun applyCompoundDrawableSize(textView: TextView, size: Int) {
            for (drawable in textView.compoundDrawables) {
                if (drawable != null) {
                    val x = (drawable.intrinsicWidth - size) / 2
                    drawable.setBounds(x, x, size, size)
                }
            }
        }
    }
}