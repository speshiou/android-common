package com.speshiou.android.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.speshiou.android.common.R
import kotlinx.android.synthetic.main.empty_view.view.*
import kotlin.properties.Delegates

class EmptyView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.empty_view, this, true)
        action_button.visibility = View.GONE
    }

    var text: String by Delegates.observable("") {
        property, oldValue, newValue ->
        prompt_text.text = newValue
    }

    var iconResId: Int by Delegates.observable(-1) {
        property, oldValue, newValue ->
        if (newValue > 0) {
            icon.setImageResource(iconResId)
        }
    }

    val actionButton by lazy {
        action_button
    }

    var actionButtonEnabled: Boolean by Delegates.observable(false) {
        property, oldValue, newValue ->
        action_button.visibility = if (newValue) View.VISIBLE else View.GONE
    }
}