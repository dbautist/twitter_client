package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.AppConstants;

import org.parceler.Parcels;

public class UserTimelineFragment extends TweetsListFragment {
  private static final String TAG = UserTimelineFragment.class.getSimpleName();

  private User mUser;

  public UserTimelineFragment() {
  }

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
  protected void populateTimeline() {
    mClient.getUserTimeline(mUser.uid, mMaxId, mResponseHandler);
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
    loadJSONFromAsset("user_timeline.json");
  }
}
