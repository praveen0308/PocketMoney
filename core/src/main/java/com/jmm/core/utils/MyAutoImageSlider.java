package com.jmm.core.utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.jmm.core.R;
import com.jmm.model.ModelBanner;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyAutoImageSlider extends CardView {

    ViewPager viewPager;
    TabLayout indicator;
    List<ModelBanner> images;
    Activity mActivity;
    Context mContext;

    public MyAutoImageSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_auto_image_slider,this);

        viewPager = findViewById(R.id.view_pager);
        indicator = findViewById(R.id.tab_layout);
        mContext=getContext();


    }

    public void setImages(List<ModelBanner> images) {
        mActivity = (Activity)mContext;
        this.images = images;
        AutoImageSliderAdapter adapter = new AutoImageSliderAdapter(getContext(), images);
        viewPager.setAdapter(adapter);
        indicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 2000, 4000);


    }

    public void setImages(Activity activity,List<ModelBanner> images) {
        mActivity = activity;
        this.images = images;
        AutoImageSliderAdapter adapter = new AutoImageSliderAdapter(getContext(), images);
        viewPager.setAdapter(adapter);
        indicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 2000, 4000);

    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            (mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < images.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}
