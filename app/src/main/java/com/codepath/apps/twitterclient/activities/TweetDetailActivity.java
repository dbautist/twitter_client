package com.codepath.apps.twitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.util.AppConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity {
  @BindView(R.id.ivProfilePhoto)
  ImageView ivProfilePhoto;

  private Tweet mTweet;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityTweetDetailBinding binding = DataBindingUtil.setContentView(
        this, R.layout.activity_tweet_detail);
    ButterKnife.bind(this);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.tweet));

    mTweet = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.TWEET_EXTRA));
    if (mTweet != null) {
      binding.setTweet(mTweet);
      initTweetDetails();
    } else {

    }

  }

  private void initTweetDetails() {
    Glide.with(this).load(mTweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
        .fitCenter().centerCrop()
        .into(ivProfilePhoto);
  }
}
