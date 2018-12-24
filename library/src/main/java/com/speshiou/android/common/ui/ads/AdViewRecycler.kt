package com.speshiou.android.common.ui.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdSize
import com.speshiou.android.common.R
import java.util.ArrayList
import java.util.HashMap

class AdViewRecycler(private val mContext: Context) {

    private val mPool = HashMap<Int, ArrayList<View>>()
    private val mLoadAdTasks = HashMap<String, LoadAdTask>()
    private val mLoadAdTaskMap = HashMap<String, ArrayList<LoadAdTask>>()
    //    private HashMap<ViewGroup, LoadAdTask> mLoadAdTaskMap = new HashMap<>();

    var bannerAdSizes = arrayOf(AdSize.LARGE_BANNER)
    private var mBannerExpAdSize = AdSize.LARGE_BANNER
    var keyword: String? = null
    private var mNativeAdLayoutResId = R.layout.ad_s
    var admobUnifiedAdLayoutResId = R.layout.admob_ad_unified
    var fbNativeAdLayoutResId = R.layout.ad_fb_native
    var fbNativeBannerAdLayoutResId = R.layout.ad_fb_native_banner

    var onClickRemoveAdButtonListener: View.OnClickListener? = null

    fun setNativeExpAdSize(adSize: AdSize) {
        mBannerExpAdSize = adSize
    }

    fun setNativeAdLayoutResId(layoutResId: Int) {
        mNativeAdLayoutResId = layoutResId
    }

    private fun createLoadAdTask(adType: String, unitId: String): LoadAdTask? {
        var task: LoadAdTask? = null
        if (adType == AdType.AD_FB_NATIVE) {
            task = LoadFbNativeAdTask(mContext, this, adType, unitId)
        } else if (adType == AdType.AD_FB_NATIVE_BANNER) {
            task = LoadFbNativeAdTask(mContext, this, adType, unitId)
        } else if ((adType == AdType.AD_DFP || adType == AdType.AD_DFP_BANNER) && !bannerAdSizes.contains(AdSize.FLUID)) {
            task = LoadAdmobNativeAdUnifiedTask(mContext, this, adType, unitId, *bannerAdSizes)
        } else if (adType == AdType.AD_DFP_BANNER) {
            task = LoadDfpBannerTask(mContext, this, adType, unitId, *bannerAdSizes)
        } else if (adType == AdType.AD_ADMOB_NATIVE) {
            task = LoadAdmobNativeAdUnifiedTask(mContext, this, adType, unitId)
        } else if (adType == AdType.AD_CSA) {
            task = LoadCSATask(mContext, this, adType, unitId, keyword)
        }
        return task
    }

    fun obtainAdTask(adType: String, unitId: String, refreshRate: Int): LoadAdTask? {
        var tasks: ArrayList<LoadAdTask>? = mLoadAdTaskMap[adType]
        if (tasks == null) {
            tasks = ArrayList()
            mLoadAdTaskMap[adType] = tasks
        }
        var reusedTask: LoadAdTask? = null
        for (task in tasks) {
            if (task.mUnitId == unitId) {
                reusedTask = task
                break
            }
        }
        if (reusedTask == null) {
            reusedTask = createLoadAdTask(adType, unitId)
        } else {
            tasks.remove(reusedTask)
        }
        reusedTask?.setRefreshRate(refreshRate)
        if (reusedTask is LoadCSATask) {
            reusedTask.keyword = keyword
        }
        return reusedTask
    }

    fun recycleAdTask(task: LoadAdTask) {
        var tasks: ArrayList<LoadAdTask>? = mLoadAdTaskMap[task.adType]
        if (tasks == null) {
            tasks = ArrayList()
            mLoadAdTaskMap[task.adType] = tasks
        }
        tasks.add(task)
    }

    fun loadAd(adContainer: ViewGroup, adType: String, unitId: String) {
        var task: LoadAdTask? = mLoadAdTasks[unitId]
        if (task == null) {
            task = createLoadAdTask(adType, unitId)
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

    fun obtainAdView(context: Context, adViewType: Int): View? {
        var views: ArrayList<View>? = mPool[adViewType]
        if (views == null) {
            views = ArrayList()
            mPool[adViewType] = views
        }
        var adView: View? = null
        for (view in views) {
            if (view.parent == null) {
                adView = view
                break
            }
        }
        if (adView == null) {
            var layoutId = -1
            when (adViewType) {
                AdViewType.AD_NATIVE -> layoutId = mNativeAdLayoutResId
                AdViewType.AD_FB_NATIVE -> layoutId = fbNativeAdLayoutResId
                AdViewType.AD_FB_NATIVE_BANNER -> layoutId = fbNativeBannerAdLayoutResId
                AdViewType.AD_ADMOB_UNIFIED -> layoutId = admobUnifiedAdLayoutResId
            }
            if (layoutId != -1) {
                adView = LayoutInflater.from(context).inflate(layoutId, null, false)
                val viewHolder = AdViewHolder(adView)
                adView?.tag = viewHolder
                views.add(adView)
            }
        }
        if (adView != null) {
            val viewHolder = adView.tag as AdViewHolder
            if (viewHolder != null) {
                viewHolder.resetViews()
                if (viewHolder.buttonRemoveAd != null) {
                    viewHolder.buttonRemoveAd.setOnClickListener(onClickRemoveAdButtonListener)
                }
            }
        }
        return adView
    }
}