package com.speshiou.android.common.ui.ads

import android.content.Context

class InterstitialAdManager {
    companion object {
        fun createLoadAdTask(context: Context, adType: String, unitId: String): LoadInterstitialAdTask? {
            var task: LoadInterstitialAdTask? = null
            if (unitId.trim().isNotBlank()) {
                if (adType == InterstitialAdType.FB) {
                    task = LoadFbInterstitialAdTask(context, adType, unitId)
                } else if (adType == InterstitialAdType.ADMOB) {
                    task = LoadAdMobInterstitialAdTask(context, adType, unitId)
                } else if (adType == InterstitialAdType.DFP) {
                    task = LoadDfpInterstitialAdTask(context, adType, unitId)
                }
            }
            return task
        }
    }
}