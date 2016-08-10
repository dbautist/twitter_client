package com.codepath.apps.twitterclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.twitterclient.activities.TweetDetailActivity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.NetworkUtil;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.codepath.apps.twitterclient.util.views.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class HomeTimelineListFragment extends TweetsListFragment {
  private static final String TAG = HomeTimelineListFragment.class.getSimpleName();

  public HomeTimelineListFragment() {}

  public static HomeTimelineListFragment newInstance(FragmentManager fragmentManager) {
    HomeTimelineListFragment frag = new HomeTimelineListFragment();
    frag.mFragmentManager = fragmentManager;
    return frag;
  }

  @Override
  protected void setSwipeRefreshListener() {
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        fetchHomeTimeline(-1);
      }
    });
  }

  @Override
  protected void setTweetList() {
    // TODO: Remove after testing
    loadJSONFromAsset("home_timeline.json");

    fetchHomeTimeline(-1);
  }

  @Override
  protected void setItemClickListener() {
    ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
        new ItemClickSupport.OnItemClickListener() {
          @Override
          public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            Tweet tweet = mTweetList.get(position);
            Intent intent = new Intent(mContext, TweetDetailActivity.class);
            intent.putExtra(AppConstants.TWEET_EXTRA, Parcels.wrap(tweet));
            startActivity(intent);
          }
        });
  }

  @Override
  protected void setOfflineListener() {
    if (!NetworkUtil.isOnline()) {
      List<Tweet> offlineTweetList = mTweetManager.getStoredTweetList();
      if (offlineTweetList != null) {
        mTweetList.addAll(offlineTweetList);
        mAdapter.notifyItemRangeInserted(0, offlineTweetList.size());
      }
      // TODO: Display a notification
    } else {
      fetchHomeTimeline(-1);
    }
  }

  @Override
  protected void customLoadMoreDataFromApi(int page) {
    // Returns results with an ID less than (that is, older than) or equal to the specified ID.
    long maxId = mTweetList.get(mTweetList.size() - 1).id - 1;
    fetchHomeTimeline(maxId);
  }

  @Override
  protected void updateNewTweet(Tweet tweet) {
    Log.d(TAG, "updateNewTweet");

    if (tweet != null) {
      // Add to the beginning of the list and scroll to the top
      Log.d(TAG, "Updating new tweet: " + tweet.text);

      mTweetList.add(0, tweet);
      mAdapter.notifyItemInserted(0);
      rvTweets.scrollToPosition(0);
    }
  }

  private void fetchHomeTimeline(final long maxId) {
//    populateFromJson();

    mClient.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG, "fetchHomeTimeline onSuccess: " + response.toString());
        try {
          JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
          List<Tweet> tweetResponseList = deserializer.fromJSONArrayToList(response);
          if (tweetResponseList != null) {
            Log.d(TAG, "tweet size: " + tweetResponseList.size());
            if (maxId == -1) {
              int listSize = mTweetList.size();
              mTweetList.clear();
              mAdapter.notifyItemRangeRemoved(0, listSize);

              mTweetManager.clearTweetList();
            }

            int curSize = mTweetList.size();
            mTweetList.addAll(tweetResponseList);
            mAdapter.notifyItemRangeInserted(curSize, tweetResponseList.size());

            mTweetManager.storeTweetList(mTweetList);
          }
        } catch (JSONException e) {
          ErrorHandler.handleAppException(e, "Exception from populating home timeline");
        }

        handleSwipeRefresh();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        handleSwipeRefresh();
        if (errorResponse != null) {
          ErrorHandler.logAppError(errorResponse.toString());
        }

        ErrorHandler.displayError(mContext, AppConstants.DEFAULT_ERROR_MESSAGE);
      }
    });
  }
}
