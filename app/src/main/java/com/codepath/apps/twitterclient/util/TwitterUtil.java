package com.codepath.apps.twitterclient.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TwitterUtil {
  private static final String TAG = TwitterUtil.class.getSimpleName();

  // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
  public static String getRelativeTimeAgo(String rawJsonDate) {
    String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
    sf.setLenient(true);

    String relativeDate = "";
    long dateMillis = 0;
    try {
      dateMillis = sf.parse(rawJsonDate).getTime();
      relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
          System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    } catch (ParseException e) {
      ErrorHandler.handleAppException(e, "Exception from getRelativeTimeAgo()");
    }

    Log.d(TAG, "------ getRelativeTimeAgo: " + relativeDate);
    return relativeDate;
  }
}
