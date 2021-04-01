package com.speshiou.android.common.ui.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.ads.AdSize
import com.speshiou.android.common.R
import java.util.ArrayList
import java.util.HashMap

class AdViewRecycler(private val mContext: Context) {

    private val mPool = HashMap<Int, ArrayList<View>>()
    private val mLoadAdTaskMap = HashMap<String, ArrayList<LoadAdTask>>()

    var adViewWidth = -1
    var bannerAdSizes = arrayOf(AdSize.LARGE_BANNER)
    var keyword: String? = null
    var gmaNativeAdLayoutResId = R.layout.admob_ad_unified
    var fbNativeAdLayoutResId = R.layout.ad_fb_native
    var fbNativeBannerAdLayoutResId = R.layout.ad_fb_native_banner

    var onClickRemoveAdButtonListener: View.OnClickListener? = null

    init {
        adViewWidth = mContext.resources.displayMetrics.widthPixels
    }

    private fun createLoadAdTask(adType: String, unitId: String): LoadAdTask? {
        var task: LoadAdTask? = null
        if (adType == AdType.AD_FB_NATIVE) {
            task = LoadFbNativeAdTask(mContext, this, adType, unitId)
        } else if (adType == AdType.AD_FB_NATIVE_BANNER) {
            task = LoadFbNativeAdTask(mContext, this, adType, unitId)
        } else if (adType == AdType.AD_DFP || (adType == AdType.AD_DFP_BANNER && !bannerAdSizes.contains(AdSize.FLUID))) {
            task = LoadGoogleAdsNativeAdUnifiedTask(mContext, this, adType, unitId, *bannerAdSizes)
        } else if (adType == AdType.AD_DFP || adType == AdType.AD_DFP_BANNER) {
            task = LoadDfpBannerTask(mContext, this, adType, unitId, adViewWidth, *bannerAdSizes)
        } else if (adType == AdType.AD_ADMOB_NATIVE) {
            task = LoadGoogleAdsNativeAdUnifiedTask(mContext, this, adType, unitId)
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
        val reusedTask = tasks.firstOrNull { it.unitId == unitId } ?: createLoadAdTask(adType, unitId)
        if (reusedTask != null) {
            tasks.remove(reusedTask)
        }
        reusedTask?.setRefreshRate(refreshRate)
        if (reusedTask is LoadCSATask) {
            reusedTask.keyword = keyword
        }
        if (reusedTask is LoadAFSNativeAdTask) {
            reusedTask.keyword = keyword
        }
        return reusedTask
    }

    fun recycleAdTask(task: LoadAdTask) {
        var tasks = mLoadAdTaskMap[task.adType]
        if (tasks != null) {
            for (task in tasks) {
                task.recycle()
            }
            tasks.clear()
        }
        tasks = arrayListOf()
        mLoadAdTaskMap[task.adType] = tasks
        tasks.add(task)
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
                AdViewType.AD_GMA_NATIVE -> layoutId = gmaNativeAdLayoutResId
                AdViewType.AD_FB_NATIVE -> layoutId = fbNativeAdLayoutResId
                AdViewType.AD_FB_NATIVE_BANNER -> layoutId = fbNativeBannerAdLayoutResId
            }
            if (layoutId != -1) {
                adView = LayoutInflater.from(context).inflate(layoutId, null, false)
                val viewHolder = AdViewHolder(adView)
                adView?.tag = viewHolder
                views.add(adView)
            }
        }
        if (adView != null) {
            val viewHolder = adView.tag as? AdViewHolder
            if (viewHolder != null) {
                viewHolder.resetViews()
                viewHolder.buttonRemoveAd?.setOnClickListener(onClickRemoveAdButtonListener)
            }
        }
        return adView
    }
}