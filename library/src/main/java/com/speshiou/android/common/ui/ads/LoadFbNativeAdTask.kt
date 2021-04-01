package com.speshiou.android.common.ui.ads

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.facebook.ads.*
import com.speshiou.android.common.R
import com.speshiou.android.common.ui.BaseApplication
import java.util.ArrayList

class LoadFbNativeAdTask(context: Context, adViewRecycler: AdViewRecycler, val fbAdType: String, unitId: String) : LoadAdTask(context, adViewRecycler, fbAdType, unitId) {

    private var mNativeAd: NativeAdBase? = null

    override fun onLoad() {
        super.onLoad()
        val nativeAd: NativeAdBase = when (this.adType) {
            AdType.AD_FB_NATIVE -> NativeAd(context, unitId)
            else -> NativeBannerAd(context, unitId)
        }
        nativeAd.setAdListener(object : NativeAdListener {

            override fun onMediaDownloaded(ad: Ad) {

            }

            override fun onError(ad: Ad, error: AdError) {
                //                    Log.e("joey", "[fb] onError error=" + error.getErrorMessage());
                if (mNativeAd != null && mNativeAd?.isAdLoaded == true) {
                    onLoaded()
                } else {
                    onFailedToLoad()
                }

                val bundle = Bundle()
                bundle.putString("ad_type", fbAdType)
                bundle.putString("ad_id", unitId)
                bundle.putString("ad_error_code", error.errorCode.toString())
                BaseApplication.firebaseAnalytics?.logEvent("ad_error", bundle)
            }

            override fun onAdLoaded(ad: Ad) {
                mNativeAd?.destroy()
                mNativeAd = ad as NativeAdBase
                if (ad.isAdLoaded) {
                    ad.unregisterView()
                }

                onLoaded()
            }

            override fun onAdClicked(ad: Ad) {
                AdCompat.logClickEvent(adType, unitId)
            }

            override fun onLoggingImpression(ad: Ad) {
                AdCompat.logImpressionEvent(adType, unitId)
            }
        })

        // Request an ad
        nativeAd.loadAd()
    }

    override fun detachAdView() {
        super.detachAdView()
        if (mNativeAd == null || mNativeAd?.isAdLoaded == false) {
            return
        }

        mNativeAd?.unregisterView()
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)

        if (mNativeAd == null || mNativeAd?.isAdLoaded == false) {
            return
        }

        val ad = mNativeAd ?: return
        val adViewType = when (adType) {
            AdType.AD_FB_NATIVE -> {
                AdViewType.AD_FB_NATIVE
            } else -> {
                AdViewType.AD_FB_NATIVE_BANNER
            }
        }
        val adView = adViewRecycler.obtainAdView(context, adViewType) ?: return
        ad.unregisterView()


        val viewHolder = adView.tag as AdViewHolder

        viewHolder.viewAdTag?.visibility = View.VISIBLE
        val nativeAdIcon = adView.findViewById<AdIconView>(R.id.native_ad_icon)
        val nativeAdMedia = adView.findViewById<MediaView>(R.id.media)
        // Set the Text.
        viewHolder.viewAdTag?.text = ad.sponsoredTranslation
        viewHolder.textViewTitle?.text = ad.advertiserName
        viewHolder.textViewSubtitle?.visibility = if (TextUtils.isEmpty(ad.adSocialContext)) View.GONE else View.VISIBLE
        viewHolder.textViewSubtitle?.text = ad.adSocialContext
        viewHolder.textViewBody?.visibility = if (TextUtils.isEmpty(ad.adBodyText)) View.GONE else View.VISIBLE
        viewHolder.textViewBody?.text = ad.adBodyText
        viewHolder.buttonAction?.visibility = if (ad.hasCallToAction()) View.VISIBLE else View.GONE
        viewHolder.buttonAction?.text = ad.adCallToAction

        // Add the AdOptionsView
        val adOptionsView = AdOptionsView(context, ad, adView.findViewById(R.id.native_ad_layout))
        viewHolder.adOptionsPlaceHolder?.removeAllViews()
        viewHolder.adOptionsPlaceHolder?.addView(adOptionsView)

        // Register the Title and CTA button to listen for clicks.
        val clickableViews = ArrayList<View>()
        clickableViews.add(adContainer)
        viewHolder.buttonAction?.let {
            clickableViews.add(it)
        }
        if (ad is NativeAd) {
            ad.registerViewForInteraction(adContainer, nativeAdMedia, nativeAdIcon, clickableViews)
        } else if (ad is NativeBannerAd) {
            ad.registerViewForInteraction(adContainer, nativeAdIcon, clickableViews)
        }

        if (adView.parent != null && adView.parent is ViewGroup) {
            (adView.parent as ViewGroup).removeView(adView)
        }
        adContainer.addView(adView)
    }
}