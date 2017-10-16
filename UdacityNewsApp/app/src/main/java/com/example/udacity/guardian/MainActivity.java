package com.example.udacity.guardian;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<ArticleData>>{

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int ARTICLE_LOADER_ID = 1;
    private static final String BUNDLE_KEY = "prev_key";

    private String mSearchKey;
    private SearchView mSearchView;
    private ArticleAdapter mAdapter;
    private TextView mEmptyView;
    private ProgressBar mLoadingIndicator;
    private boolean mIsSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            mSearchKey = savedInstanceState.getString(BUNDLE_KEY);
            mIsSavedState = true;
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mAdapter = new ArticleAdapter(this, new ArrayList<ArticleData>());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext
                ());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(mIsSavedState){
            initiateSearch(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        if(mSearchView != null){
            mSearchKey = mSearchView.getQuery().toString();
        }

        outState.putString(BUNDLE_KEY, mSearchKey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        EditText searchEditText = mSearchView.findViewById(android.support.v7.appcompat.R.id
                .search_src_text);
        searchEditText.setTextColor(getColor(android.R.color.black));
        searchEditText.setHintTextColor(getColor(android.R.color.white));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                mSearchKey = query;
                initiateSearch(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                //Do Nothing
                return false;
            }
        });

        if(mSearchKey != null && !mSearchKey.isEmpty()){
            searchItem.expandActionView();
            mSearchView.setQuery(mSearchKey, false);
            mSearchView.clearFocus();
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void initiateSearch(boolean isNewSearch){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            LoaderManager loaderManager = getLoaderManager();
            if(isNewSearch){
                loaderManager.restartLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
            }else{
                loaderManager.initLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
            }
        }else{
            mLoadingIndicator.setVisibility(View.GONE);
            mAdapter.clear();
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<ArticleData>> onCreateLoader(int id, Bundle bundle){

        mAdapter.clear();
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        String query = "";
        if(mSearchKey != null){
            query = getString(R.string.base_url) + mSearchKey + getString(R.string.url_tags) +
                    getString(R.string.url_end);
        }
        return new ArticleLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<ArticleData>> loader, List<ArticleData> articleList){

        mLoadingIndicator.setVisibility(View.GONE);
        mAdapter.clear();
        if(articleList != null && !articleList.isEmpty()){
            mAdapter.addAll(articleList);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.empty_view_text);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ArticleData>> loader){

        mAdapter.clear();

    }
}
