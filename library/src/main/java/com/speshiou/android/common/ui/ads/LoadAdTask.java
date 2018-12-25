package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.ads.mediation.facebook.FacebookAdapter;
import com.speshiou.android.common.R;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.List;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadAdTask implements Runnable {

    public static final int AD_REFRESH_RATE = 60;

    protected Context mContext;
    protected AdViewRecycler mAdViewRecycler;
    protected String mUnitId;
    protected long mRefreshRateMillis = AD_REFRESH_RATE * 1000;
    protected ViewGroup mAdContainer;
    private boolean mLoading = false;
    private boolean mFailedInLoadingAd = false;
    private boolean mInitialized = false;
    private long mLoadedTime;
    String adType;

    public LoadAdTask(Context context, AdViewRecycler adViewRecycler, String adType, String unitId) {
        mContext = context;
        mAdViewRecycler = adViewRecycler;
        mUnitId = unitId;
        this.adType = adType;
    }

    @Override
    public void run() {
        if (!mInitialized || isReadyForRefreshing() || mFailedInLoadingAd) {
            mInitialized = true;
            if (!TextUtils.isEmpty(mUnitId)) {
                onLoad();
            }
        }
    }

    protected void onLoad() {
        mLoadedTime = System.currentTimeMillis();
        mLoading = true;
        mFailedInLoadingAd = false;
    }

    protected void onLoaded() {
        mLoading = false;
        mFailedInLoadingAd = false;
        if (mAdContainer != null && mAdContainer.getTag() != null && equals(mAdContainer.getTag())) {
            setAdContainer(mAdContainer);
        }
    }

    protected void onFailedToLoad() {
        mLoading = false;
        mFailedInLoadingAd = true;
//            setAdContainer(mAdContainer);
    }

    protected boolean isLoading() {
        return mLoading;
    }

    protected boolean isFailedInLoadingAd() {
        return mFailedInLoadingAd;
    }

    public void markAsReadyForRefreshing() {
        mLoadedTime = 0;
    }

    protected boolean isReadyForRefreshing() {
        return System.currentTimeMillis() - mLoadedTime > getRefreshRate();
    }

    public void setRefreshRate(int refreshRate) {
        mRefreshRateMillis = (refreshRate <= 0 ? AD_REFRESH_RATE : refreshRate) * 1000;
    }

    protected long getRefreshRate() {
        return mRefreshRateMillis;
    }

    public void setAdContainer(ViewGroup adContainer) {
        mAdContainer = adContainer;
        if (mAdContainer != null) {
            mAdContainer.setTag(this);
            mAdContainer.removeAllViews();
            if (!isLoading() && !mFailedInLoadingAd) {
                attachAdView(adContainer);
            }
        } else {
            detachAdView();
        }
    }

    public ViewGroup getAdContainer() {
        return mAdContainer;
    }

    protected void attachAdView(ViewGroup adContainer) {

    }

    protected void detachAdView() {

    }
}
