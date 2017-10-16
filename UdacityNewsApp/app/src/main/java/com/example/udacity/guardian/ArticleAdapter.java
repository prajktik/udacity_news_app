package com.example.udacity.guardian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder>{


    private ArrayList<ArticleData> marticleList;
    private Context mContext;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView thumbnail;
        private TextView section;
        private TextView title;
        private TextView author;
        private TextView date;
        private View currentView;

        MyViewHolder(View view){
            super(view);
            currentView = view;
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            section = view.findViewById(R.id.tv_article_section);
            title =  view.findViewById(R.id.tv_article_title);
            author = view.findViewById(R.id.tv_article_author);
            date = view.findViewById(R.id.tv_article_date);
        }
    }

     ArticleAdapter(Context context, List articleList){
        mContext = context;
        if(articleList != null && articleList instanceof ArrayList){
            marticleList = (ArrayList<ArticleData>) articleList;
        }else{
            marticleList = new ArrayList<ArticleData>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){

        final ArticleData data = marticleList.get(position);

        String thumbnailId = data.getThumbnail();
        if(thumbnailId != null && !thumbnailId.isEmpty()){
            new LoadThumbnailTask(holder.thumbnail).execute(thumbnailId);
        }else{
            holder.thumbnail.setVisibility(View.GONE);
        }

        holder.section.setText(data.getSection());
        holder.title.setText(data.getTitle());
        String author = data.getAuthor();
        if(author != null && !author.isEmpty()){
            holder.author.setText(author);
        }else{
            holder.author.setVisibility(View.GONE);
        }
        holder.date.setText(data.getDate());
        holder.currentView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String url = data.getWebUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount(){
        return marticleList.size();
    }

    private class LoadThumbnailTask extends AsyncTask<String, Void, Bitmap>{
        ImageView mThumbnail;

        public LoadThumbnailTask(ImageView bmImage){
            this.mThumbnail = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String mThumbnailLink = urls[0];
            Bitmap bitmap = null;
            try{
                InputStream in = new URL(mThumbnailLink).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch(Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result){
            mThumbnail.setImageBitmap(result);
        }
    }

    void clear(){
        if(marticleList != null){
            marticleList.clear();
            notifyDataSetChanged();
        }
    }

    void addAll(List<ArticleData> articleList){
        if(marticleList == null){
            marticleList = new ArrayList<ArticleData>();
        }
        marticleList.addAll(articleList);
        notifyDataSetChanged();
    }
}
