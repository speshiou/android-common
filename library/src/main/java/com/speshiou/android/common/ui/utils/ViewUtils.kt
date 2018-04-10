package com.speshiou.android.common.ui.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.MenuItem

class ViewUtils() {
    companion object {
        fun tintMenuItem(context: Context, menuItem: MenuItem, color: Int) {
            val drawable = DrawableCompat.wrap(menuItem.icon)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, android.R.color.white))
            menuItem.icon = drawable
        }
    }
}