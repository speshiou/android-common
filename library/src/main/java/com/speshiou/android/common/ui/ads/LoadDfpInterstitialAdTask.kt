package com.speshiou.android.common.ui.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd

class LoadDfpInterstitialAdTask(context: Context, adType: String, adId: String): LoadInterstitialAdTask(context, adType, adId) {
    private var interstitialAd: AdManagerInterstitialAd? = null

    override fun onLoad() {
        super.onLoad()
        AdManagerInterstitialAd.load(context, adId, AdManagerAdRequest.Builder().build(), object : AdManagerInterstitialAdLoadCallback() {
            override fun onAdLoaded(adManagerInterstitialAd: AdManagerInterstitialAd) {
                super.onAdLoaded(adManagerInterstitialAd)
                interstitialAd = adManagerInterstitialAd
                this@LoadDfpInterstitialAdTask.onAdLoaded(adManagerInterstitialAd)
                listener?.onAdLoaded()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
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
                this@LoadDfpInterstitialAdTask.interstitialAd = null
            }
        }
    }

    override fun show(activity: Activity) {
        super.show(activity)
        interstitialAd?.show(activity)
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