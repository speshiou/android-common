package com.speshiou.android.common.ui

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics

open class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    companion object {
        var firebaseAnalytics: FirebaseAnalytics? = null
    }
}