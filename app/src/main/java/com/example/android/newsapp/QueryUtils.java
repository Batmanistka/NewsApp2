package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsapp.GuardianNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static HttpURLConnection urlConnection = null;

    private static InputStream inputStream = null;

    private QueryUtils() {
    }

    public static List<GuardianNews> fetchArticlesFromServer(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static Bitmap getThumbnail(String downloadUrl) throws IOException {
        Bitmap thumbnail = null;
        if (downloadUrl == null) {
            return thumbnail;
        }
        try {
            URL url = createUrl(downloadUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            thumbnail = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error downloading thumbnail", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return thumbnail;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<GuardianNews> extractFeatureFromJson(String guardianNewsJSON) {
        if (TextUtils.isEmpty(guardianNewsJSON)) {
            return null;
        }

        List<GuardianNews> guardianNews = new ArrayList<>();
        try {
            JSONObject jsonResponseObject = new JSONObject(guardianNewsJSON);
            JSONObject jsonResults = jsonResponseObject.getJSONObject("response");
            JSONArray responseArray = jsonResults.getJSONArray("results");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject article = responseArray.getJSONObject(i);

                String section = article.getString("sectionName");
                String title = article.getString("webTitle");
                String date = article.getString("webPublicationDate");
                String url = article.getString("webUrl");

                JSONObject fields = article.getJSONObject("fields");
                Bitmap thumbnail = null;
                String author = fields.getString("byline");
                try {
                    thumbnail = getThumbnail(fields.getString("thumbnail"));
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error getting thumbnail", e);
                }
                guardianNews.add(new GuardianNews(title, date, section, author, thumbnail, url));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        return guardianNews;
    }
}
