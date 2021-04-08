package com.speshiou.android.common.ui.ads

interface AdTaskListener {
    fun onAdLoaded()
    fun onAdFailedToLoad(errorCode: Int)
}