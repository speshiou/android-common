package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdSize;
import com.speshiou.android.common.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joey on 2017/9/6.
 */

public class AdViewRecycler {

    private Context mContext;

    private HashMap<Integer, ArrayList<View>> mPool = new HashMap<>();
    private HashMap<String, LoadAdTask> mLoadAdTasks = new HashMap<>();
    private HashMap<Integer, ArrayList<LoadAdTask>> mLoadAdTaskMap = new HashMap<>();
//    private HashMap<ViewGroup, LoadAdTask> mLoadAdTaskMap = new HashMap<>();

    private AdSize[] mBannerAdSizes = new AdSize[] { AdSize.LARGE_BANNER };
    private AdSize mBannerExpAdSize = AdSize.LARGE_BANNER;
    private String mKeyword;
    private int mNativeAdLayoutResId = R.layout.ad_s;
    private int mInstallAdLayoutResId = R.layout.ad_install;
    private int mContentAdLayoutResId = R.layout.ad_content;
    int fbNativeAdLayoutResId = R.layout.ad_fb_native;

    public View.OnClickListener onClickRemoveAdButtonListener;

    public AdViewRecycler(Context context) {
        mContext = context;
    }

    public void setBannerAdSize(AdSize... adSizes) {
        mBannerAdSizes = adSizes;
    }

    public void setNativeExpAdSize(AdSize adSize) {
        mBannerExpAdSize = adSize;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }

    public void setNativeAdLayoutResId(int layoutResId) {
        mNativeAdLayoutResId = layoutResId;
    }

    public void setInstallAdLayoutResId(int layoutResId) {
        mInstallAdLayoutResId = layoutResId;
    }

    public void setContentAdLayoutResId(int layoutResId) {
        mContentAdLayoutResId = layoutResId;
    }

    public LoadAdTask createLoadAdTask(int adType, String unitId) {
        LoadAdTask task = null;
        if (adType == AdType.AD_FB_NATIVE) {
            task = new LoadFbNativeAdTask(mContext, this, unitId);
        } else if (adType == AdType.AD_DFP) {
            task = new LoadDfpAdTask(mContext, this, unitId, mBannerAdSizes);
        } else if (adType == AdType.AD_ADMOB_NATIVE_ADV) {
            task = new LoadAdmobNativeAdAdvTask(mContext, this, unitId);
        } else if (adType == AdType.AD_ADMOB_NATIVE_EXP) {
            task = new LoadAdmobNativeAdExpTask(mContext, this, unitId, mBannerExpAdSize);
        } else if (adType == AdType.AD_DFP_BANNER) {
            task = new LoadDfpBannerTask(mContext, this, unitId, mBannerAdSizes);
        } else if (adType == AdType.AD_CSA) {
            task = new LoadCSATask(mContext, this, unitId, mKeyword);
        }
        return task;
    }

    public LoadAdTask obtainAdTask(int adType, String unitId, int refreshRate) {
        ArrayList<LoadAdTask> tasks = mLoadAdTaskMap.get(adType);
        if (tasks == null) {
            tasks = new ArrayList<>();
            mLoadAdTaskMap.put(adType, tasks);
        }
        LoadAdTask reusedTask = null;
        for (LoadAdTask task : tasks) {
            if (task.mUnitId.equals(unitId)) {
                reusedTask = task;
                break;
            }
        }
        if (reusedTask == null) {
            reusedTask = createLoadAdTask(adType, unitId);
        } else {
            tasks.remove(reusedTask);
        }
        reusedTask.setRefreshRate(refreshRate);
        if (reusedTask instanceof LoadCSATask) {
            ((LoadCSATask) reusedTask).setKeyword(mKeyword);
        }
        return reusedTask;
    }

    public void recycleAdTask(int adType, LoadAdTask task) {
        ArrayList<LoadAdTask> tasks = mLoadAdTaskMap.get(adType);
        if (tasks == null) {
            tasks = new ArrayList<>();
            mLoadAdTaskMap.put(adType, tasks);
        }
        tasks.add(task);
    }

    public void loadAd(ViewGroup adContainer, int adType, String unitId) {
        LoadAdTask task = mLoadAdTasks.get(unitId);
        if (task == null) {
            task = createLoadAdTask(adType, unitId);
        }
//
//        LoadAdTask boundTaskMap = mLoadAdTaskMap.get(adContainer);
//        if (boundTaskMap != null && boundTaskMap.getAdContainer() != null && boundTaskMap.getAdContainer().equals(adContainer)) {
//            boundTaskMap.setAdContainer(null);
//        }
//        mLoadAdTaskMap.put(adContainer, task);
//        mLoadAdTasks.put(unitId, task);
//        task.run();
//        task.setAdContainer(adContainer);
    }

    public View obtainAdView(Context context, final int adViewType, String unitId) {
        ArrayList<View> views = mPool.get(adViewType);
        if (views == null) {
            views = new ArrayList<>();
            mPool.put(adViewType, views);
        }
        View adView = null;
        for (View view : views) {
            if (view.getParent() == null) {
                adView = view;
                break;
            }
        }
        if (adView == null) {
            int layoutId = -1;
            switch (adViewType) {
                case AdViewType.AD_NATIVE:
                    layoutId = mNativeAdLayoutResId;
                    break;
                case AdViewType.AD_INSTALL:
                    layoutId = mInstallAdLayoutResId;
                    break;
                case AdViewType.AD_CONTENT:
                    layoutId = mContentAdLayoutResId;
                    break;
                case AdViewType.AD_FB_NATIVE:
                    layoutId = fbNativeAdLayoutResId;
                    break;
            }
            if (layoutId != -1) {
                adView = LayoutInflater.from(context).inflate(layoutId, null, false);
                AdViewHolder viewHolder = new AdViewHolder(adView);
                adView.setTag(viewHolder);
            }
            views.add(adView);
        }
        if (adView != null) {
            AdViewHolder viewHolder = (AdViewHolder) adView.getTag();
            if (viewHolder != null) {
                viewHolder.resetViews();
                if (viewHolder.buttonRemoveAd != null) {
                    viewHolder.buttonRemoveAd.setOnClickListener(onClickRemoveAdButtonListener);
                }
            }
        }
        return adView;
    }
}
