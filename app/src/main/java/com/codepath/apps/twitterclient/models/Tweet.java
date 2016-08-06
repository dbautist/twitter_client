package com.codepath.apps.twitterclient.models;

import android.util.Log;

import com.codepath.apps.twitterclient.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.HashMap;

@Parcel
public class Tweet implements JSONSerializable {
  private static final String TAG = Tweet.class.getSimpleName();
  private HashMap<String, String> timeMap;

  public String text;
  public long id;
  public String createdAt;
  public String relativeTimestamp;
  public String displayTimestamp;
  public boolean favorited;
  public int favoriteCount;
  public boolean retweeted;
  public int retweetCount;
  public String replyTweet;
  public User user;

  // empty constructor needed by the Parceler library
  public Tweet() {}

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    Log.d(TAG, "tweet response: " + jsonObject.toString());

    text = jsonObject.getString("text");
    id = jsonObject.getLong("id");
    createdAt = jsonObject.getString("created_at");
    relativeTimestamp = DateUtil.getRelativeTimeAgo(createdAt);
    favorited = jsonObject.getBoolean("favorited");
    favoriteCount = jsonObject.getInt("favorite_count");
    retweeted = jsonObject.getBoolean("retweeted");
    retweetCount = jsonObject.getInt("retweet_count");
    setDisplayTimestamp();

    user = new User();
    user.configureFromJSON(jsonObject.getJSONObject("user"));
  }

  private void setDisplayTimestamp(){
    // hacky - remove 'ago' then only grab the first letter of the time
    String timestamp = relativeTimestamp.replace("ago", "");
    String[] time = timestamp.split(" ");
    if (time.length == 2) {
      displayTimestamp = time[0] + time[1].charAt(0);
    }
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("id=").append(id).append(";\n");
    str.append("text=").append(text).append(";\n");
    str.append("createdAt=").append(createdAt).append(";\n");
    str.append("user=").append(user.toString()).append(";\n");

    return str.toString();
  }
}
