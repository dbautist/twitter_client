package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.databinding.ItemTweetBinding;
import com.codepath.apps.twitterclient.models.Tweet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
  private static final String TAG = TweetsAdapter.class.getSimpleName();

  private List<Tweet> mTweetList;
  private Context mContext;

  public TweetsAdapter(Context context, List<Tweet> tweetList) {
    this.mContext = context;
    this.mTweetList = tweetList;
  }

  @Override
  public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_tweet, parent, false);
    ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
    Tweet tweet = mTweetList.get(position);
    Log.d(TAG, "------------ tweet[" + position + "]:\n" + tweet.toString());
    holder.bindTo(tweet);

    if (TextUtils.isEmpty(tweet.user.profileImageUrl)) {
      holder.ivProfilePhoto.setVisibility(View.GONE);
    } else {
      holder.ivProfilePhoto.setVisibility(View.VISIBLE);
      if (tweet.favorited) {
        holder.btFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.favorite_on));
      } else {
        holder.btFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.favorite));
      }

      if (tweet.retweeted) {
        holder.btRetweet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.retweet_on));
      } else {
        holder.btRetweet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.retweet));
      }

      if (tweet.user != null) {
        Glide.with(mContext).load(tweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
            .fitCenter().centerCrop()
            .into(holder.ivProfilePhoto);
      }
    }
  }

  @Override
  public int getItemCount() {
    return mTweetList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private ItemTweetBinding mBinding;

    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.btFavorite)
    Button btFavorite;
    @BindView(R.id.btRetweet)
    Button btRetweet;

    public ViewHolder(View itemView) {
      super(itemView);
      mBinding = DataBindingUtil.bind(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bindTo(Tweet tweet) {
      mBinding.setTweet(tweet);
      mBinding.executePendingBindings();
    }
  }
}
