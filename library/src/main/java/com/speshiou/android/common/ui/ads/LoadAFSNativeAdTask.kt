package com.speshiou.android.common.ui.ads

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.afsn.AdListener
import com.google.android.gms.ads.afsn.SearchAdController
import com.google.android.gms.ads.afsn.search.SearchAdOptions
import com.google.android.gms.ads.afsn.search.SearchAdRequest
import com.speshiou.android.common.R


class LoadAFSNativeAdTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, var keyword: String?) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var adController: SearchAdController? = null
    private var mAdView: View? = null

    public override fun onLoad() {
        super.onLoad()

        val keyword = keyword
        if (keyword == null || TextUtils.isEmpty(keyword)) {
            onFailedToLoad()
            return
        }

        val unitIds = unitId.split("/")
        val clientId = unitIds[0]
        val channelId = unitIds[1]
        val styleId = unitIds[2]

        val adOptionsBuilder = SearchAdOptions.Builder()
        adOptionsBuilder.setAdType(SearchAdOptions.AD_TYPE_TEXT)
        adOptionsBuilder.setPrefetch(true)
        adOptionsBuilder.setNumAdsRequested(3)
        adOptionsBuilder.setChannel(channelId)
        // Provide a callback to trigger when ads are loaded.
        val adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                if (mAdView != null) {
                    onLoaded()
                } else {
                    onFailedToLoad()
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                val adView = mAdView
                if (adView != null) {
                    (adView.parent  as ViewGroup).removeView(adView)
                }

                mAdView = adController?.createAdView()

                onLoaded()
            }

            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                AdCompat.logClickEvent(adType, unitId)
            }
        }

        adController = SearchAdController(context, clientId, styleId,
                adOptionsBuilder.build(), adListener)
        val requestBuilder = SearchAdRequest.Builder()
        requestBuilder.setQuery(keyword)

        adController?.loadAds(requestBuilder.build())
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)
        val adView = mAdView ?: return
        if (adView.parent != null && adView.parent is ViewGroup) {
            (adView.parent as ViewGroup).removeView(adView)
        }
        adContainer.addView(adView)
        var lp = adView.layoutParams
        if (lp == null) {
            lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        }
        if (lp is ViewGroup.MarginLayoutParams) {
            val offset = adView.context.resources.getDimensionPixelSize(R.dimen.csa_offset)
            lp.setMargins(offset, 0, 0, 0)
            adView.layoutParams = lp
        }
        adController?.populateAdView(adView, "demoAd")
    }
}