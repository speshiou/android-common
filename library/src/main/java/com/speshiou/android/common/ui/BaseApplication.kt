package com.speshiou.android.common.ui

import android.app.Application
import android.webkit.WebView
import com.facebook.ads.AudienceNetworkAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.speshiou.android.common.BuildConfig

open class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        AudienceNetworkAds.initialize(this)
    }

    companion object {
        var firebaseAnalytics: FirebaseAnalytics? = null
    }
}