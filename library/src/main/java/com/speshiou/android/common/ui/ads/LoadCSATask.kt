package com.speshiou.android.common.ui.ads

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.search.DynamicHeightSearchAdRequest
import com.google.android.gms.ads.search.SearchAdView
import com.speshiou.android.common.R
import com.speshiou.android.common.ui.ads.AdViewRecycler
import com.speshiou.android.common.ui.ads.LoadAdTask

/**
 * Created by joey on 2018/1/10.
 */
class LoadCSATask(context: Context, adViewRecycler: AdViewRecycler, unitId: String, var keyword: String?) : LoadAdTask(context, adViewRecycler, unitId) {

    private var mSearchAdView: SearchAdView? = null
    private var mAdViewContainer: View? = null

    var adViewDecoratorResIs: Int = -1

    public override fun onLoad() {
        super.onLoad()

        if (TextUtils.isEmpty(keyword)) {
            onFailedToLoad()
            return
        }

        val searchAdView = SearchAdView(mContext)
        searchAdView.adSize = AdSize.SEARCH
        val unitIds = mUnitId.split("/")
        if (unitIds.isNotEmpty()) {
            searchAdView.adUnitId = unitIds[0]
        }
        searchAdView.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)
                if (mSearchAdView != null) {
                    onLoaded()
                } else {
                    onFailedToLoad()
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                if (mSearchAdView != null) {
                    mSearchAdView?.destroy()
                }
                mAdViewContainer = if (adViewDecoratorResIs != -1) {
                    LayoutInflater.from(searchAdView.context).inflate(adViewDecoratorResIs, null, false)
                } else {
                    null
                }

                mSearchAdView = searchAdView
                val adViewContainer = mAdViewContainer
                if (adViewContainer != null) {
                    val vg: ViewGroup = adViewContainer.findViewById(R.id.ad_view_container)
                    vg.addView(searchAdView)
                }
                onLoaded()
            }
        }
        val builder = DynamicHeightSearchAdRequest.Builder()
        builder.setQuery(keyword)
        if (unitIds.size > 1) {
            builder.setChannel(unitIds[1])
        }
        // Customization options (set using setters on
        // DynamicHeightSearchAdRequest.Builder)
        builder.setNumber(1)
        builder.setCssWidth(300)     // Equivalent to "width" CSA parameter
        builder.setIsSellerRatingsEnabled(true)
        builder.setIsTitleBold(true)
        builder.setFontSizeTitle(16)
        builder.setColorTitleLink("#3B5998")
        builder.setColorText("#333333")
//        builder.setLongerHeadlines(false)
        builder.setAdvancedOptionValue("longerHeadlines", "false")
        builder.setIsLocationEnabled(false)
//        builder.setIsSiteLinksEnabled(false)
        builder.setAdvancedOptionValue("siteLinks", "false")
        builder.setIsSellerRatingsEnabled(false)
        builder.setDetailedAttribution(false)
        builder.setAdvancedOptionValue("domainLinkAboveDescription", "false")
        builder.setColorDomainLink("#9197A3")
        builder.setIsSiteLinksEnabled(true)
        builder.setIsClickToCallEnabled(false)
        searchAdView.loadAd(builder.build())
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)
        if (mSearchAdView == null) {
            return
        }
        val adView = if (mAdViewContainer == null) mSearchAdView else mAdViewContainer
        if (adView?.parent != null && adView.parent is ViewGroup) {
            (adView.parent as ViewGroup).removeView(adView)
        }
        adContainer.addView(adView)
    }
}