package com.speshiou.android.common.ui.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class LoadAdMobInterstitialAdTask(context: Context, adType: String, adId: String): LoadInterstitialAdTask(context, adType, adId) {
    private var interstitialAd: InterstitialAd? = null

    override fun onLoad() {
        super.onLoad()
        InterstitialAd.load(context, adId, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                this@LoadAdMobInterstitialAdTask.interstitialAd = interstitialAd
                this@LoadAdMobInterstitialAdTask.onAdLoaded(interstitialAd)
                listener?.onAdLoaded()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                listener?.onError()
            }
        })
    }

    private fun onAdLoaded(interstitialAd: InterstitialAd) {
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                listener?.onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                listener?.onAdDisplayed()
                this@LoadAdMobInterstitialAdTask.interstitialAd = null
            }
        }
    }

    override fun show(activty: Activity) {
        super.show(activty)
        interstitialAd?.show(activty)
    }

    override fun destroy() {
        super.destroy()
    }

    override fun isAdInvalidated(): Boolean {
        return !isAdLoaded()
    }

    override fun isAdLoaded(): Boolean {
        return interstitialAd != null
    }
}