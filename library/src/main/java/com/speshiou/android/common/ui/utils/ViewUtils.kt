package com.speshiou.android.common.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.graphics.drawable.DrawableCompat
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import kotlin.math.abs


class ViewUtils() {
    companion object {
        fun getStyledColor(context: Context, attrId: Int): Int {
            val typedValue = TypedValue()

            val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(attrId))
            val color = a.getColor(0, 0)
            a.recycle()
            return color
        }

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

        fun disallowInterceptTouchEvent(recyclerView: RecyclerView) {
            recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                var startX: Float = -1f
                var startY: Float = -1f
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val action = e.action
                    when (action) {
                        MotionEvent.ACTION_DOWN ->  {
                            startX = e.x
                            startY = e.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (abs(e.x - startX) > abs(e.y - startY)) {
                                rv.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            rv.parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }
            })
        }

        fun measureTextViewHeight(
                text: CharSequence,
                textSize: Int, // in pixels
                deviceWidth: Int, // in pixels
                padding: Int // in pixels
        ): Int {
            val myTextPaint = TextPaint()
            myTextPaint.isAntiAlias = true
            // this is how you would convert sp to pixels based on screen density
            //myTextPaint.setTextSize(16 * context.getResources().getDisplayMetrics().density);
            myTextPaint.textSize = textSize.toFloat()
            val alignment = Layout.Alignment.ALIGN_NORMAL
            val spacingMultiplier = 1f
            val spacingAddition = padding.toFloat() // optionally apply padding here
            val includePadding = padding != 0
            val myStaticLayout = StaticLayout(text, myTextPaint, deviceWidth, alignment, spacingMultiplier, spacingAddition, includePadding)
            return myStaticLayout.height
        }
    }
}