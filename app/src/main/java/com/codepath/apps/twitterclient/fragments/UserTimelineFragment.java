package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UserTimelineFragment extends TweetsListFragment {
  private static final String TAG = UserTimelineFragment.class.getSimpleName();

  private User mUser;

  public UserTimelineFragment() {}

  public static UserTimelineFragment newInstance(FragmentManager fragmentManager, User user) {
    UserTimelineFragment frag = new UserTimelineFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.USER_EXTRA, Parcels.wrap(user));
    frag.setArguments(args);
    frag.mFragmentManager = fragmentManager;

    return frag;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUser = Parcels.unwrap(getArguments().getParcelable(AppConstants.USER_EXTRA));
  }

  @Override
  protected void setSwipeRefreshListener() {
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        fetchUserTimeline();
      }
    });
  }

  @Override
  protected void setTweetList() {
    // TODO: Remove after testing
    loadJSONFromAsset("user_timeline.json");

    fetchUserTimeline();
  }

  @Override
  protected void setOfflineListener() {
    // do nothing
  }

  @Override
  protected void customLoadMoreDataFromApi(int page) {
    fetchUserTimeline();
  }

  @Override
  protected void updateNewTweet(Tweet tweet) {
    // do nothing
    Log.d(TAG, "updateNewTweet");
  }

  private void fetchUserTimeline() {
//    populateFromJson();

    mClient.getUserTimeline(mUser.uid, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG, "fetchUserTimeline onSuccess: " + response.toString());
        try {
          JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
          List<Tweet> tweetResponseList = deserializer.fromJSONArrayToList(response);
          if (tweetResponseList != null) {
            Log.d(TAG, "tweet size: " + tweetResponseList.size());

            int curSize = mTweetList.size();
            mTweetList.addAll(tweetResponseList);
            mAdapter.notifyItemRangeInserted(curSize, tweetResponseList.size());

            mTweetManager.storeTweetList(mTweetList);
          }
        } catch (JSONException e) {
          ErrorHandler.handleAppException(e, "Exception from populating mentions timeline");
        }

        handleSwipeRefresh();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
      errorResponse) {
        handleSwipeRefresh();
        if (errorResponse != null) {
          ErrorHandler.logAppError(errorResponse.toString());
        }

        ErrorHandler.displayError(mContext, AppConstants.DEFAULT_ERROR_MESSAGE);
      }
    });
  }
}
