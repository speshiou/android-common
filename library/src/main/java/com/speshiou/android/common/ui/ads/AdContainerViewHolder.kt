package com.speshiou.android.common.ui.ads

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.speshiou.android.common.R

class AdContainerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val adContainer = itemView.findViewById<ViewGroup>(R.id.ad_container)
}