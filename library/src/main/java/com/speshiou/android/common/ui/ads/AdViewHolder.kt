package com.speshiou.android.common.ui.ads

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.speshiou.android.common.R

/**
 * Created by joey on 2017/9/5.
 */
class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var viewAdTag: TextView?
    var textViewTitle: TextView?
    var textViewSubtitle: TextView?
    var textViewCaption: TextView?
    var textViewBody: TextView?
    var buttonAction: TextView?
    var buttonRemoveAd: TextView?
    var ratingBar: RatingBar?
    var imageViewIcon: ImageView?
    var adOptionsPlaceHolder: ViewGroup?
    var adContainer: ViewGroup?

    fun resetViews() {
        textViewTitle?.text = ""
        textViewSubtitle?.text = ""
        textViewBody?.text = ""
        textViewCaption?.text = ""
        ratingBar?.visibility = View.GONE
        imageViewIcon?.setImageDrawable(null)
    }

    init {
        viewAdTag = itemView.findViewById(R.id.ad_tag)
        textViewTitle = itemView.findViewById(R.id.title)
        textViewSubtitle = itemView.findViewById(R.id.subtitle)
        textViewCaption = itemView.findViewById(R.id.caption)
        textViewBody = itemView.findViewById(R.id.body)
        ratingBar = itemView.findViewById(R.id.rating)
        imageViewIcon = itemView.findViewById(R.id.icon)
        buttonAction = itemView.findViewById(R.id.button_action)
        buttonRemoveAd = itemView.findViewById(R.id.button_remove_ad)
        adContainer = itemView.findViewById(R.id.ad_container)
        adOptionsPlaceHolder = itemView.findViewById(R.id.adchoice_placeholder)
        resetViews()
    }
}