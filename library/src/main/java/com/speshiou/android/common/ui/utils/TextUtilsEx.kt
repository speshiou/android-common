package com.speshiou.android.common.ui.utils

import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View

class TextUtilsEx {
    companion object {
        fun delegateLinks(c: CharSequence, delegate: (url: String) -> Unit): CharSequence {
            val ssb = SpannableStringBuilder(c)
            val urls = ssb.getSpans(0, c.length, URLSpan::class.java)
            for (span in urls) {
                val start = ssb.getSpanStart(span)
                val end = ssb.getSpanEnd(span)
                val flags = ssb.getSpanFlags(span)
                val clickable = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        delegate.invoke(span.url)
                    }
                }
                ssb.setSpan(clickable, start, end, flags)
                ssb.removeSpan(span)
            }
            return ssb
        }
    }
}