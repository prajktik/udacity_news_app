package com.example.udacity.guardian;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import static com.example.udacity.guardian.MainActivity.LOG_TAG;

public final class ArticleData{

    String mThumbnail;
    String mSection;
    String mTitle;
    String mAuthor;
    String mDate;
    String mWebUrl;

    public ArticleData(String thumbnail, String section, String title, String author, String
            date, String webUrl){
        mThumbnail = thumbnail;
        mSection = section;
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mWebUrl = webUrl;
    }

    public String getThumbnail(){
        return mThumbnail;
    }

    public String getSection(){
        return mSection;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getDate(){
        return formatDate(mDate);
    }

    private String formatDate(final String date) {
        String formattedDate = "N.A.";
        if ((date != null) && (!date.isEmpty())) {
            try {
                SimpleDateFormat currentSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat newSDF = new SimpleDateFormat("yyyy.MM.dd / HH:mm");//("MM dd, yyyy");
                formattedDate = newSDF.format(currentSDF.parse(date));
            } catch (ParseException e) {
                formattedDate = "N.A.";
                Log.e(LOG_TAG, "Error parsing the published date", e);
            }
        }

        return formattedDate;
    }
    public String getWebUrl(){
        return mWebUrl;
    }
}
