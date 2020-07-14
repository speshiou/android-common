package com.speshiou.android.common.ext

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_NIGHT_MASK



@ColorInt
fun Context.getStyledColor(@AttrRes attrId: Int): Int {
    val resolvedAttr = TypedValue()
    this.theme.resolveAttribute(attrId, resolvedAttr, true)
    val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
    return ContextCompat.getColor(this, colorRes)
}

fun Context.isNightModeEnabled(): Boolean {
    return this.resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}