package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadDfpBannerTask extends LoadAdTask {

    private PublisherAdView mPublisherAdView;
    private AdSize[] mBannerAdSizes;

    public LoadDfpBannerTask(Context context, AdViewRecycler adViewRecycler, String unitId, AdSize[] adSizes) {
        super(context, adViewRecycler, unitId);
        mBannerAdSizes = adSizes;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        final PublisherAdView publisherAdView = new PublisherAdView(mContext);
        publisherAdView.setAdSizes(mBannerAdSizes);
        publisherAdView.setAdUnitId(mUnitId);
        publisherAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
//                    Log.e("joey", "[admob exp] onAdFailedToLoad errorCode=" + errorCode);
                if (mPublisherAdView != null) {
                    onLoaded();
                } else {
                    onFailedToLoad();
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mPublisherAdView != null) {
                    mPublisherAdView.destroy();
                }
                mPublisherAdView = publisherAdView;
                onLoaded();
            }
        });
        publisherAdView.loadAd(new PublisherAdRequest.Builder().build());
    }

    @Override
    public void attachAdView(final ViewGroup adContainer) {
        super.attachAdView(adContainer);
        if (mPublisherAdView == null) {
            return;
        }
        View adView = mPublisherAdView;
        if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        adContainer.addView(adView);
    }
}