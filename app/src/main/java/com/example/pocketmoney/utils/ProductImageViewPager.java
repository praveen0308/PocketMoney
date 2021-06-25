package com.example.pocketmoney.utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.example.pocketmoney.R;
import com.example.pocketmoney.mlm.model.ModelBanner;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProductImageViewPager extends CardView {

    ViewPager viewPager;
    TabLayout indicator;
    List<?> images;
    Activity mActivity;
    Context mContext;

    public ProductImageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_product_image_view_pager,this);

        viewPager = findViewById(R.id.vp_product_images);
        indicator = findViewById(R.id.tl_product_images);
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

    public void setImages(Activity activity,List<?> images) {
        mActivity = activity;
        this.images = images;
        AutoImageSliderAdapter adapter = new AutoImageSliderAdapter(getContext(), images);
        viewPager.setAdapter(adapter);
        indicator.setupWithViewPager(viewPager, true);

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new SliderTimer(), 2000, 4000);

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
