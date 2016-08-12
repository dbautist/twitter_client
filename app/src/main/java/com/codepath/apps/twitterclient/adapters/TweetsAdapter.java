package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.ProfileActivity;
import com.codepath.apps.twitterclient.databinding.ItemMediaTweetBinding;
import com.codepath.apps.twitterclient.databinding.ItemTweetBinding;
import com.codepath.apps.twitterclient.fragments.ComposeDialogFragment;
import com.codepath.apps.twitterclient.models.Media;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.views.PatternEditableBuilder;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
  private static final String TAG = TweetsAdapter.class.getSimpleName();
  public static final int TYPE_TWEET = 0;
  public static final int TYPE_TWEET_MEDIA = 1;

  private List<Tweet> mTweetList;
  private Context mContext;
  private FragmentManager mFragmentManager;
  private Tweet mTweet;

  public TweetsAdapter(Context context, FragmentManager fragmentManager, List<Tweet> tweetList) {
    this.mContext = context;
    this.mFragmentManager = fragmentManager;
    this.mTweetList = tweetList;
  }

  @Override
  public int getItemViewType(int position) {
    Tweet tweet = mTweetList.get(position);
    if (tweet.media == null) {
      return TYPE_TWEET;
    } else {
      return TYPE_TWEET_MEDIA;
    }
  }

  @Override
  public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ViewHolder viewHolder;

    if (viewType == TYPE_TWEET) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_tweet, parent, false);
      viewHolder = new TweetViewHolder(view);
    } else {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_media_tweet, parent, false);
      viewHolder = new TweetMediaViewHolder(view);
    }

    viewHolder.setContext(mContext);
    viewHolder.setFragmentManager(mFragmentManager);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
    Log.d("POS2_DEBUG", String.valueOf(position));

    mTweet = mTweetList.get(position);
    Log.d(TAG, "tweet[" + position + "]:\n" + mTweet.toString());

    int type = getItemViewType(position);
    if (type == TYPE_TWEET) {
      TweetViewHolder tweetViewHolder = (TweetViewHolder) holder;
      tweetViewHolder.bindTo(mTweet);
    } else {
      TweetMediaViewHolder tweetMediaViewHolder = (TweetMediaViewHolder) holder;
      tweetMediaViewHolder.bindTo(mTweet);

      Media tweetMedia = mTweet.media;
      Glide.with(mContext).load(tweetMedia.mediaUrl) // .placeholder(R.drawable.loading_placeholder)
          .centerCrop()
          .into(tweetMediaViewHolder.ivMedia);
    }

    // Make mentions and tags clickable
    new PatternEditableBuilder().
        addPattern(Pattern.compile("\\@(\\w+)"), ContextCompat.getColor(mContext, R.color.primary_dark),
            new PatternEditableBuilder.SpannableClickedListener() {
              @Override
              public void onSpanClicked(String text) {
                Toast.makeText(mContext, "Clicked username: " + text,
                    Toast.LENGTH_SHORT).show();
              }
            }).
        addPattern(Pattern.compile("\\#(\\w+)"), ContextCompat.getColor(mContext, R.color.primary_dark),
            new PatternEditableBuilder.SpannableClickedListener() {
              @Override
              public void onSpanClicked(String text) {
                Toast.makeText(mContext, "Clicked hashtag: " + text,
                    Toast.LENGTH_SHORT).show();
              }
            }).into(holder.tvText);

    if (TextUtils.isEmpty(mTweet.user.profileImageUrl)) {
      holder.ivProfilePhoto.setVisibility(View.GONE);
    } else {
      holder.ivProfilePhoto.setVisibility(View.VISIBLE);
      if (mTweet.favorited) {
        holder.btFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.favorite_on));
      } else {
        holder.btFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.favorite));
      }

      if (mTweet.retweeted) {
        holder.btRetweet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.retweet_on));
      } else {
        holder.btRetweet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.retweet));
      }

      if (mTweet.user != null) {
        Glide.with(mContext).load(mTweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
            .fitCenter().centerCrop()
            .bitmapTransform(new RoundedCornersTransformation(mContext, 5, 0))
            .into(holder.ivProfilePhoto);
      }
    }
  }

  @Override
  public int getItemCount() {
    return mTweetList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    protected FragmentManager mFragmentManager;
    protected Context mContext;
    protected Tweet mTweet;

    @BindView(R.id.tvText)
    TextView tvText;
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.btReply)
    Button btReply;
    @BindView(R.id.btFavorite)
    Button btFavorite;
    @BindView(R.id.btRetweet)
    Button btRetweet;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

    }

    @OnClick(R.id.btReply)
    public void replyButtonClick(View view) {
      Log.d("POS_DEBUG", String.valueOf(getLayoutPosition()));
      Log.d("TWEET_DEBUG", mTweet.toString());

      ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(mTweet);
      composeDialogFragment.show(mFragmentManager, "fragment_compose");
    }

    @OnClick(R.id.ivProfilePhoto)
    public void profilePhotoClick(View view) {
      Intent intent = new Intent(mContext, ProfileActivity.class);
      intent.putExtra(AppConstants.USER_EXTRA, Parcels.wrap(mTweet.user));
      mContext.startActivity(intent);
    }

    public void setContext(Context context) {
      this.mContext = context;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
      this.mFragmentManager = fragmentManager;
    }
  }

  public static class TweetViewHolder extends ViewHolder {
    private ItemTweetBinding mBinding;

    public TweetViewHolder(View itemView) {
      super(itemView);
      mBinding = DataBindingUtil.bind(itemView);
    }

    public void bindTo(Tweet tweet) {
      mTweet = tweet;
      mBinding.setTweet(tweet);
      mBinding.executePendingBindings();
    }
  }

  public static class TweetMediaViewHolder extends ViewHolder {
    private ItemMediaTweetBinding mBinding;

    @BindView(R.id.ivMedia)
    ImageView ivMedia;

    public TweetMediaViewHolder(View itemView) {
      super(itemView);
      mBinding = DataBindingUtil.bind(itemView);
    }

    public void bindTo(Tweet tweet) {
      mTweet = tweet;
      mBinding.setTweet(tweet);
      mBinding.executePendingBindings();
    }
  }
}
