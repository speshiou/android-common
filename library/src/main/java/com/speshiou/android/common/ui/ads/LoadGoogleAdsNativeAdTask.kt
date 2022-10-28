package com.speshiou.android.common.ui.ads

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.ads.mediation.facebook.FacebookAdapter
import com.google.ads.mediation.facebook.FacebookExtras
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.formats.AdManagerAdViewOptions
import com.google.android.gms.ads.formats.OnAdManagerAdViewLoadedListener
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.speshiou.android.common.R

class LoadGoogleAdsNativeAdTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String, private vararg val bannerAdSizes: AdSize) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var _adLoader: AdLoader? = null
    private var _nativeAd: NativeAd? = null
    private var _adManagerAdView: AdManagerAdView? = null
    private var _nativeAds = arrayListOf<NativeAd>()

    var adCount = 1

    private fun clearAds() {
        _adManagerAdView?.destroy()
        _adManagerAdView = null
        _nativeAd?.destroy()
        _nativeAd = null
        for (ad in _nativeAds) {
            ad.destroy()
        }
        _nativeAds.clear()
    }

    public override fun onLoad() {
        super.onLoad()
        if (_adLoader?.isLoading == true) {
            return
        }
        if (_adLoader != null) {
            performLoader()
            return
        }

        val loaderBuilder = AdLoader.Builder(context, unitId)
                .withAdListener(object : com.google.android.gms.ads.AdListener() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        AdCompat.logImpressionEvent(adType, unitId)
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        AdCompat.logClickEvent(adType, unitId)
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        if (_nativeAd != null || _adManagerAdView != null) {
                            onLoaded()
                        } else {
                            onFailedToLoad()
                        }
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                        .setVideoOptions(VideoOptions.Builder().build())
                        .build())
        if (adType != AdType.AD_DFP_BANNER) {
            loaderBuilder.forNativeAd {
                if (adType == AdType.AD_ADMOB_NATIVE) {
                    _nativeAds.add(it)
                } else {
                    clearAds()
                    _nativeAd = it
                }
                onLoaded()
            }
        }
        if (bannerAdSizes.isNotEmpty()) {
            loaderBuilder.forAdManagerAdView(OnAdManagerAdViewLoadedListener {
                clearAds()
                _adManagerAdView = it
                onLoaded()
            }, *bannerAdSizes)
        }
        if (adType == AdType.AD_DFP || adType == AdType.AD_DFP_BANNER) {
            loaderBuilder.withAdManagerAdViewOptions(AdManagerAdViewOptions.Builder().build())
        }
        _adLoader = loaderBuilder.build()

        performLoader()
    }

    private fun performLoader() {
        // Request an ad
        if (adType == AdType.AD_DFP || adType == AdType.AD_DFP_BANNER) {
//            adLoader?.loadAd(PublisherAdRequest.Builder()
//                    .addTestDevice("8A347539F0976701577341ECB483FE19")
//                    .addTestDevice("F31D20B68A1791570F88B6A627A4182E").build())
            _adLoader?.loadAd(AdManagerAdRequest.Builder().build())
        } else {
//            adLoader?.loadAd(AdRequest.Builder()
//                    .addTestDevice("8A347539F0976701577341ECB483FE19")
//                    .addTestDevice("F31D20B68A1791570F88B6A627A4182E").build())
            val extras = FacebookExtras()
                    .setNativeBanner(true)
                    .build()
            _adLoader?.loadAds(AdRequest.Builder().addNetworkExtrasBundle(FacebookAdapter::class.java, extras).build(), adCount)
        }
    }

    public override fun attachAdView(adContainer: ViewGroup, index: Int) {
        super.attachAdView(adContainer, index)

        val nativeAd = getNativeAd(index)
        val publisherAdView = _adManagerAdView
        var adView: View? = null
        if (nativeAd != null) {
            adContainer.removeAllViews()
            val nativeAdView = adViewRecycler.obtainAdView(context, AdViewType.AD_GMA_NATIVE) as? NativeAdView
            if (nativeAdView != null) {
                populateAdView(nativeAd, nativeAdView)
                adView = nativeAdView
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

    private fun getNativeAd(index: Int): NativeAd? {
        if (adType == AdType.AD_ADMOB_NATIVE) {
            if (index < _nativeAds.size && index >= 0) {
                return _nativeAds[index]
            }
        } else {
            return _nativeAd
        }
        return null
    }

    protected fun populateAdView(nativeAd: NativeAd,
                                 adView: NativeAdView) {
        adView.id = AdViewType.AD_GMA_NATIVE
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
        val title = nativeAd.headline
        if (TextUtils.isEmpty(title)) {
            adView.headlineView?.visibility = View.GONE
        } else {
            adView.headlineView?.visibility = View.VISIBLE
            (adView.headlineView as TextView).text = title
            if (adView.mediaView == null) {
//                adView.bodyView?.visibility = View.GONE
            }
        }
        (adView.bodyView as? TextView)?.text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction
        val icon = nativeAd.icon?.drawable
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
        var price = nativeAd.price
        val store = nativeAd.store
        if (store == null) {
//            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as? TextView)?.text = store
        }

        if (nativeAd.starRating == null || nativeAd.starRating == 0.0) {
//            adView.starRatingView?.visibility = View.GONE
        } else {
            if (nativeAd.starRating?.toFloat() == 0f) {
//                adView.starRatingView?.visibility = View.GONE
            } else {
                (adView.starRatingView as? RatingBar)?.rating = nativeAd.starRating?.toFloat() ?: 0f
                adView.starRatingView?.visibility = View.VISIBLE
            }
        }

        val extras = nativeAd.extras
        if (extras != null) {
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
            adView.findViewById<View>(R.id.secondary_line)?.visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }

    override fun recycle() {
        super.recycle()
        clearAds()
    }
}