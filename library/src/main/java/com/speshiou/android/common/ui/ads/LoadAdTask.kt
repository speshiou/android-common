package com.speshiou.android.common.ui.ads

import android.content.Context
import android.view.ViewGroup
import android.text.TextUtils

/**
 * Created by joey on 2017/9/12.
 */
open class LoadAdTask(var context: Context, var adViewRecycler: AdViewRecycler, var adType: String, var unitId: String) : Runnable {
    protected var refreshRate: Long = -1
    protected var mAdContainer: ViewGroup? = null
    protected var isLoading = false
        private set
    protected var isFailedInLoadingAd = false
        private set
    private var mInitialized = false
    private var mLoadedTime: Long = 0
    override fun run() {
        if (!mInitialized || isReadyForRefreshing || isFailedInLoadingAd) {
            mInitialized = true
            if (!TextUtils.isEmpty(unitId)) {
                onLoad()
            }
        }
    }

    protected open fun onLoad() {
        mLoadedTime = System.currentTimeMillis()
        isLoading = true
        isFailedInLoadingAd = false
    }

    protected fun onLoaded() {
        isLoading = false
        isFailedInLoadingAd = false
        if (mAdContainer != null && mAdContainer!!.tag != null && equals(mAdContainer!!.tag)) {
            adContainer = mAdContainer
        }
    }

    protected fun onFailedToLoad() {
        isLoading = false
        isFailedInLoadingAd = true
        //            setAdContainer(mAdContainer);
    }

    fun markAsReadyForRefreshing() {
        mLoadedTime = 0
    }

    protected val isReadyForRefreshing: Boolean
        protected get() {
            if (mLoadedTime == 0L) {
                return true
            }
            return if (refreshRate <= 0) {
                false
            } else System.currentTimeMillis() - mLoadedTime > refreshRate
        }

    fun setRefreshRate(refreshRate: Int) {
        if (refreshRate <= 0) {
            this.refreshRate = -1
        } else {
            this.refreshRate = (refreshRate * 1000).toLong()
        }
    }

    fun setAdContainer(adContainer: ViewGroup?, index: Int = -1) {
        mAdContainer = adContainer
        if (adContainer != null) {
            adContainer.tag = this
            adContainer.removeAllViews()
            if (!isLoading && !isFailedInLoadingAd) {
                if (index == -1) {
                    attachAdView(adContainer)
                } else {
                    attachAdView(adContainer, index)
                }
            }
        } else {
            detachAdView()
        }
    }

    open var adContainer: ViewGroup?
        get() = mAdContainer
        set(adContainer) {
            setAdContainer(adContainer, -1)
        }

    protected open fun attachAdView(adContainer: ViewGroup) {
        attachAdView(adContainer, 0)
    }

    protected open fun attachAdView(adContainer: ViewGroup, index: Int) {}
    protected open fun detachAdView() {}
    open fun recycle() {}

    companion object {
        const val AD_REFRESH_RATE = -1
    }

    init {
        setRefreshRate(AD_REFRESH_RATE)
    }
}