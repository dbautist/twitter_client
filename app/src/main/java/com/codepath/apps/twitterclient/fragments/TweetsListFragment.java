package com.codepath.apps.twitterclient.fragments;

import android.content.Context;
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
import com.codepath.apps.twitterclient.adapters.TweetsAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.views.DividerItemDecoration;
import com.codepath.apps.twitterclient.util.views.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class TweetsListFragment extends Fragment implements ComposeDialogFragment.ComposeDialogListener {
  private static final String TAG = TweetsListFragment.class.getSimpleName();

  @BindView(R.id.swipeContainer)
  SwipeRefreshLayout swipeContainer;
  @BindView(R.id.rvTweets)
  RecyclerView rvTweets;

  protected Context mContext;
  protected TwitterClient mClient;
  protected TweetManager mTweetManager;
  protected TweetsAdapter mAdapter;
  protected FragmentManager mFragmentManager;
  protected List<Tweet> mTweetList;
  protected String mTestJSON;

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
    mTweetManager = TweetManager.getInstance();
    mClient = TwitterApplication.getRestClient();

    initSwipeRefreshLayout();
    initTweetList();
  }

  protected abstract void setSwipeRefreshListener();

  protected abstract void setTweetList();

  protected abstract void setItemClickListener();

  protected abstract void setOfflineListener();

  private void initSwipeRefreshLayout() {
    setSwipeRefreshListener();

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
    setTweetList();

    RecyclerView.ItemDecoration itemDecoration = new
        DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST);
    rvTweets.addItemDecoration(itemDecoration);
    rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        customLoadMoreDataFromApi(page);
      }
    });

    setItemClickListener();
    setOfflineListener();
  }


  protected abstract void customLoadMoreDataFromApi(int page);

  protected void handleSwipeRefresh() {
    if (swipeContainer.isRefreshing()) {
      swipeContainer.setRefreshing(false);
    }
  }

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

  protected abstract void updateNewTweet(Tweet tweet);
}
