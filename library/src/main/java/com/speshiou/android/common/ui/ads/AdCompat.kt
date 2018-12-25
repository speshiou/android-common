package com.speshiou.android.common.ui.ads

import com.google.android.gms.ads.AdSize

class AdCompat {
    companion object {
        fun parseBannerSizes(s: String): Array<AdSize> {
            val bannerSizes = s.split(",").map {
                it.trim()
            }

            var sizes = arrayListOf<AdSize>()
            for (size in bannerSizes) {
                when (size) {
                    "BANNER" -> {
                        sizes.add(AdSize.BANNER)
                    }
                    "FULL_BANNER" -> {
                        sizes.add(AdSize.FULL_BANNER)
                    }
                    "LARGE_BANNER" -> {
                        sizes.add(AdSize.LARGE_BANNER)
                    }
                    "LEADERBOARD" -> {
                        sizes.add(AdSize.LEADERBOARD)
                    }
                    "MEDIUM_RECTANGLE" -> {
                        sizes.add(AdSize.MEDIUM_RECTANGLE)
                    }
                    "WIDE_SKYSCRAPER" -> {
                        sizes.add(AdSize.WIDE_SKYSCRAPER)
                    }
                    "SMART_BANNER" -> {
                        sizes.add(AdSize.SMART_BANNER)
                    }
                    "FLUID" -> {
                        sizes.add(AdSize.FLUID)
                    }
                }
            }
            return sizes.toTypedArray()
        }
    }
}