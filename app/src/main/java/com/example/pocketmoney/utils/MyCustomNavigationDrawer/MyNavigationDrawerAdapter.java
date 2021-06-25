package com.example.pocketmoney.utils.MyCustomNavigationDrawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketmoney.R;

import java.util.List;

public class MyNavigationDrawerAdapter extends RecyclerView.Adapter<MyNavigationDrawerAdapter.MyNavigationDrawerViewHolder> {

    private List<ModelNavigationItem> navigationItemList;

    public MyNavigationDrawerAdapter(List<ModelNavigationItem> navigationItemList) {
        this.navigationItemList = navigationItemList;
    }

    @NonNull
    @Override
    public MyNavigationDrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.template_navigation_drawer_item,parent,false);

        return new MyNavigationDrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNavigationDrawerViewHolder holder, int position) {
        holder.bindItems(navigationItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return navigationItemList.size();
    }

    static class MyNavigationDrawerViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle,tvSubtitle;
        ImageView ivIcon;

        public MyNavigationDrawerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            ivIcon = itemView.findViewById(R.id.iv_icon);

        }

        public void bindItems(ModelNavigationItem navigationItem){
            tvTitle.setText(navigationItem.getTitle());
            tvSubtitle.setText(navigationItem.getSubTitle());
            ivIcon.setImageResource(navigationItem.getImageUrl());
        }
    }
}
