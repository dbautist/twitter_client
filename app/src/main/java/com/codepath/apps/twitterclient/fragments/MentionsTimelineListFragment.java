package com.codepath.apps.twitterclient.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineListFragment extends TweetsListFragment{
  private static final String TAG = MentionsTimelineListFragment.class.getSimpleName();

  public MentionsTimelineListFragment() {}

  public static MentionsTimelineListFragment newInstance(FragmentManager fragmentManager) {
    MentionsTimelineListFragment frag = new MentionsTimelineListFragment();
    frag.mFragmentManager = fragmentManager;
    return frag;
  }

  @Override
  protected void setSwipeRefreshListener() {
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        fetchMentionsTimeline(-1);
      }
    });
  }

  @Override
  protected void setTweetList() {
    // TODO: Remove after testing
    loadJSONFromAsset("home_mention.json");

    fetchMentionsTimeline(-1);
  }

  @Override
  protected void setOfflineListener() {
    // no support for timeline
  }

  @Override
  protected void customLoadMoreDataFromApi(int page) {
    // Returns results with an ID less than (that is, older than) or equal to the specified ID.
    long maxId = mTweetList.get(mTweetList.size() - 1).id - 1;
    fetchMentionsTimeline(maxId);
  }

  @Override
  protected void updateNewTweet(Tweet tweet) {
    // do nothing
    Log.d(TAG, "updateNewTweet");
  }

  private void fetchMentionsTimeline(final long maxId) {
//    populateFromJson();

    mClient.getMentionsTimeline(maxId, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG, "fetchMentionsTimeline onSuccess: " + response.toString());
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
          ErrorHandler.handleAppException(e, "Exception from populating mentions timeline");
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
