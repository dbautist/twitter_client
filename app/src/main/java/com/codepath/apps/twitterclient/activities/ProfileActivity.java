package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.databinding.ActivityProfileBinding;
import com.codepath.apps.twitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.NumUtil;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
  private static final String TAG = ProfileActivity.class.getSimpleName();

  @BindView(R.id.appbar)
  AppBarLayout appbar;
  @BindView(R.id.collapsing_toolbar)
  CollapsingToolbarLayout collapsing_toolbar;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.ivBackdrop)
  ImageView ivBackdrop;
  @BindView(R.id.ivProfileImage)
  ImageView ivProfileImage;
  @BindView(R.id.tvNumFollowers)
  TextView tvNumFollowers;
  @BindView(R.id.tvNumFollowing)
  TextView tvNumFollowing;
  @BindView(R.id.tvFollowingText)
  TextView tvFollowingText;
  @BindView(R.id.tvFollowersText)
  TextView tvFollowersText;
  @BindView(R.id.followingLayout)
  LinearLayout followingLayout;

  private ActivityProfileBinding mBinding;
  private User mUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(
        this, R.layout.activity_profile);
    ButterKnife.bind(this);

    toolbar.setTitle("");
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mUser = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.USER_EXTRA));
    if (mUser != null) {
      Log.d(TAG, "User Profile: " + mUser.toString());
      mBinding.setUser(mUser);
      initUserDetails();
    }

    initFragment();
  }

  private void initUserDetails() {
    // Replace the normal profile image with the 'bigger' version
    String profileUrl = mUser.profileImageUrl.replace("normal", "bigger");
    Glide.with(this).load(profileUrl)
        .fitCenter().centerCrop()
        .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
        .into(ivProfileImage);

    if (mUser.profileBannerUrl != null) {
      Glide.with(this).load(mUser.profileBannerUrl)
          .fitCenter().centerCrop()
          .into(ivBackdrop);
    }

    // Format numFollowers and numFollowing
    tvNumFollowers.setText(NumUtil.format(mUser.followersCount));
    tvNumFollowing.setText(NumUtil.format(mUser.followingCount));

    if(TweetManager.getInstance().getCurrentUser().uid == mUser.uid){
      // hide the follow layout
      followingLayout.setVisibility(View.GONE);
    } else {
      if (mUser.isFollowing) {
        followingLayout.setVisibility(View.GONE);
      } else {
        followingLayout.setVisibility(View.VISIBLE);
      }
    }
  }

  private void initFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    UserTimelineFragment fragment = UserTimelineFragment.newInstance(getSupportFragmentManager(), mUser);
    ft.replace(R.id.flUserFragment, fragment);
    ft.commit();
  }

  @OnClick(R.id.tvFollowingText)
  public void onFollowingClick(View view) {
    Intent intent = new Intent(this, UserListActivity.class);
    intent.putExtra(AppConstants.USER_ID_EXTRA, mUser.uid);
    startActivity(intent);
  }

  @OnClick(R.id.tvFollowersText)
  public void onFollowersClick(View view) {
    Intent intent = new Intent(this, UserListActivity.class);
    intent.putExtra(AppConstants.USER_ID_EXTRA, mUser.uid);
    intent.putExtra(AppConstants.FOLLOWERS_LIST_EXTRA, true);
    startActivity(intent);
  }
}
