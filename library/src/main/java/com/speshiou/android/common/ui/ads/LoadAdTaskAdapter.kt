package com.speshiou.android.common.ui.ads

import android.view.ViewGroup

class LoadAdTaskAdapter(private val loadAdTask: LoadAdTask, private val adIndex: Int): LoadAdTask(loadAdTask.context, loadAdTask.adViewRecycler, loadAdTask.adType, loadAdTask.unitId) {
    override fun run() {
        loadAdTask.run()
    }

    override var adContainer: ViewGroup?
        get() = loadAdTask.adContainer
        set(value) {
            loadAdTask.setAdContainer(value, adIndex)
        }
}