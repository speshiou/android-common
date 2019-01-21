package com.speshiou.android.common.ui.ads

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.ads.mediation.facebook.FacebookAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.google.android.gms.ads.formats.*
import com.speshiou.android.common.R

class LoadAdmobNativeAdUnifiedTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, private vararg val bannerAdSizes: AdSize) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var adLoader: AdLoader? = null
    private var mUnifiedNativeAd: UnifiedNativeAd? = null
    private var mPublisherAdView: PublisherAdView? = null
    private var mUnifiedNativeAds = arrayListOf<UnifiedNativeAd>()

    var adCount = 1

    private fun clearAds() {
        mPublisherAdView?.destroy()
        mPublisherAdView = null
        mUnifiedNativeAd?.destroy()
        mUnifiedNativeAd = null
        for (ad in mUnifiedNativeAds) {
            ad.destroy()
        }
        mUnifiedNativeAds.clear()
    }

    public override fun onLoad() {
        super.onLoad()
        if (adLoader?.isLoading == true) {
            return
        }
        if (adLoader != null) {
            performLoader()
            return
        }

        val loaderBuilder = AdLoader.Builder(mContext, mUnitId)
                .withAdListener(object : com.google.android.gms.ads.AdListener() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        AdCompat.logImpressionEvent(adType, mUnitId)
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        AdCompat.logClickEvent(adType, mUnitId)
                    }

                    override fun onAdFailedToLoad(errorCode: Int) {
//                        Log.e("joey", "onAdFailedToLoad $errorCode")
                        if (mUnifiedNativeAd != null || mPublisherAdView != null) {
                            onLoaded()
                        } else {
                            onFailedToLoad()
                        }
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
        if (adType != AdType.AD_DFP_BANNER) {
            loaderBuilder.forUnifiedNativeAd {
                unifiedNativeAd ->
                if (adType == AdType.AD_ADMOB_NATIVE) {
                    mUnifiedNativeAds.add(unifiedNativeAd)
                } else {
                    clearAds()
                    mUnifiedNativeAd = unifiedNativeAd
                }
                onLoaded()
            }
        }
        if (bannerAdSizes.isNotEmpty()) {
            loaderBuilder.forPublisherAdView(object : OnPublisherAdViewLoadedListener {
                override fun onPublisherAdViewLoaded(publisherAdView: PublisherAdView?) {
                    clearAds()
                    mPublisherAdView = publisherAdView
                    onLoaded()
                }

            }, *bannerAdSizes)
        }
        if (adType == AdType.AD_DFP || adType == AdType.AD_DFP_BANNER) {
            loaderBuilder.withPublisherAdViewOptions(PublisherAdViewOptions.Builder().build())
        }
        adLoader = loaderBuilder.build()

        performLoader()
    }

    private fun performLoader() {
        // Request an ad
        if (adType == AdType.AD_DFP || adType == AdType.AD_DFP_BANNER) {
//            adLoader?.loadAd(PublisherAdRequest.Builder()
//                    .addTestDevice("8A347539F0976701577341ECB483FE19")
//                    .addTestDevice("F31D20B68A1791570F88B6A627A4182E").build())
            adLoader?.loadAd(PublisherAdRequest.Builder().build())
        } else {
//            adLoader?.loadAd(AdRequest.Builder()
//                    .addTestDevice("8A347539F0976701577341ECB483FE19")
//                    .addTestDevice("F31D20B68A1791570F88B6A627A4182E").build())
            val extras = FacebookAdapter.FacebookExtrasBundleBuilder()
                    .build()
            adLoader?.loadAds(AdRequest.Builder().addNetworkExtrasBundle(FacebookAdapter::class.java, extras).build(), adCount)
        }
    }

    public override fun attachAdView(adContainer: ViewGroup, index: Int) {
        super.attachAdView(adContainer, index)

        val unifiedNativeAd = getUnifiedNativeAd(index)
        val publisherAdView = mPublisherAdView
        var adView: View? = null
        if (unifiedNativeAd != null) {
            adContainer.removeAllViews()
            val unifiedNativeAdView = mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_ADMOB_UNIFIED) as? UnifiedNativeAdView
            if (unifiedNativeAdView != null) {
                populateAdView(unifiedNativeAd, unifiedNativeAdView)
                adView = unifiedNativeAdView
            }
        } else if (publisherAdView != null) {
            adView = publisherAdView
        }
        if (adView != null) {
            if (adView.parent != null && adView.parent is ViewGroup) {
                (adView.parent as ViewGroup).removeView(adView)
            }
            adContainer.addView(adView)
        }
    }

    private fun getUnifiedNativeAd(index: Int): UnifiedNativeAd? {
        if (adType == AdType.AD_ADMOB_NATIVE) {
            if (index < mUnifiedNativeAds.size && index >= 0) {
                return mUnifiedNativeAds[index]
            }
        } else {
            return mUnifiedNativeAd
        }
        return null
    }

    protected fun populateAdView(unifiedNativeAd: UnifiedNativeAd,
                                           adView: UnifiedNativeAdView) {
        adView.id = AdViewType.AD_ADMOB_UNIFIED
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        //                VideoController vc = nativeAppInstallAd.getVideoController();
        //
        //                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        //                // VideoController will call methods on this object when events occur in the video
        //                // lifecycle.
        //                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
        //                    public void onVideoEnd() {
        //                        // Publishers should allow native ads to complete video playback before refreshing
        //                        // or replacing them with another ad in the same UI location.
        //                        mRefresh.setEnabled(true);
        //                        mVideoStatus.setText("Video status: Video playback has ended.");
        //                        super.onVideoEnd();
        //                    }
        //                });

        val viewHolder = adView.tag as AdViewHolder
        adView.headlineView = viewHolder.textViewTitle
        adView.bodyView = viewHolder.textViewBody
        adView.callToActionView = viewHolder.buttonAction
        adView.iconView = viewHolder.imageViewIcon
        adView.priceView = viewHolder.textViewSubtitle
        adView.starRatingView = adView.findViewById(R.id.rating)
        adView.storeView = adView.findViewById(R.id.caption)
        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
        adView.mediaView = adView.findViewById(R.id.media)
        adView.mediaView?.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewRemoved(parent: View?, child: View?) {

            }

            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child != null && child is ImageView) {
                    child.adjustViewBounds = true
                }
            }

        })

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        val title = unifiedNativeAd.headline
        if (TextUtils.isEmpty(title)) {
            adView.headlineView?.visibility = View.GONE
        } else {
            adView.headlineView?.visibility = View.VISIBLE
            (adView.headlineView as TextView).text = title
            if (adView.mediaView == null) {
//                adView.bodyView?.visibility = View.GONE
            }
        }
        (adView.bodyView as? TextView)?.text = unifiedNativeAd.body
        (adView.callToActionView as Button).text = unifiedNativeAd.callToAction
        val icon = unifiedNativeAd.icon?.drawable
        if (icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            adView.iconView?.visibility = View.VISIBLE
            (adView.iconView as ImageView).setImageDrawable(icon)
        }

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        //                if (vc.hasVideoContent()) {
        //                    mVideoStatus.setText(String.format(Locale.getDefault(),
        //                            "Video status: Ad contains a %.2f:1 video asset.",
        //                            vc.getAspectRatio()));
        //                } else {
        //                    mRefresh.setEnabled(true);
        //                    mVideoStatus.setText("Video status: Ad does not contain a video asset.");
        //                }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        var price = unifiedNativeAd.price
        val store = unifiedNativeAd.store
        if (store == null) {
//            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as? TextView)?.text = store
        }

        if (unifiedNativeAd.starRating == null || unifiedNativeAd.starRating == 0.0) {
//            adView.starRatingView?.visibility = View.GONE
        } else {
            if (unifiedNativeAd.starRating?.toFloat() == 0f) {
//                adView.starRatingView?.visibility = View.GONE
            } else {
                (adView.starRatingView as? RatingBar)?.rating = unifiedNativeAd.starRating?.toFloat() ?: 0f
                adView.starRatingView?.visibility = View.VISIBLE
            }
        }

        val extras = unifiedNativeAd.extras
        if (extras != null) {
            if (extras.containsKey(FacebookAdapter.KEY_SUBTITLE_ASSET)) {
                val text = extras.getString(FacebookAdapter.KEY_SUBTITLE_ASSET)
                if (title != text) {
                    adView.bodyView?.visibility = View.VISIBLE
                    (adView.bodyView as? TextView)?.text = text
                }
            }
            if (extras.containsKey(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET)) {
                price = extras.getString(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET)
            }
        }

        if (price == null) {
//            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as? TextView)?.text = price
        }

        if (TextUtils.isEmpty(store) && TextUtils.isEmpty(price)) {
//            adView.findViewById<View>(R.id.secondary_line).visibility = View.GONE
        } else {
            adView.findViewById<View>(R.id.secondary_line).visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(unifiedNativeAd)
    }

    override fun recycle() {
        super.recycle()
        clearAds()
    }
}