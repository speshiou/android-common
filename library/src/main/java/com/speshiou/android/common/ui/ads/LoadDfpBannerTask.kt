package com.speshiou.android.common.ui.ads

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView

class LoadDfpBannerTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, private vararg val bannerAdSizes: AdSize) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var mPublisherAdView: PublisherAdView? = null
    var page = 0

    public override fun onLoad() {
//        super.onLoad()

        if (mPublisherAdView != null) {
            return
        }

        val publisherAdView = PublisherAdView(mContext)
        publisherAdView.setAdSizes(*bannerAdSizes)
        publisherAdView.adUnitId = mUnitId

        publisherAdView.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)
                onFailedToLoad()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
//                if (mPublisherAdView != null) {
//                    mPublisherAdView?.destroy()
//                }

//                mPublisherAdView = publisherAdView
//
                onLoaded()
            }
        }
        mPublisherAdView = publisherAdView
//        publisherAdView.loadAd(PublisherAdRequest.Builder().build())
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

            if (isReadyForRefreshing) {
                super.onLoad()
                adView.loadAd(PublisherAdRequest.Builder().build())
            }
        }
    }
}