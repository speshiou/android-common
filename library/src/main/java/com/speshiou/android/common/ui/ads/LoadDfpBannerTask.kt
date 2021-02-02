package com.speshiou.android.common.ui.ads

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView

class LoadDfpBannerTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, val adViewWidth: Int, private vararg val bannerAdSizes: AdSize) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var _adManagerAdView: AdManagerAdView? = null

    public override fun onLoad() {
        super.onLoad()
        if (bannerAdSizes.isEmpty() || _adManagerAdView != null || adViewWidth <= 0) {
            return
        }

        val publisherAdView = AdManagerAdView(context)
        publisherAdView.layoutParams = ViewGroup.LayoutParams(adViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        publisherAdView.setAdSizes(*bannerAdSizes)
        publisherAdView.adUnitId = unitId

        publisherAdView.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)
                if (_adManagerAdView != null) {
                    onLoaded()
                } else {
                    onFailedToLoad()
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                if (_adManagerAdView != null) {
                    _adManagerAdView?.destroy()
                }

                _adManagerAdView = publisherAdView
                onLoaded()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                AdCompat.logImpressionEvent(adType, unitId)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                AdCompat.logClickEvent(adType, unitId)
            }
        }
        publisherAdView.loadAd(AdManagerAdRequest.Builder().build())
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)
        if (_adManagerAdView == null) {
            return
        }

        val adView = _adManagerAdView
        if (adView != null) {
            if (adView.parent != null && adView.parent is ViewGroup) {
                (adView.parent as ViewGroup).removeView(adView)
            }
            adContainer.addView(adView)
        }
    }

    override fun recycle() {
        super.recycle()
        _adManagerAdView?.destroy()
    }
}