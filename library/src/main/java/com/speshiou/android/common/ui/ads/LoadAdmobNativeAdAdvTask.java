package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.speshiou.android.common.R;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadAdmobNativeAdAdvTask extends LoadAdTask {

    private AdLoader adLoader;
    private NativeAppInstallAd mNativeAppInstallAd;
    private NativeContentAd mNativeContentAd;

    public LoadAdmobNativeAdAdvTask(Context context, AdViewRecycler adViewRecycler, String adType, String unitId) {
        super(context, adViewRecycler, adType, unitId);
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
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (adLoader != null) {
            adLoader.loadAd(new AdRequest.Builder().build());
            return;
        }

        adLoader = new AdLoader.Builder(mContext, mUnitId).forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
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
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
//                    Log.e("joey", "[admob adv]onAdFailedToLoad errorCode=" + errorCode);
                        if (mNativeAppInstallAd != null || mNativeContentAd != null) {
                            onLoaded();
                        } else {
                            onFailedToLoad();
                        }
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();


        // Request an ad
//        adLoader.loadAd(new AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE232").build());
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void attachAdView(ViewGroup adContainer) {
        super.attachAdView(adContainer);

        View adView = null;
        if (mNativeAppInstallAd != null) {
            NativeAppInstallAdView nativeAppInstallAdView = (NativeAppInstallAdView) mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_INSTALL, mUnitId);
            populateAppInstallAdView(mNativeAppInstallAd, nativeAppInstallAdView);
            adView = nativeAppInstallAdView;
        }

        if (mNativeContentAd != null) {
            NativeContentAdView nativeContentAdView = (NativeContentAdView) mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_CONTENT, mUnitId);
            populateContentAdView(mNativeContentAd, nativeContentAdView);
            adView = nativeContentAdView;
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