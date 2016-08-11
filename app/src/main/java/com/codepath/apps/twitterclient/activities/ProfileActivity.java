package com.codepath.apps.twitterclient.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragments.HomeTimelineListFragment;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.util.AppConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

  @BindView(R.id.appbar)
  AppBarLayout appbar;
  @BindView(R.id.collapsing_toolbar)
  CollapsingToolbarLayout collapsing_toolbar;
  @BindView(R.id.toolbar)
  Toolbar toolbar;

  private User mUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    ButterKnife.bind(this);

    toolbar.setTitle("");
    setSupportActionBar(toolbar);

    mUser = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.USER_EXTRA));

    initFragment();
  }

  private void initFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.flUserFragment, HomeTimelineListFragment.newInstance(getSupportFragmentManager()));
    ft.commit();
  }
}
