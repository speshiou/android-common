package com.speshiou.android.common.ui.ads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.LoadAdError

class LoadAdMobInterstitialAdTask(context: Context, adType: String, adId: String): LoadInterstitialAdTask(context, adType, adId) {
    private var interstitialAd: InterstitialAd? = null

    override fun onLoad() {
        super.onLoad()
        interstitialAd = InterstitialAd(context)
        interstitialAd?.adUnitId = adId
        interstitialAd?.adListener = object: AdListener() {

            override fun onAdOpened() {
                super.onAdOpened()
                listener?.onAdDisplayed()

                AdCompat.logImpressionEvent(adType, adId)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                listener?.onAdDismissed()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                listener?.onAdClicked()

                AdCompat.logClickEvent(adType, adId)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                listener?.onAdLoaded()
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)
                listener?.onError()
            }
        }

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd?.loadAd(AdRequest.Builder().build())
    }

    override fun show() {
        super.show()
        interstitialAd?.show()
    }

    override fun destroy() {
        super.destroy()
    }

    override fun isAdInvalidated(): Boolean {
        return !isAdLoaded()
    }

    override fun isAdLoaded(): Boolean {
        return interstitialAd?.isLoaded ?: false
    }
}