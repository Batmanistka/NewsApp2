package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GuardianNewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<GuardianNews>> {

    private static final String GN_URL =
            "http://content.guardianapis.com/search?q=news&order-by=newest&page-size=100&show-fields=headline,byline,firstPublicationDate,shortUrl,thumbnail&api-key=test";

    private static final int GUARDIANNEWS_LOADER_ID = 1;

    private GuardianNewsAdapter mNewsAdapter;

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_news_activity);

        ListView guardianNewsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        guardianNewsListView.setEmptyView(mEmptyStateTextView);

        mNewsAdapter = new GuardianNewsAdapter(this, new ArrayList<GuardianNews>());
        guardianNewsListView.setAdapter(mNewsAdapter);

        guardianNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GuardianNews currentGuardianNews = mNewsAdapter.getItem(position);
                Uri guardianNewsUri = Uri.parse(currentGuardianNews.getmUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, guardianNewsUri);
                startActivity(websiteIntent);
            }
        });
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GUARDIANNEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<GuardianNews>> onCreateLoader(int i, Bundle bundle) {
        return new GuardianNewsLoader(this, GN_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<GuardianNews>> loader, List<GuardianNews> guardianNews) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_guardianNews);
        mNewsAdapter.clear();
        if (guardianNews != null && !guardianNews.isEmpty()) {
            mNewsAdapter.addAll(guardianNews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<GuardianNews>> loader) {
        mNewsAdapter.clear();
    }
}