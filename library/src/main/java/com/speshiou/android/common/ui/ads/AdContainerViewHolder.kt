package com.speshiou.android.common.ui.ads

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.speshiou.android.common.R

class AdContainerViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    val adContainer = itemView.findViewById<ViewGroup>(R.id.ad_container)
}