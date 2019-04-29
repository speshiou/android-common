package com.speshiou.android.common.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        fun tintCompoundDrawable(textView: TextView, color: Int) {
            for (drawable in textView.compoundDrawables) {
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, color)
                }
            }
        }

        fun applyCompoundDrawableSize(textView: TextView, size: Int) {
            for (drawable in textView.compoundDrawables) {
                if (drawable != null) {
                    val x = (drawable.intrinsicWidth - size) / 2
                    drawable.setBounds(x, x, size, size)
                }
            }
        }

        fun showSoftKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}