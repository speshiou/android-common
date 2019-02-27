package com.speshiou.android.common.ui.ads

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView

class LoadDfpBannerTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, val adViewWidth: Int, private vararg val bannerAdSizes: AdSize) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var mPublisherAdView: PublisherAdView? = null

    public override fun onLoad() {
        super.onLoad()
        if (bannerAdSizes.isEmpty() || mPublisherAdView != null || adViewWidth <= 0) {
            return
        }

        val publisherAdView = PublisherAdView(mContext)
        publisherAdView.layoutParams = ViewGroup.LayoutParams(adViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        publisherAdView.setAdSizes(*bannerAdSizes)
        publisherAdView.adUnitId = mUnitId

        publisherAdView.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)
                if (mPublisherAdView != null) {
                    onLoaded()
                } else {
                    onFailedToLoad()
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                if (mPublisherAdView != null) {
                    mPublisherAdView?.destroy()
                }

                mPublisherAdView = publisherAdView
                onLoaded()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                AdCompat.logImpressionEvent(adType, mUnitId)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                AdCompat.logClickEvent(adType, mUnitId)
            }
        }
        publisherAdView.loadAd(PublisherAdRequest.Builder().build())
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)
        if (mPublisherAdView == null) {
            return
        }

        val adView = mPublisherAdView
        if (adView != null) {
            if (adView.parent != null && adView.parent is ViewGroup) {
                (adView.parent as ViewGroup).removeView(adView)
            }
            adContainer.addView(adView)
        }
    }

    override fun recycle() {
        super.recycle()
        mPublisherAdView?.destroy()
    }
}