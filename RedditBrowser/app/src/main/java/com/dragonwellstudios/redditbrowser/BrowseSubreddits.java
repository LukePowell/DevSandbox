package com.dragonwellstudios.redditbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class BrowseSubreddits extends Activity {


    ArrayList<String> posts = new ArrayList<String>();

    public static String fromStream(InputStream stream) {
        StringBuilder builder = new StringBuilder();
        Scanner s = new Scanner(stream);
        while (s.hasNext())
        {
            builder.append(s.nextLine());
        }
        return builder.toString();
    }
    public class LoadSubreddits extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL loadSubreddits = new URL("http://www.reddit.com/subreddits/popular.json");
                HttpURLConnection urlConnection = (HttpURLConnection)loadSubreddits.openConnection();

                String jsonString = fromStream(urlConnection.getInputStream());

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray array = jsonObject.getJSONObject("data").getJSONArray("children");
                    for(int i = 0; i < array.length(); ++i)
                    {
                        Log.d("RedditBrowser",array.getJSONObject(i).toString());
                        posts.add("/r/" + array.getJSONObject(i).getJSONObject("data").getString("display_name"));
                    }
                }
                catch(JSONException ex){
                    Log.e("RedditBrowser",ex.toString());
                }

                urlConnection.disconnect();
            }
            catch (IOException ex)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            ListView view = (ListView)findViewById(R.id.subReddits);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BrowseSubreddits.this,android.R.layout.simple_list_item_1,posts);
            view.setAdapter(adapter);

            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle b = new Bundle();
                    b.putString("subreddit",posts.get(i));
                    Intent master = new Intent(view.getContext(),SubredditListActivity.class);
                    master.putExtras(b);
                    view.getContext().startActivity(master);
                }
            });

        }

    }

    LoadSubreddits loadSubreddits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_subreddits);

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        //Need to set up some sort of data here probably show a loading window
        loadSubreddits = new LoadSubreddits();
        loadSubreddits.execute((Void) null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse_subreddits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
