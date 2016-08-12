package com.codepath.apps.twitterclient.fragments;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.codepath.apps.twitterclient.models.Tweet;

import java.util.List;


public class HomeTimelineListFragment extends TweetsListFragment {
  private static final String TAG = HomeTimelineListFragment.class.getSimpleName();

  public HomeTimelineListFragment() {
  }

  public static HomeTimelineListFragment newInstance(FragmentManager fragmentManager) {
    HomeTimelineListFragment frag = new HomeTimelineListFragment();
    frag.mFragmentManager = fragmentManager;
    return frag;
  }

  @Override
  protected void populateTimeline() {
    mClient.getHomeTimeline(mMaxId, mResponseHandler);
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

  @Override
  protected void fetchOfflineData() {
    List<Tweet> offlineTweetList = mTweetManager.getStoredTweetList();
    if (offlineTweetList != null) {
      mTweetList.addAll(offlineTweetList);
      mAdapter.notifyItemRangeInserted(0, offlineTweetList.size());
    }
  }

  @Override
  protected void storeOfflineData() {
    mTweetManager.storeTweetList(mTweetList);
  }

  @Override
  protected void clearOfflineData() {
    mTweetManager.clearTweetList();
  }

  @Override
  protected void setTestingData() {
    Log.d(TAG, "setTestingData");

    loadJSONFromAsset("home_timeline.json");
  }
}
