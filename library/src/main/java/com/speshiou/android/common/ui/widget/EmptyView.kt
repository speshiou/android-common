package com.speshiou.android.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.speshiou.android.common.R
import com.speshiou.android.common.databinding.EmptyViewBinding
import kotlin.properties.Delegates

class EmptyView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    private val binding: EmptyViewBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.empty_view, this, true)
        binding = EmptyViewBinding.inflate(LayoutInflater.from(context), this)
        binding.actionButton.visibility = View.GONE

        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.EmptyView,
                    0, 0
            )
            try {
                val d = a.getDrawable(R.styleable.EmptyView_emptyIcon)
                binding.icon.setImageDrawable(d)
                val text = a.getString(R.styleable.EmptyView_emptyText)
                binding.promptText.text = text
            } finally { // release the TypedArray so that it can be reused.
                a.recycle()
            }
        }
    }

    var text: CharSequence by Delegates.observable<CharSequence>("") {
        property, oldValue, newValue ->
        binding.promptText.text = newValue
    }

    var iconResId: Int by Delegates.observable(-1) {
        property, oldValue, newValue ->
        if (newValue > 0) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(iconResId)
        } else {
            binding.icon.visibility = View.GONE
        }
    }

    val actionButton by lazy {
        binding.actionButton
    }

    var actionButtonEnabled: Boolean by Delegates.observable(false) {
        property, oldValue, newValue ->
        binding.actionButton.visibility = if (newValue) View.VISIBLE else View.GONE
    }
}