package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class GuardianNewsLoader extends AsyncTaskLoader<List<GuardianNews>> {
    private String mUrl;

    public GuardianNewsLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<GuardianNews> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        return QueryUtils.fetchArticlesFromServer(mUrl);
    }
}

