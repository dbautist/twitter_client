package com.codepath.apps.twitterclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TweetsAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.codepath.apps.twitterclient.util.views.DividerItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
  private static final String TAG = TimelineActivity.class.getSimpleName();

  private TwitterClient mClient;
  private TweetsAdapter mAdapter;
  private List<Tweet> mTweetList;

  @BindView(R.id.rvTweets)
  RecyclerView rvTweets;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    ButterKnife.bind(this);

    mClient = TwitterApplication.getRestClient();
    initTweetList();
    populateTimeline();
  }

  private void initTweetList() {
    mTweetList = new ArrayList<>();
    mAdapter = new TweetsAdapter(this, mTweetList);
    rvTweets.setLayoutManager(new LinearLayoutManager(this));
    rvTweets.setAdapter(mAdapter);

    RecyclerView.ItemDecoration itemDecoration = new
        DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
    rvTweets.addItemDecoration(itemDecoration);
  }

  // Send an API request to get the timeline JSON
  // Fill the listview by creating the tweet objects from JSON
  private void populateTimeline() {
    mClient.getHomeTimeline(new JsonHttpResponseHandler(){
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG, "====== onSuccess: " + response.toString());
        try {
          JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
          List<Tweet> tweetList = deserializer.fromJSONArrayToList(response);
          if (tweetList != null) {
            Log.d(TAG, "------ size: " + tweetList.size());
            mTweetList.addAll(tweetList);
            mAdapter.notifyItemRangeInserted(0, tweetList.size());
          }
        } catch (JSONException e) {
          ErrorHandler.handleAppException(e, "Exception from populating Twitter timeline");
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        ErrorHandler.logAppError(errorResponse.toString());
        ErrorHandler.displayError(TimelineActivity.this, AppConstants.DEFAULT_ERROR_MESSAGE);
      }
    });
  }
}
