package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GuardianNewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<GuardianNews>> {

    private static final String GN_URL =
            "https://content.guardianapis.com/search?";

    private GuardianNewsAdapter mNewsAdapter;
    private TextView mEmptyStateTextView;
    ListView guardianNewsListView;
    View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_news_activity);

        guardianNewsListView = findViewById(R.id.list);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        mNewsAdapter = new GuardianNewsAdapter(this, new ArrayList<GuardianNews>());
        guardianNewsListView.setAdapter(mNewsAdapter);
        guardianNewsListView.setEmptyView(mEmptyStateTextView);
        loadingIndicator = findViewById(R.id.loading_indicator);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null &&
                networkInfo.isConnectedOrConnecting();
        if (isConnected) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        guardianNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GuardianNews currentGuardianNews = mNewsAdapter.getItem(position);
                Uri guardianNewsUri = Uri.parse(currentGuardianNews.getmUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, guardianNewsUri);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<GuardianNews>> onCreateLoader(int i, Bundle args) {
        String TAG = "URI";
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean business = sharedPrefs.getBoolean("business",false);
        Boolean fashion = sharedPrefs.getBoolean("fashion",false);
        Boolean games = sharedPrefs.getBoolean("games",false);
        Boolean lifeandstyle = sharedPrefs.getBoolean("lifeandstyle",false);
        Boolean money = sharedPrefs.getBoolean("money",false);
        Boolean science = sharedPrefs.getBoolean("science",false);
        Boolean sport = sharedPrefs.getBoolean("sport",false);
        Boolean technology = sharedPrefs.getBoolean("technology",false);
        Boolean travel = sharedPrefs.getBoolean("travel",false);

        String numArticles = sharedPrefs.getString(
                getString(R.string.settings_num_articles_key),
                getString(R.string.settings_num_articles_default));

        Uri baseUri = Uri.parse(GN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String sections = "";
        if(business){
            sections += "business|";
        }
        if(fashion){
            sections += "fashion|";
        }
        if(games){
            sections += "games|";
        }
        if(lifeandstyle){
            sections += "lifeandstyle|";
        }
        if(money){
            sections += "money|";
        }
        if(science){
            sections += "science|";
        }
        if(sport){
            sections += "sport|";
        }
        if(technology){
            sections += "technology|";
        }
        if(travel){
            sections += "travel|";
        }

        if(sections.endsWith("|")){
            sections = sections.substring(0, sections.length()-1) + "";
        }
        if (!sections.isEmpty()){
            uriBuilder.appendQueryParameter("section", sections);
        }

        uriBuilder.appendQueryParameter("page-size", numArticles);
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,byline");
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.d(TAG, "uriBuilder: " + uriBuilder.toString());

        return new GuardianNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<GuardianNews>> loader, List<GuardianNews> guardianNews) {
        mEmptyStateTextView.setText(R.string.no_guardianNews);
        mNewsAdapter.clear();
        if (guardianNews != null && !guardianNews.isEmpty()) {
            mNewsAdapter.addAll(guardianNews);
        }
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<GuardianNews>> loader) {
        mNewsAdapter.clear();
        getLoaderManager().restartLoader(0, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}