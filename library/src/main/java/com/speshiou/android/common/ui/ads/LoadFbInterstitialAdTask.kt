package com.speshiou.android.common.ui.ads

import android.app.Activity
import android.content.Context
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener

class LoadFbInterstitialAdTask(context: Context, adType: String, adId: String): LoadInterstitialAdTask(context, adType, adId) {

    private var interstitialAd: InterstitialAd? = null

    override fun onLoad() {
        super.onLoad()
        val interstitialAd = InterstitialAd(context, adId)
        val listener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                listener?.onAdDisplayed()

                AdCompat.logImpressionEvent(adType, adId)
            }

            override fun onInterstitialDismissed(ad: Ad) {
                listener?.onAdDismissed()
            }

            override fun onError(ad: Ad, adError: AdError) {
                listener?.onError()
            }

            override fun onAdLoaded(ad: Ad) {
                listener?.onAdLoaded()
            }

            override fun onAdClicked(ad: Ad) {
                listener?.onAdClicked()

                AdCompat.logClickEvent(adType, adId)
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
            }
        }

        this.interstitialAd = interstitialAd

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                .withAdListener(listener)
                .build())
    }

    override fun show(activity: Activity) {
        super.show(activity)
        interstitialAd?.show()
    }

    override fun destroy() {
        super.destroy()
        interstitialAd?.destroy()
    }

    override fun isAdInvalidated(): Boolean {
        return interstitialAd?.isAdInvalidated ?: true
    }

    override fun isAdLoaded(): Boolean {
        return interstitialAd?.isAdLoaded ?: false
    }
}