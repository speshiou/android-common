package com.speshiou.android.common.ui.ads;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.speshiou.android.common.R;

/**
 * Created by joey on 2017/9/5.
 */

public class AdViewHolder extends RecyclerView.ViewHolder {

    public TextView viewAdTag;
    public TextView textViewTitle;
    public TextView textViewSubtitle;
    public TextView textViewCaption;
    public TextView textViewBody;
    public TextView buttonAction;
    public TextView buttonRemoveAd;
    public RatingBar ratingBar;
    public ImageView imageViewIcon;
    public ViewGroup adContainer;
    public ViewGroup adOptionsPlaceHolder;

    public AdViewHolder(View itemView) {
        super(itemView);
        viewAdTag = itemView.findViewById(R.id.ad_tag);
        textViewTitle = (TextView) itemView.findViewById(R.id.title);
        textViewSubtitle = (TextView) itemView.findViewById(R.id.subtitle);
        textViewCaption = (TextView) itemView.findViewById(R.id.caption);
        textViewBody = (TextView) itemView.findViewById(R.id.body);
        ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
        imageViewIcon = (ImageView) itemView.findViewById(R.id.icon);
        buttonAction = (TextView) itemView.findViewById(R.id.button_action);
        buttonRemoveAd = itemView.findViewById(R.id.button_remove_ad);
        adContainer = (ViewGroup) itemView.findViewById(R.id.ad_container);
        adOptionsPlaceHolder = (ViewGroup) itemView.findViewById(R.id.adchoice_placeholder);
        resetViews();
    }

    public void resetViews() {
        if (textViewTitle != null) {
            textViewTitle.setText("");
        }
        if (textViewSubtitle != null) {
            textViewSubtitle.setText("");
        }
        if (textViewBody != null) {
            textViewBody.setText("");
        }
        if (textViewCaption != null) {
            textViewCaption.setText("");
        }
        if (ratingBar != null) {
            ratingBar.setVisibility(View.GONE);
        }
        if (imageViewIcon != null) {
            imageViewIcon.setImageDrawable(null);
        }
    }
}
