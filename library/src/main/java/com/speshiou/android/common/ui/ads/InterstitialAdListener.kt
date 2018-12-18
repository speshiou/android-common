package com.speshiou.android.common.ui.ads

interface InterstitialAdListener {
    fun onAdLoaded()
    fun onAdDismissed()
    fun onAdClicked()
    fun onAdDisplayed()
    fun onError()
}