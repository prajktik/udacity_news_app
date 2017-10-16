package com.example.udacity.guardian;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

class ArticleLoader extends AsyncTaskLoader<List<ArticleData>>{

    private String mQuery;
    private String mSearchKey;


    public ArticleLoader(Context context, String query){

        super(context);
        mQuery = query;

    }

    @Override
    protected void onStartLoading(){

        forceLoad();
    }

    @Override
    public List<ArticleData> loadInBackground(){

        return QueryUtils.fetchBookListing(mQuery);

    }
}
