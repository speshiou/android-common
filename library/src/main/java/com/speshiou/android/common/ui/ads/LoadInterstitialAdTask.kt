package com.speshiou.android.common.ui.ads

import android.content.Context

open class LoadInterstitialAdTask(val context: Context, val adId: String) {

    var listener: InterstitialAdListener? = null

    fun load() {
        onLoad()
    }

    open fun onLoad() {

    }

    open fun destroy() {

    }

    open fun show() {

    }

    open fun isAdInvalidated(): Boolean {
        return true
    }

    open fun isAdLoaded(): Boolean {
        return false
    }
}