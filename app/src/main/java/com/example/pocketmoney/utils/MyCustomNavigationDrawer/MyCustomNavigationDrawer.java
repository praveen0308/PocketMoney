package com.example.pocketmoney.utils.MyCustomNavigationDrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketmoney.R;

import java.util.ArrayList;
import java.util.List;

public class MyCustomNavigationDrawer extends ConstraintLayout {

    RecyclerView recyclerView;
    TextView tvTitle,tvSubtitle;
    ImageView ivProfile;
    List<ModelNavigationItem> navigationItemList = new ArrayList<>();

    public MyCustomNavigationDrawer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.template_user_navigation_drawer,this);

        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        ivProfile = findViewById(R.id.iv_profile);
        recyclerView = findViewById(R.id.rv_navigation_items);
    }

    private void setRecyclerView(){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        MyNavigationDrawerAdapter adapter = new MyNavigationDrawerAdapter(navigationItemList);

        recyclerView.setAdapter(adapter);
    }

    public void setNavigationItemList(List<ModelNavigationItem> navigationItemList) {
        this.navigationItemList = navigationItemList;
        setRecyclerView();
    }
}
