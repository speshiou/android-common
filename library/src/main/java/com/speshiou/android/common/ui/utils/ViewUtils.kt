package com.speshiou.android.common.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.MenuItem
import android.widget.TextView

class ViewUtils() {
    companion object {
        fun tintMenuItem(menuItem: MenuItem, color: Int) {
            menuItem.icon = tintDrawable(menuItem.icon, color)
        }

        fun tintDrawable(drawable: Drawable, color: Int): Drawable {
            val drawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(drawable, color)
            return drawable
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