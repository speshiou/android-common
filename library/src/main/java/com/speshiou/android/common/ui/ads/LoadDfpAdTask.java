package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.speshiou.android.common.R;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.formats.OnPublisherAdViewLoadedListener;
import com.google.android.gms.ads.formats.PublisherAdViewOptions;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadDfpAdTask extends LoadAdTask {

    private AdLoader adLoader;
    private AdSize[] mBannerAdSizes = new AdSize[] { AdSize.LARGE_BANNER };
    private NativeAppInstallAd mNativeAppInstallAd;
    private NativeContentAd mNativeContentAd;
    private PublisherAdView mPublisherAdView;

    public LoadDfpAdTask(Context context, AdViewRecycler adViewRecycler, String unitId, AdSize[] adSizes) {
        super(context, adViewRecycler, unitId);
        mBannerAdSizes = adSizes;
    }

    private void clearAds() {
        if (mNativeAppInstallAd != null) {
            mNativeAppInstallAd.destroy();
            mNativeAppInstallAd = null;
        }
        if (mNativeContentAd != null) {
            mNativeContentAd.destroy();
            mNativeContentAd = null;
        }
        if (mPublisherAdView != null) {
            mPublisherAdView.destroy();
            mPublisherAdView = null;
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (adLoader != null) {
            adLoader.loadAd(new PublisherAdRequest.Builder().build());
            return;
        }

        adLoader = new AdLoader.Builder(mContext, mUnitId)
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
                clearAds();
                mNativeAppInstallAd = appInstallAd;
                onLoaded();
            }
        })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd contentAd) {
                        clearAds();
                        mNativeContentAd = contentAd;
                        onLoaded();
                    }
                })
                .forPublisherAdView(new OnPublisherAdViewLoadedListener() {
                    @Override
                    public void onPublisherAdViewLoaded(PublisherAdView publisherAdView) {
//                        Log.e("test", "[dfp] onPublisherAdViewLoaded");
                        clearAds();
                        mPublisherAdView = publisherAdView;
                        onLoaded();
                    }
                }, mBannerAdSizes)
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
//                        Log.e("test", "[dfp] onAdFailedToLoad errorCode=" + errorCode);
                        if (mNativeAppInstallAd != null || mNativeContentAd != null || mPublisherAdView != null) {
                            onLoaded();
                        } else {
                            onFailedToLoad();
                        }
                    }
                })
                .withPublisherAdViewOptions(new PublisherAdViewOptions.Builder().build())
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();


        // Request an ad
        adLoader.loadAd(new PublisherAdRequest.Builder().build());
    }

    @Override
    public void attachAdView(ViewGroup adContainer) {
        super.attachAdView(adContainer);

        View adView = null;
        if (mNativeAppInstallAd != null) {
            NativeAppInstallAdView nativeAppInstallAdView = (NativeAppInstallAdView) mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_INSTALL, mUnitId);
            populateAppInstallAdView(mNativeAppInstallAd, nativeAppInstallAdView);
            adView = nativeAppInstallAdView;
        } else if (mNativeContentAd != null) {
            NativeContentAdView nativeContentAdView = (NativeContentAdView) mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_CONTENT, mUnitId);
            populateContentAdView(mNativeContentAd, nativeContentAdView);
            adView = nativeContentAdView;
        } else if (mPublisherAdView != null) {
            adView = mPublisherAdView;
        }

        if (adView != null) {
            if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }
}