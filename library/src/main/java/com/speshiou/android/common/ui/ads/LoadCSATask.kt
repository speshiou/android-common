package com.speshiou.android.common.ui.ads

import android.content.Context
import android.text.TextUtils
import android.view.ViewGroup
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.search.DynamicHeightSearchAdRequest
import com.google.android.gms.ads.search.SearchAdView

/**
 * Created by joey on 2018/1/10.
 */
class LoadCSATask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, var keyword: String?) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var searchAdView: SearchAdView? = null
    var page = 0
    var clientId = ""
    var channelId = ""
    var styleId = ""

    public override fun onLoad() {
        super.onLoad()

        if (TextUtils.isEmpty(keyword)) {
            onFailedToLoad()
            return
        }

        if (searchAdView == null) {
            searchAdView = SearchAdView(mContext)
            searchAdView?.adSize = AdSize.SEARCH
            val unitIds = mUnitId.split("/")
            clientId = unitIds[0]
            channelId = unitIds[1]
            styleId = unitIds[2]
            searchAdView?.adUnitId = clientId

            searchAdView?.adListener = object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    super.onAdFailedToLoad(errorCode)
//                if (mSearchAdView != null) {
//                    onLoaded()
//                } else {
//                    onFailedToLoad()
//                }
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()

//                if (mSearchAdView != null) {
//                    mSearchAdView?.destroy()
//                }
//
//                mSearchAdView = searchAdView
//
//                onLoaded()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    AdCompat.logClickEvent(adType, mUnitId)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    AdCompat.logImpressionEvent(adType, mUnitId)
                }
            }
        }

        onLoaded()
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)
        searchAdView?.let {
            adContainer.addView(it)
            val builder = DynamicHeightSearchAdRequest.Builder()
            builder.setQuery(keyword)
            builder.setChannel(channelId)
            builder.setNumber(1)
            if (page > 0) {
                builder.setPage(page)
            }
            builder.setAdvancedOptionValue("csa_styleId", styleId)
            it.loadAd(builder.build())
        }
    }
}