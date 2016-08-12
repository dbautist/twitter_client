package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TimelinePagerAdapter;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
  private static final String TAG = TimelineActivity.class.getSimpleName();

  @BindView(R.id.toolbar)
  Toolbar toolbar;
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

    setSupportActionBar(toolbar);

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
    ImageView toolbarImage = (ImageView) toolbar.findViewById(R.id.toolbarImage);
    toolbarImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
        intent.putExtra(AppConstants.USER_EXTRA, Parcels.wrap(mCurrentUser));
        startActivity(intent);
      }
    });

//    Glide.with(this).load(mCurrentUser.profileImageUrl)
//        .fitCenter().centerCrop()
//        .into(toolbarImage);

    TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
    toolbarTitle.setText(mCurrentUser.screenName);
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
