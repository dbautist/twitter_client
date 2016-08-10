package com.codepath.apps.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TimelinePagerAdapter;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
  private static final String TAG = TimelineActivity.class.getSimpleName();

  @BindView(R.id.tabs)
  PagerSlidingTabStrip tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private TwitterClient mClient;
  private User mCurrentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    ButterKnife.bind(this);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayUseLogoEnabled(true);
      getSupportActionBar().setLogo(R.drawable.twitter_logo_white_on_blue);
    }

    mClient = TwitterApplication.getRestClient();
    initViewPager();

    getCurrentUser();
  }

  private void initViewPager() {
    viewpager.setAdapter(new TimelinePagerAdapter(getSupportFragmentManager()));

    // Give the PagerSlidingTabStrip the ViewPager
    tabs.setViewPager(viewpager);
  }

  private void setUserInfo() {
    Log.d(TAG, "User: " + mCurrentUser.toString());
    getSupportActionBar().setTitle(mCurrentUser.screenName);
  }

  private void getCurrentUser() {
    Log.d(TAG, "Fetching user from the server");

    mClient.getUser(new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "getCurrentUser onSuccess: " + response.toString());
        JSONDeserializer<User> deserializer = new JSONDeserializer<>(User.class);
        mCurrentUser = deserializer.configureJSONObject(response);
        if (mCurrentUser == null) {
          ErrorHandler.logAppError("current user is NULL");
        } else {
          TweetManager.getInstance().setCurrentUser(mCurrentUser);
          setUserInfo();
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        ErrorHandler.logAppError("getCurrentUser onFailure1: " + responseString);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        ErrorHandler.logAppError("getCurrentUser onFailure2: " + errorResponse.toString());
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        ErrorHandler.logAppError("getCurrentUser onFailure3");
      }
    });
  }
}
