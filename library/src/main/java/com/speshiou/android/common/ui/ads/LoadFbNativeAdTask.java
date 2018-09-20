package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.speshiou.android.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadFbNativeAdTask extends LoadAdTask {

    private NativeAd mNativeAd;

    public LoadFbNativeAdTask(Context context, AdViewRecycler adViewRecycler, String unitId) {
        super(context, adViewRecycler, unitId);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        NativeAd nativeAd = new NativeAd(mContext, mUnitId);
        nativeAd.setAdListener(new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError error) {
//                    Log.e("joey", "[fb] onError error=" + error.getErrorMessage());
                if (mNativeAd != null && mNativeAd.isAdLoaded()) {
                    onLoaded();
                } else {
                    onFailedToLoad();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mNativeAd != null) {
                    mNativeAd.destroy();
                }
                mNativeAd = (NativeAd) ad;
                if (mNativeAd.isAdLoaded()) {
                    mNativeAd.unregisterView();
                }

                onLoaded();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        // Request an ad
        nativeAd.loadAd();
    }

    @Override
    protected void detachAdView() {
        super.detachAdView();
        if (mNativeAd == null || !mNativeAd.isAdLoaded()) {
            return;
        }

        mNativeAd.unregisterView();
    }

    @Override
    public void attachAdView(ViewGroup adContainer) {
        super.attachAdView(adContainer);

        if (mNativeAd == null || !mNativeAd.isAdLoaded()) {
            return;
        }

        mNativeAd.unregisterView();

        View adView = mAdViewRecycler.obtainAdView(mContext, AdViewType.AD_FB_NATIVE, mUnitId);
        AdViewHolder viewHolder = (AdViewHolder) adView.getTag();

        viewHolder.viewAdTag.setVisibility(View.VISIBLE);
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        // Set the Text.
        viewHolder.viewAdTag.setText(mNativeAd.getSponsoredTranslation());
        viewHolder.textViewTitle.setText(mNativeAd.getAdvertiserName());
        viewHolder.textViewSubtitle.setVisibility(TextUtils.isEmpty(mNativeAd.getAdSocialContext()) ? View.GONE : View.VISIBLE);
        viewHolder.textViewSubtitle.setText(mNativeAd.getAdSocialContext());
        viewHolder.textViewBody.setVisibility(TextUtils.isEmpty(mNativeAd.getAdBodyText()) ? View.GONE : View.VISIBLE);
        viewHolder.textViewBody.setText(mNativeAd.getAdBodyText());
        viewHolder.buttonAction.setVisibility(mNativeAd.hasCallToAction() ? View.VISIBLE : View.GONE);
        viewHolder.buttonAction.setText(mNativeAd.getAdCallToAction());

        // Add the AdChoices icon
        AdChoicesView adChoicesView = new AdChoicesView(mContext, mNativeAd, true);
        viewHolder.adChoicePlaceHolder.removeAllViews();
        viewHolder.adChoicePlaceHolder.addView(adChoicesView);

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adContainer);
        clickableViews.add(viewHolder.buttonAction);
        mNativeAd.registerViewForInteraction(adContainer, nativeAdMedia, nativeAdIcon, clickableViews);

        if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        adContainer.addView(adView);
    }
}
