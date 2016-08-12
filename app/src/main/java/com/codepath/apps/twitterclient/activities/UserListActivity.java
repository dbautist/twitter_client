package com.codepath.apps.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.fragments.UserListFragment;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UserListActivity extends AppCompatActivity {
  private static final String TAG = UserListActivity.class.getSimpleName();

  private TwitterClient mClient;
  private UserListFragment mFragment;
  private long mUserId;
  private boolean isFollowersList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_following);
    ButterKnife.bind(this);

    initFragment();

    mClient = TwitterApplication.getRestClient();
    mUserId = getIntent().getLongExtra(AppConstants.USER_ID_EXTRA, -1);
    isFollowersList = getIntent().getBooleanExtra(AppConstants.FOLLOWERS_LIST_EXTRA, false);
    if (mUserId != -1) {
      getUserList();
    }
  }

  private void getUserList() {
    String title = "";

    if (isFollowersList) {
      title = getResources().getString(R.string.followers_title);
      mClient.getFollowersList(mUserId, mResponseHandler);
    } else {
      title = getResources().getString(R.string.following_title);
      mClient.getFriendsList(mUserId, mResponseHandler);
    }

    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(title);
    }
  }

  JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
      try {
        JSONArray userArray = response.getJSONArray("users");
        JSONDeserializer<User> deserializer = new JSONDeserializer<>(User.class);
        List<User> userList = deserializer.fromJSONArrayToList(userArray);
        mFragment.addItemList(userList);
      } catch (JSONException e) {
        ErrorHandler.handleAppException(e, "Exception from populating friends list");
      }

      handleRequestComplete();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
      handleRequestComplete();

      if (errorResponse != null) {
        ErrorHandler.logAppError(errorResponse.toString());
      }

      ErrorHandler.displayError(UserListActivity.this, AppConstants.DEFAULT_ERROR_MESSAGE);
    }
  };

  protected void handleRequestComplete() {
    mFragment.hideProgress();
  }

  private void initFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    mFragment = new UserListFragment();
    ft.replace(R.id.flUserFragment, mFragment);
    ft.commit();
  }
}
