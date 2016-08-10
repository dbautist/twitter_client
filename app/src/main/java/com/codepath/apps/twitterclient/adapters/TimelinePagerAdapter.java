package com.codepath.apps.twitterclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.twitterclient.fragments.HomeTimelineListFragment;
import com.codepath.apps.twitterclient.fragments.MentionsTimelineListFragment;

public class TimelinePagerAdapter extends FragmentPagerAdapter {
  final int PAGE_COUNT = 2;
  private String tabTitles[] = new String[] { "HOME", "MENTION"};
  private FragmentManager mFragmentManager;

  public TimelinePagerAdapter(FragmentManager fm) {
    super(fm);
    mFragmentManager = fm;
  }

  @Override
  public int getCount() {
    return PAGE_COUNT;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return HomeTimelineListFragment.newInstance(mFragmentManager);
    } else if (position == 1){
      return MentionsTimelineListFragment.newInstance(mFragmentManager);
    } else {
      return null;
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    // Generate title based on item position
    return tabTitles[position];
  }
}