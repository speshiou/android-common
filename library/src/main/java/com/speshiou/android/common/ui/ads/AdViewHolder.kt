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
    var viewAdTag: TextView
    var textViewTitle: TextView?
    var textViewSubtitle: TextView?
    var textViewCaption: TextView?
    var textViewBody: TextView?
    var buttonAction: TextView
    var buttonRemoveAd: TextView
    var ratingBar: RatingBar?
    var imageViewIcon: ImageView?
    var adContainer: ViewGroup
    var adOptionsPlaceHolder: ViewGroup
    fun resetViews() {
        if (textViewTitle != null) {
            textViewTitle!!.text = ""
        }
        if (textViewSubtitle != null) {
            textViewSubtitle!!.text = ""
        }
        if (textViewBody != null) {
            textViewBody!!.text = ""
        }
        if (textViewCaption != null) {
            textViewCaption!!.text = ""
        }
        if (ratingBar != null) {
            ratingBar!!.visibility = View.GONE
        }
        if (imageViewIcon != null) {
            imageViewIcon!!.setImageDrawable(null)
        }
    }

    init {
        viewAdTag = itemView.findViewById(R.id.ad_tag)
        textViewTitle = itemView.findViewById<View>(R.id.title) as TextView
        textViewSubtitle = itemView.findViewById<View>(R.id.subtitle) as TextView
        textViewCaption = itemView.findViewById<View>(R.id.caption) as TextView
        textViewBody = itemView.findViewById<View>(R.id.body) as TextView
        ratingBar = itemView.findViewById<View>(R.id.rating) as RatingBar
        imageViewIcon = itemView.findViewById<View>(R.id.icon) as ImageView
        buttonAction = itemView.findViewById<View>(R.id.button_action) as TextView
        buttonRemoveAd = itemView.findViewById(R.id.button_remove_ad)
        adContainer = itemView.findViewById<View>(R.id.ad_container) as ViewGroup
        adOptionsPlaceHolder = itemView.findViewById<View>(R.id.adchoice_placeholder) as ViewGroup
        resetViews()
    }
}