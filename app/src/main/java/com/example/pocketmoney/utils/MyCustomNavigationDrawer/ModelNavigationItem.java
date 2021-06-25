package com.example.pocketmoney.utils.MyCustomNavigationDrawer;

public class ModelNavigationItem {

    private String title;
    private String subTitle;
    private int imageUrl;

    public ModelNavigationItem(String title, String subTitle, int imageUrl) {
        this.title = title;
        this.subTitle = subTitle;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
