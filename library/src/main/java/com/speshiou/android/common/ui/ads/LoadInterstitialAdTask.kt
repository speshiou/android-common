package com.speshiou.android.common.ui.ads

import android.app.Activity
import android.content.Context

open class LoadInterstitialAdTask(val context: Context, val adType: String, val adId: String) {

    var listener: InterstitialAdListener? = null

    fun load() {
        onLoad()
    }

    open fun onLoad() {

    }

    open fun destroy() {

    }

    open fun show(activity: Activity) {

    }

    open fun isAdInvalidated(): Boolean {
        return true
    }

    open fun isAdLoaded(): Boolean {
        return false
    }
}