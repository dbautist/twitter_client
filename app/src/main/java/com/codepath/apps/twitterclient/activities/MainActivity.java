package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TimelinePagerAdapter;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  private static final String TAG = MainActivity.class.getSimpleName();
  private int[] tabIcons = {R.drawable.home, R.drawable.mention};
  private String tabTitles[] = new String[] { "Home", "Mentions"};

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;
  @BindView(R.id.drawerLayout)
  DrawerLayout drawerLayout;
  @BindView(R.id.nav_view)
  NavigationView navigationView;
  RelativeLayout headerView;

  private TwitterClient mClient;
  private User mCurrentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.secondary));
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.setDrawerListener(toggle);
    toggle.syncState();
    navigationView.setNavigationItemSelectedListener(this);

    mClient = TwitterApplication.getRestClient();
    initViewPager();
    getCurrentUser();
  }

  private void initViewPager() {
    viewpager.setAdapter(new TimelinePagerAdapter(this, getSupportFragmentManager()));
    tabs.setupWithViewPager(viewpager);

    if (tabs != null) {
      for (int i = 0; i < tabs.getTabCount(); i++) {
        tabs.getTabAt(i).setIcon(tabIcons[i]);
      }
    }

    setActionBarTitle(tabTitles[0]);
    tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        Log.d(TAG, "tab: onTabSelected" + position);
        viewpager.setCurrentItem(position);
        setActionBarTitle(tabTitles[tab.getPosition()]);
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });
  }

  private void setActionBarTitle(String title){
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(title);
    }
  }

  private void setNavHeader() {
    View view = navigationView.getHeaderView(0);
    headerView = (RelativeLayout) view.findViewById(R.id.header);
    headerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        gotoProfile();
      }
    });

    TextView tvName = (TextView) view.findViewById(R.id.tvName);
    tvName.setText(mCurrentUser.name);

    TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
    tvScreenName.setText(mCurrentUser.screenName);

    ImageView ivProfilePhoto = (ImageView) view.findViewById(R.id.ivProfilePhoto);
    Glide.with(this).load(mCurrentUser.profileImageUrl)
        .fitCenter().centerCrop()
        .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
        .into(ivProfilePhoto);

    ImageView ivBackdrop = (ImageView) view.findViewById(R.id.ivBackdrop);
    if (mCurrentUser.profileBannerUrl != null) {
      Glide.with(this).load(mCurrentUser.profileBannerUrl)
          .fitCenter().centerCrop()
          .into(ivBackdrop);
    }
  }

  private void getCurrentUser() {
    Log.d(TAG, "Fetching user from the server");

    mClient.getUser(new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "getCurrentUser onSuccess: " + response.toString());
        JSONDeserializer<User> deserializer = new JSONDeserializer<>(User.class);
        mCurrentUser = deserializer.configureJSONObject(response);
        if (mCurrentUser == null) {
          ErrorHandler.logAppError("current user is NULL");
        } else {
          Log.d(TAG, "---- current user: " + mCurrentUser.toString());
          TweetManager.getInstance().setCurrentUser(mCurrentUser);
          setNavHeader();
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        ErrorHandler.logAppError("getCurrentUser onFailure1: " + responseString);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        ErrorHandler.logAppError("getCurrentUser onFailure2: " + errorResponse.toString());
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        ErrorHandler.logAppError("getCurrentUser onFailure3");
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.nav_profile) {
      gotoProfile();
    }

    return true;
  }

  private void gotoProfile() {
    drawerLayout.closeDrawer(GravityCompat.START);

    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
    intent.putExtra(AppConstants.USER_EXTRA, Parcels.wrap(mCurrentUser));
    startActivity(intent);
  }
}
