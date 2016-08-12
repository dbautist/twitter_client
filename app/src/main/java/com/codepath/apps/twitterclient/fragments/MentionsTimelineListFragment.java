package com.codepath.apps.twitterclient.fragments;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.codepath.apps.twitterclient.models.Tweet;

public class MentionsTimelineListFragment extends TweetsListFragment {
  private static final String TAG = MentionsTimelineListFragment.class.getSimpleName();

  public MentionsTimelineListFragment() {
  }

  public static MentionsTimelineListFragment newInstance(FragmentManager fragmentManager) {
    MentionsTimelineListFragment frag = new MentionsTimelineListFragment();
    frag.mFragmentManager = fragmentManager;
    return frag;
  }

  @Override
  protected void populateTimeline() {
    mClient.getMentionsTimeline(mMaxId, mResponseHandler);
  }

  @Override
  protected void updateNewTweet(Tweet tweet) {
    // do nothing
    Log.d(TAG, "updateNewTweet");
  }

  @Override
  protected void fetchOfflineData() {
    // do nothing
    Log.d(TAG, "fetchOfflineData");
  }

  @Override
  protected void storeOfflineData() {
    // do nothing
    Log.d(TAG, "storeOfflineData");
  }

  @Override
  protected void clearOfflineData() {
    // do nothing
    Log.d(TAG, "clearOfflineData");
  }

  @Override
  protected void setTestingData() {
    Log.d(TAG, "setTestingData");

    loadJSONFromAsset("home_mention.json");
  }
}
