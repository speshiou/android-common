package com.speshiou.android.common.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.speshiou.android.common.R

class EmptyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    companion object {
        fun newInstance(parent: ViewGroup): EmptyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_view_list_item, parent, false)
            return EmptyViewHolder(view)
        }
    }

    val emptyView: EmptyView = itemView.findViewById(R.id.empty_view)
}