package com.codepath.apps.twitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.databinding.ActivityProfileBinding;
import com.codepath.apps.twitterclient.fragments.HomeTimelineListFragment;
import com.codepath.apps.twitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.NumUtil;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
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
      mBinding.setUser(mUser);
      initUserDetails();
    }

    initFragment();
  }

  private void initUserDetails() {
    Glide.with(this).load(mUser.profileImageUrl)
        .fitCenter().centerCrop()
        .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
        .into(ivProfileImage);

    if (mUser.backgroundImageUrl != null) {
      Glide.with(this).load(mUser.backgroundImageUrl)
          .fitCenter().centerCrop()
          .into(ivBackdrop);
    }

    // Format numFollowers and numFollowing
    tvNumFollowers.setText(NumUtil.format(mUser.followersCount));
    tvNumFollowing.setText(NumUtil.format(mUser.followingCount));
  }

  private void initFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    UserTimelineFragment fragment = UserTimelineFragment.newInstance(getSupportFragmentManager(), mUser);
    ft.replace(R.id.flUserFragment, fragment);
    ft.commit();
  }
}
