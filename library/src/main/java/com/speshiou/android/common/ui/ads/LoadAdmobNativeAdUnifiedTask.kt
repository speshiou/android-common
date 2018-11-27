package com.speshiou.android.common.ui.ads

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.ads.mediation.facebook.FacebookAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.*
import com.speshiou.android.common.R

class LoadAdmobNativeAdUnifiedTask(context: Context, adViewRecycler: AdViewRecycler, adType: String, unitId: String) : LoadAdTask(context, adViewRecycler, adType, unitId) {

    private var adLoader: AdLoader? = null
    private var mUnifiedNativeAd: UnifiedNativeAd? = null

    private fun clearAds() {
        mUnifiedNativeAd?.destroy()
        mUnifiedNativeAd = null
    }

    public override fun onLoad() {
        super.onLoad()
        if (adLoader?.isLoading == true) {
            return
        }
        if (adLoader != null) {
            adLoader?.loadAd(AdRequest.Builder().build())
            return
        }

        adLoader = AdLoader.Builder(mContext, mUnitId)
                .forUnifiedNativeAd {
                    unifiedNativeAd ->
                    clearAds()
                    mUnifiedNativeAd = unifiedNativeAd
                    onLoaded()
                }
                .withAdListener(object : com.google.android.gms.ads.AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        if (mUnifiedNativeAd != null) {
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
                .build()


        // Request an ad
        //        adLoader.loadAd(new AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE232").build());
        adLoader?.loadAd(AdRequest.Builder().build())
    }

    public override fun attachAdView(adContainer: ViewGroup) {
        super.attachAdView(adContainer)

        val unifiedNativeAd = mUnifiedNativeAd
        if (unifiedNativeAd != null) {
            adContainer.removeAllViews()
            val adView = mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_ADMOB_UNIFIED, mUnitId) as? UnifiedNativeAdView
            if (adView != null) {
                populateAdView(unifiedNativeAd, adView)

                if (adView.parent != null && adView.parent is ViewGroup) {
                    (adView.parent as ViewGroup).removeView(adView)
                }
                adContainer.addView(adView)
            }
        }
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

        adView.headlineView = adView.findViewById(R.id.title)
        adView.bodyView = adView.findViewById(R.id.body)
        adView.callToActionView = adView.findViewById(R.id.button_action)
        adView.iconView = adView.findViewById(R.id.icon)
        adView.priceView = adView.findViewById(R.id.subtitle)
        adView.starRatingView = adView.findViewById(R.id.rating)
        adView.storeView = adView.findViewById(R.id.caption)

        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
        //                MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
        //                adView.setMediaView(mediaView);

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        val title = unifiedNativeAd.headline as String
        (adView.headlineView as TextView).text = title
        //        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        (adView.bodyView as TextView).text = ""
        (adView.callToActionView as Button).text = unifiedNativeAd.callToAction
        (adView.iconView as ImageView).setImageDrawable(
                unifiedNativeAd.icon.drawable)

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
        val price = unifiedNativeAd.price as String
        if (price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = price
        }

        val store = unifiedNativeAd.store as String
        if (store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = store
        }

        if (TextUtils.isEmpty(store) && TextUtils.isEmpty(price)) {
            adView.findViewById<View>(R.id.secondary_line).visibility = View.GONE
        } else {
            adView.findViewById<View>(R.id.secondary_line).visibility = View.VISIBLE
        }

        if (unifiedNativeAd.starRating == null || unifiedNativeAd.starRating == 0.0) {
            adView.starRatingView.visibility = View.GONE
        } else {
            if (unifiedNativeAd.starRating?.toFloat() == 0f) {
                adView.starRatingView.visibility = View.GONE
            } else {
                (adView.starRatingView as RatingBar).rating = unifiedNativeAd.starRating?.toFloat() ?: 0f
                adView.starRatingView.visibility = View.VISIBLE
            }
        }

        val extras = unifiedNativeAd.extras
        if (extras != null) {
            if (extras.containsKey(FacebookAdapter.KEY_SUBTITLE_ASSET)) {
                val text = extras.get(FacebookAdapter.KEY_SUBTITLE_ASSET) as String
                if (title != text) {
                    (adView.bodyView as TextView).text = text
                }
            }
            //            if (extras.containsKey(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET)) {
            //                String text = (String) extras.get(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET);
            //                ((TextView) adView.getPriceView()).setText(text);
            //            }
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(unifiedNativeAd)
    }
}