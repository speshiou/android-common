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

    public static final int AD_REFRESH_RATE = -1;

    protected Context mContext;
    protected AdViewRecycler mAdViewRecycler;
    protected String mUnitId;
    protected long mRefreshRateMillis = -1;
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
        setRefreshRate(AD_REFRESH_RATE);
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
        if (mLoadedTime == 0) {
            return true;
        }
        if (getRefreshRate() <= 0) {
            return false;
        }
        return System.currentTimeMillis() - mLoadedTime > getRefreshRate();
    }

    public void setRefreshRate(int refreshRate) {
        if (refreshRate <= 0) {
            mRefreshRateMillis = -1;
        } else {
            mRefreshRateMillis = refreshRate * 1000;
        }
    }

    protected long getRefreshRate() {
        return mRefreshRateMillis;
    }

    public void setAdContainer(ViewGroup adContainer) {
        setAdContainer(adContainer, -1);
    }

    public void setAdContainer(ViewGroup adContainer, int index) {
        mAdContainer = adContainer;
        if (mAdContainer != null) {
            mAdContainer.setTag(this);
            mAdContainer.removeAllViews();
            if (!isLoading() && !mFailedInLoadingAd) {
                if (index == -1) {
                    attachAdView(mAdContainer);
                } else {
                    attachAdView(adContainer, index);
                }
            }
        } else {
            detachAdView();
        }
    }

    public ViewGroup getAdContainer() {
        return mAdContainer;
    }

    protected void attachAdView(ViewGroup adContainer) {
        attachAdView(adContainer, 0);
    }

    protected void attachAdView(ViewGroup adContainer, int index) {

    }

    protected void detachAdView() {

    }

    public void recycle() {

    }
}
