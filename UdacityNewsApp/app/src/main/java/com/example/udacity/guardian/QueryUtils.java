package com.example.udacity.guardian;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public final class QueryUtils{

    private static final String LOG_TAG = QueryUtils.class.getName();
    private static final String CONTRIBUTOR = "contributor";

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int CONNECT_SUCCESS = 200;

    private QueryUtils(){
    }

    public static ArrayList<ArticleData> fetchBookListing(String query){

        if(query == null || query.isEmpty()){
            return null;
        }

        Log.i(LOG_TAG, "QUERY IS : \n"+query);
        URL url = createUrl(query);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<ArticleData> articleList = extractBooks(jsonResponse);
        return articleList;
    }

    private static URL createUrl(String query){
        URL url = null;

        try{
            url = new URL(query);
        }catch(MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{

        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == CONNECT_SUCCESS){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG, "Problem retrieving the ArticleData List JSON results.", e);
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset
                    .forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static ArrayList<ArticleData> extractBooks(String jSonResponse){

        ArrayList<ArticleData> articleList = new ArrayList<>();
        try{

            JSONObject mainObj = new JSONObject(jSonResponse);
            JSONObject response = mainObj.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");
            JSONObject result;
            String section = null;
            String date = null;
            String title = null;
            String webUrl = null;
            JSONObject fields = null;
            String thumbnailId = null;

            JSONArray tagsArray;
            int noOfTags;
            JSONObject tag;
            String type;

            StringBuilder builder;
            String authors = "";

            ArticleData data;

            if(resultsArray != null){

                for(int i = 0; i < resultsArray.length(); i++){

                    result = resultsArray.getJSONObject(i);
                    if(result.has("sectionName")){
                        section = result.getString("sectionName");
                    }
                    if(result.has("webPublicationDate")){
                        date = result.getString("webPublicationDate");
                    }
                    if(result.has("webTitle")){
                        title = result.getString("webTitle");
                    }
                    if(result.has("webUrl")){
                        webUrl = result.getString("webUrl");
                    }
                    if(result.has("fields")){
                        fields = result.getJSONObject("fields");
                        if(fields.has("thumbnail")){
                            thumbnailId = fields.getString("thumbnail");
                        }
                    }

                    if(result.has("tags")){
                        tagsArray = result.getJSONArray("tags");
                        if(tagsArray != null && tagsArray.length() > 0){
                            builder = new StringBuilder();
                            noOfTags = tagsArray.length();
                            for(int j = 0; j < noOfTags; j++){

                                tag = tagsArray.getJSONObject(j);

                                if(tag.has("type")){
                                    type = tag.getString("type");
                                    if(CONTRIBUTOR.equals(type)){
                                        builder.append(tag.getString("webTitle"));
                                        if(j < (noOfTags - 1)){ //do not append comma ',' after
                                            // the last tag
                                            builder.append(", ");
                                        }
                                    }
                                }
                            }
                            authors = builder.toString();
                        }
                    }

                    data = new ArticleData(thumbnailId, section, title, authors, date, webUrl);
                    articleList.add(data);
                }
            }

        }catch(JSONException e){
            Log.e(LOG_TAG, "Problem parsing the news search JSON results", e);
        }

        return articleList;
    }

}
