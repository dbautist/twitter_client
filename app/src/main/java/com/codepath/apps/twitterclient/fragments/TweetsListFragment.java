package com.codepath.apps.twitterclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.activities.TweetDetailActivity;
import com.codepath.apps.twitterclient.adapters.TweetsAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.NetworkUtil;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.codepath.apps.twitterclient.util.views.DividerItemDecoration;
import com.codepath.apps.twitterclient.util.views.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitterclient.util.views.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public abstract class TweetsListFragment extends Fragment implements ComposeDialogFragment.ComposeDialogListener {
  private static final String TAG = TweetsListFragment.class.getSimpleName();

  @BindView(R.id.swipeContainer)
  SwipeRefreshLayout swipeContainer;
  @BindView(R.id.rvTweets)
  RecyclerView rvTweets;
  @BindView(R.id.pbLoading)
  AVLoadingIndicatorView pbLoading;

  protected Context mContext;
  protected TweetManager mTweetManager;
  protected TwitterClient mClient;
  protected TweetsAdapter mAdapter;
  protected FragmentManager mFragmentManager;
  protected List<Tweet> mTweetList;
  protected String mTestJSON;
  protected long mMaxId = -1;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mContext = getActivity();
    mClient = TwitterApplication.getRestClient();
    mTweetManager = TweetManager.getInstance();

    initSwipeRefreshLayout();
    initTweetList();
  }

  private void initSwipeRefreshLayout() {
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        mMaxId = -1;
        populateTimeline();
      }
    });

    // Configure the refreshing colors
    swipeContainer.setColorSchemeResources(R.color.primary,
        R.color.primary_dark,
        R.color.light_gray,
        R.color.extra_light_gray);

  }

  private void initTweetList() {
    mTweetList = new ArrayList<>();
    mAdapter = new TweetsAdapter(mContext, mFragmentManager, mTweetList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
    rvTweets.setLayoutManager(linearLayoutManager);
    rvTweets.setAdapter(mAdapter);

    RecyclerView.ItemDecoration itemDecoration = new
        DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST);
    rvTweets.addItemDecoration(itemDecoration);
    rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        // Returns results with an ID less than (that is, older than) or equal to the specified ID.
        mMaxId = mTweetList.get(mTweetList.size() - 1).id - 1;
        populateTimeline();
      }
    });

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

    setTestingData();
    setTweetList();
  }

  private void setTweetList() {
    pbLoading.show();

    if (!NetworkUtil.isOnline()) {
      fetchOfflineData();

      // TODO: Display a notification
      pbLoading.hide();
    } else {
      mMaxId = -1;
      populateTimeline();
    }
  }

  protected JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
      Log.d(TAG, "fetchHomeTimeline onSuccess: " + response.toString());
      try {
        JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
        List<Tweet> tweetResponseList = deserializer.fromJSONArrayToList(response);
        if (tweetResponseList != null) {
          Log.d(TAG, "tweet size: " + tweetResponseList.size());
          if (mMaxId == -1) {
            int listSize = mTweetList.size();
            mTweetList.clear();
            mAdapter.notifyItemRangeRemoved(0, listSize);
            clearOfflineData();
          }

          int curSize = mTweetList.size();
          mTweetList.addAll(tweetResponseList);
          mAdapter.notifyItemRangeInserted(curSize, tweetResponseList.size());

          storeOfflineData();
        }
      } catch (JSONException e) {
        ErrorHandler.handleAppException(e, "Exception from populating home timeline");
      }

      handleRequestComplete();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
      handleRequestComplete();
      if (errorResponse != null) {
        ErrorHandler.logAppError(errorResponse.toString());
      }

      ErrorHandler.displayError(mContext, AppConstants.DEFAULT_ERROR_MESSAGE);
    }
  };

  protected void handleRequestComplete() {
    if (swipeContainer.isRefreshing()) {
      swipeContainer.setRefreshing(false);
    } else {
      pbLoading.hide();
    }
  }

  @OnClick(R.id.fabComposeTweet)
  public void composeTweet() {
    FragmentManager fm = getChildFragmentManager();
    ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(null);
    composeDialogFragment.show(fm, "fragment_compose");
  }

  @Override
  public void onUpdateStatusSuccess(Tweet status) {
    Log.d(TAG, "Compose tweet success: " + status.text);
    updateNewTweet(status);
  }

  protected abstract void populateTimeline();

  protected abstract void updateNewTweet(Tweet tweet);

  protected abstract void fetchOfflineData();

  protected abstract void storeOfflineData();

  protected abstract void clearOfflineData();

  protected abstract void setTestingData();

  ///============ TESTING ONLY
  protected void loadJSONFromAsset(String jsonFileName) {
    try {
      InputStream is = getActivity().getAssets().open(jsonFileName);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      mTestJSON = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      ex.printStackTrace();
      mTestJSON = null;
    }
  }

  protected void populateFromJson() {
    try {
      JSONArray jsonArray = new JSONArray(mTestJSON);
      JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
      List<Tweet> tweetResponseList = deserializer.fromJSONArrayToList(jsonArray);
      if (tweetResponseList != null) {
        Log.d(TAG, "tweet size: " + tweetResponseList.size());
        int listSize = mTweetList.size();
        mTweetList.clear();
        mTweetList.addAll(tweetResponseList);
        mAdapter.notifyItemRangeRemoved(0, listSize);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
