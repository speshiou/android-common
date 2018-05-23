package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadAdmobNativeAdExpTask extends LoadAdTask {

    private NativeExpressAdView mNativeExpressAdView;
    private AdSize mBannerExpAdSize;

    public LoadAdmobNativeAdExpTask(Context context, AdViewRecycler adViewRecycler, String unitId, AdSize adSize) {
        super(context, adViewRecycler, unitId);
        mBannerExpAdSize = adSize;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        final NativeExpressAdView nativeExpressAdView = new NativeExpressAdView(mContext);
        nativeExpressAdView.setAdSize(mBannerExpAdSize);
        nativeExpressAdView.setAdUnitId(mUnitId);
        nativeExpressAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
//                    Log.e("joey", "[admob exp] onAdFailedToLoad errorCode=" + errorCode);
                if (mNativeExpressAdView != null) {
                    onLoaded();
                } else {
                    onFailedToLoad();
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mNativeExpressAdView != null) {
                    mNativeExpressAdView.destroy();
                }
                mNativeExpressAdView = nativeExpressAdView;
                onLoaded();
            }
        });
        nativeExpressAdView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void attachAdView(final ViewGroup adContainer) {
        super.attachAdView(adContainer);
        if (mNativeExpressAdView == null) {
            return;
        }
        View adView = mNativeExpressAdView;
        if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        adContainer.addView(adView);
    }
}