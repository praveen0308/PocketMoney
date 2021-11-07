package com.sampurna.pocketmoney.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.sampurna.pocketmoney.R;
import com.sampurna.pocketmoney.mlm.model.ModelBanner;
import com.sampurna.pocketmoney.shopping.model.ProductImage;

import java.util.List;

public class AutoImageSliderAdapter extends PagerAdapter {


    private Context context;
    private List<?> images;

    public AutoImageSliderAdapter(Context context, List<?> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.auto_image_slide, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        if(images.get(position).getClass()==ModelBanner.class){
            ModelBanner banner = (ModelBanner) images.get(position);
            imageView.setImageResource(banner.getImageUrl());
        }
        else if (images.get(position).getClass()== ProductImage.class){
            ProductImage productImage = (ProductImage) images.get(position);
            if (productImage!=null) {
                String imagePath = Constants.IMAGE_PATH_PREFIX + productImage.getImage_Path();
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_photo_library)
                        .error(R.drawable.ic_error)
                        .into(imageView);
            }
        }

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
