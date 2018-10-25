package com.speshiou.android.common.ui.ads;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.ads.mediation.facebook.FacebookAdapter;
import com.speshiou.android.common.R;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.List;

/**
 * Created by joey on 2017/9/12.
 */

public class LoadAdTask implements Runnable {

    public static final int AD_REFRESH_RATE = 60;

    protected Context mContext;
    protected AdViewRecycler mAdViewRecycler;
    protected String mUnitId;
    protected long mRefreshRateMillis = AD_REFRESH_RATE * 1000;
    protected ViewGroup mAdContainer;
    private boolean mLoading = false;
    private boolean mFailedInLoadingAd = false;
    private boolean mInitialized = false;
    private long mLoadedTime;
    String adType;

    public LoadAdTask(Context context, AdViewRecycler adViewRecycler, String adType, String unitId) {
        mContext = context;
        mAdViewRecycler = adViewRecycler;
        mUnitId = unitId;
        this.adType = adType;
    }

    @Override
    public void run() {
        if (!mInitialized || isReadyForRefreshing() || mFailedInLoadingAd) {
            mInitialized = true;
            mLoadedTime = System.currentTimeMillis();
            if (!TextUtils.isEmpty(mUnitId)) {
                onLoad();
            }
        }
    }

    protected void onLoad() {
        mLoading = true;
        mFailedInLoadingAd = false;
    }

    protected void onLoaded() {
        mLoading = false;
        mFailedInLoadingAd = false;
        if (mAdContainer != null && mAdContainer.getTag() != null && equals(mAdContainer.getTag())) {
            setAdContainer(mAdContainer);
        }
    }

    protected void onFailedToLoad() {
        mLoading = false;
        mFailedInLoadingAd = true;
//            setAdContainer(mAdContainer);
    }

    protected boolean isLoading() {
        return mLoading;
    }

    protected boolean isFailedInLoadingAd() {
        return mFailedInLoadingAd;
    }

    public void markAsReadyForRefreshing() {
        mLoadedTime = 0;
    }

    protected boolean isReadyForRefreshing() {
        return System.currentTimeMillis() - mLoadedTime > getRefreshRate();
    }

    public void setRefreshRate(int refreshRate) {
        mRefreshRateMillis = (refreshRate <= 0 ? AD_REFRESH_RATE : refreshRate) * 1000;
    }

    protected long getRefreshRate() {
        return mRefreshRateMillis;
    }

    public void setAdContainer(ViewGroup adContainer) {
        mAdContainer = adContainer;
        if (mAdContainer != null) {
            mAdContainer.setTag(this);
            mAdContainer.removeAllViews();
            if (!isLoading() && !mFailedInLoadingAd) {
                attachAdView(adContainer);
            }
        } else {
            detachAdView();
        }
    }

    public ViewGroup getAdContainer() {
        return mAdContainer;
    }

    protected void attachAdView(ViewGroup adContainer) {
//            Log.e("joey", "[" + this.getClass().getSimpleName() + "] attachAdView");
    }

    protected void detachAdView() {

    }

    protected void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                            NativeAppInstallAdView adView) {
        adView.setId(AdViewType.AD_INSTALL);
// Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
//                VideoController vc = nativeAppInstallAd.getVideoController();
//
//                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
//                // VideoController will call methods on this object when events occur in the video
//                // lifecycle.
//                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//                    public void onVideoEnd() {
//                        // Publishers should allow native ads to complete video playback before refreshing
//                        // or replacing them with another ad in the same UI location.
//                        mRefresh.setEnabled(true);
//                        mVideoStatus.setText("Video status: Video playback has ended.");
//                        super.onVideoEnd();
//                    }
//                });

        adView.setHeadlineView(adView.findViewById(R.id.title));
        adView.setBodyView(adView.findViewById(R.id.body));
        adView.setCallToActionView(adView.findViewById(R.id.button_action));
        adView.setIconView(adView.findViewById(R.id.icon));
        adView.setPriceView(adView.findViewById(R.id.subtitle));
        adView.setStarRatingView(adView.findViewById(R.id.rating));
        adView.setStoreView(adView.findViewById(R.id.caption));

        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
//                MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
//                adView.setMediaView(mediaView);

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        String title = (String) nativeAppInstallAd.getHeadline();
        ((TextView) adView.getHeadlineView()).setText(title);
//        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((TextView) adView.getBodyView()).setText("");
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAppInstallAd.getIcon().getDrawable());

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
//                if (vc.hasVideoContent()) {
//                    mVideoStatus.setText(String.format(Locale.getDefault(),
//                            "Video status: Ad contains a %.2f:1 video asset.",
//                            vc.getAspectRatio()));
//                } else {
//                    mRefresh.setEnabled(true);
//                    mVideoStatus.setText("Video status: Ad does not contain a video asset.");
//                }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        String price = (String) nativeAppInstallAd.getPrice();
        if (price == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(price);
        }

        String store = (String) nativeAppInstallAd.getStore();
        if (store == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(store);
        }

        if (TextUtils.isEmpty(store) && TextUtils.isEmpty(price)) {
            adView.findViewById(R.id.secondary_line).setVisibility(View.GONE);
        } else {
            adView.findViewById(R.id.secondary_line).setVisibility(View.VISIBLE);
        }

        if (nativeAppInstallAd.getStarRating() == null || nativeAppInstallAd.getStarRating() == 0) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            if (nativeAppInstallAd.getStarRating().floatValue() == 0) {
                adView.getStarRatingView().setVisibility(View.GONE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAppInstallAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        Bundle extras = nativeAppInstallAd.getExtras();
        if (extras != null) {
            if (extras.containsKey(FacebookAdapter.KEY_SUBTITLE_ASSET)) {
                String text = (String) extras.get(FacebookAdapter.KEY_SUBTITLE_ASSET);
                if (!title.equals(text)) {
                    ((TextView) adView.getBodyView()).setText(text);
                }
            }
//            if (extras.containsKey(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET)) {
//                String text = (String) extras.get(FacebookAdapter.KEY_SOCIAL_CONTEXT_ASSET);
//                ((TextView) adView.getPriceView()).setText(text);
//            }
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    protected void populateContentAdView(NativeContentAd nativeContentAd,
                                         NativeContentAdView adView) {
        adView.setId(AdViewType.AD_CONTENT);
        adView.setHeadlineView(adView.findViewById(R.id.title));
        adView.setImageView(adView.findViewById(R.id.icon));
        adView.setBodyView(adView.findViewById(R.id.body));
        adView.findViewById(R.id.rating).setVisibility(View.GONE);
        adView.setCallToActionView(adView.findViewById(R.id.button_action));
//                adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.subtitle));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
//        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getBodyView()).setText("");
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        com.google.android.gms.ads.formats.NativeAd.Image logoImage = nativeContentAd.getLogo();

//                if (logoImage == null) {
//                    adView.getLogoView().setVisibility(View.INVISIBLE);
//                } else {
//                    ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
//                    adView.getLogoView().setVisibility(View.VISIBLE);
//                }

        // Assign native ad object to the native view.

        Bundle extras = nativeContentAd.getExtras();
        if (extras != null) {
//            if (extras.containsKey(FacebookAdapter.KEY_SUBTITLE_ASSET)) {
//                String subtitle = (String) extras.get(FacebookAdapter.KEY_SUBTITLE_ASSET);
//                ((TextView) adView.getBodyView()).setText(subtitle);
//            }
        }

        adView.setNativeAd(nativeContentAd);
    }
}
