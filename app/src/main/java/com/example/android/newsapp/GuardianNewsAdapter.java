package com.example.android.newsapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GuardianNewsAdapter extends ArrayAdapter<GuardianNews> {

    private TextView titleTexView;
    private TextView dateTexView;
    private TextView sectionTextView;
    private TextView authorTexView;
    private ImageView thumbnailImageView;


    public GuardianNewsAdapter(Context context, List<GuardianNews> guardianNews) {
        super(context, 0, guardianNews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.guardian_news_list_item, parent, false);
        }

        GuardianNews currentGuardianNews = getItem(position);

        titleTexView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTexView.setText(currentGuardianNews.getmTitle());

        dateTexView = (TextView) listItemView.findViewById(R.id.date_text_view);
        dateTexView.setText(currentGuardianNews.getmDate());

        sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);

        if (TextUtils.isEmpty(currentGuardianNews.getmAuthor())) {
            sectionTextView.setVisibility(View.GONE);
        } else {
            sectionTextView.setText(currentGuardianNews.getmSection());
        }

        authorTexView = (TextView) listItemView.findViewById(R.id.author_text_view);
        if (TextUtils.isEmpty(currentGuardianNews.getmAuthor())) {
            authorTexView.setVisibility(View.GONE);
        } else {
            authorTexView.setText(currentGuardianNews.getmAuthor());
        }

        thumbnailImageView = (ImageView) listItemView.findViewById(R.id.thumbnail_image_view);
        if (currentGuardianNews.hasThumbnail() && currentGuardianNews.getmThumbnail() != null) {
            thumbnailImageView.setImageBitmap(currentGuardianNews.getmThumbnail());
        } else {
            thumbnailImageView.setVisibility(View.GONE);
        }

        return listItemView;
    }
}