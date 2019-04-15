package com.speshiou.android.common.ui.widget

import android.content.Context
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View


class ScrollAwareFABBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    override fun onStartNestedScroll(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                axes, type)
    }

    override fun onNestedScroll(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyConsumed > 0) {
            val layoutParams = child.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            Log.e("joey", "child.animation ${child.animation}")
            child.animate().translationY((layoutParams.bottomMargin + child.height).toFloat()).start()
        } else if (dyConsumed < 0) {
            child.animate().translationY(0f).start()
        }
    }

}