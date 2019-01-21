package com.speshiou.android.common.ui.ads

import android.view.ViewGroup

class LoadAdTaskAdapter(val loadAdTask: LoadAdTask, val adIndex: Int): LoadAdTask(loadAdTask.mContext, loadAdTask.mAdViewRecycler, loadAdTask.adType, loadAdTask.mUnitId) {
    override fun run() {
        loadAdTask.run()
    }

    override fun setAdContainer(adContainer: ViewGroup?) {
        loadAdTask.setAdContainer(adContainer, adIndex)
    }
}