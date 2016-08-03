package com.codepath.apps.twitterclient.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
  private static final String TAG = TimelineActivity.class.getSimpleName();

  private TwitterClient client;

  @BindView(R.id.lvTweets)
  ListView lvTweets;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    ButterKnife.bind(this);

    client = TwitterApplication.getRestClient();
    populateTimeline();
  }

  // Send an API request to get the timeline JSON
  // Fill the listview by creating the tweet objects from JSON
  private void populateTimeline() {
    client.getHomeTimeline(new JsonHttpResponseHandler(){
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG, "====== onSuccess: " + response.toString());
        try {
          JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
          List<Tweet> articleList = deserializer.fromJSONArrayToList(response);
          if (articleList != null) {
            Log.d(TAG, "====== deserialization successful");
          }
        } catch (JSONException e) {
          ErrorHandler.handleAppException(e, "Exception from populating Twitter timeline");
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d(TAG, "====== onFailure: " + errorResponse.toString());

      }
    });
  }
}
