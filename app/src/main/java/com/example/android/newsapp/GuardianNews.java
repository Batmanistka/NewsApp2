package com.example.android.newsapp;

import android.graphics.Bitmap;

public class GuardianNews {

    private String mTitle;
    private String mPublished;
    private String mSection;
    private String mAuthor;
    private Bitmap mThumbnail;
    private String mUrl;

    public GuardianNews(String mTitle, String mPublished, String mSection, String mAuthor, Bitmap mThumbnail, String mUrl) {
        this.mTitle = mTitle;
        this.mPublished = mPublished;
        this.mSection = mSection;
        this.mAuthor = mAuthor;
        this.mThumbnail = mThumbnail;
        this.mUrl = mUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPublished() {
        return mPublished.substring(0, 10);
    }

    public String getmSection() {
        return mSection;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public Bitmap getmThumbnail() {
        return mThumbnail;
    }

    public String getmUrl() {
        return mUrl;
    }

    public boolean hasThumbnail() {
        return mThumbnail != null;
    }
}

