package com.speshiou.android.common.ui.ads

class AdUtils {
    companion object {
        var AD_ENABLED = true

        fun setAdEnabled(enabled: Boolean) {
            AD_ENABLED = enabled
        }

        fun insertAdData(adViewRecycler: AdViewRecycler, data: Array<Any>, adPositions: Array<Int>, adType: String, adId: String): Array<Any> {
            if (AD_ENABLED && data.isNotEmpty() && AdType.allTypes.contains(adType) && adPositions.isNotEmpty() && adType.isNotBlank() && adId.isNotBlank()) {
                var updatedData = arrayListOf<Any>()
                updatedData.addAll(data)
                var admobTask: LoadAdTask? = null
                val admobNumInBundle = 2
                adPositions.forEachIndexed { index, i ->
                    if (i > -1) {
                        var insertToIndex = -1
                        if (i <= data.size) {
                            insertToIndex = i + index

                        } else if (updatedData[updatedData.lastIndex] !is LoadAdTask) {
//                            insertToIndex = updatedData.lastIndex + 1
                        }
                        if (adType == AdType.AD_ADMOB_NATIVE && index % admobNumInBundle == 0) {
                            admobTask = getAdData(adViewRecycler, adType, adId, -1, admobNumInBundle)
                        }
                        if (insertToIndex > -1) {
                            var adTask: LoadAdTask? = null
                            if (admobTask != null) {
                                if (admobTask is LoadAdTaskAdapter) {
                                    adTask = admobTask
                                } else {
                                    admobTask?.apply {
                                        adTask = LoadAdTaskAdapter(this, index % admobNumInBundle)
                                    }
                                }
                            } else {
                                adTask = getAdData(adViewRecycler, adType, adId, index, adPositions.size)
                            }
                            adTask?.apply {
                                updatedData.add(insertToIndex, this)
                            }
                        }
                    }
                }
                return updatedData.toTypedArray()
            } else {
                return data
            }
        }

        private fun getAdData(adViewRecycler: AdViewRecycler, adType: String, adId: String, index: Int, totalAds: Int): LoadAdTask? {
            val adTask = adViewRecycler.obtainAdTask(adType, adId, -1)
            if (adTask != null) {
                if (adTask is LoadCSATask) {
                    adTask.page = index + 1
                } else if (adTask is LoadGoogleAdsNativeAdTask) {
                    adTask.adCount = totalAds
                }
            }
            return adTask
        }
    }
}