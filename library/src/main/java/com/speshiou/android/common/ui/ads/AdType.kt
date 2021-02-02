package com.speshiou.android.common.ui.ads

object AdType {
    const val AD_FB_NATIVE = "FB_NATIVE"
    const val AD_FB_NATIVE_BANNER = "FB_NATIVE_BANNER"
    const val AD_DFP = "DFP"
    const val AD_DFP_BANNER = "DFP_BANNER"
    const val AD_ADMOB_NATIVE = "ADMOB_NATIVE"
    const val AD_CSA = "CSA"
    var allTypes = arrayOf(
            AD_FB_NATIVE,
            AD_DFP,
            AD_DFP_BANNER,
            AD_ADMOB_NATIVE,
            AD_CSA,
            AD_FB_NATIVE_BANNER
    )
}