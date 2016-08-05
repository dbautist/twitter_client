package com.codepath.apps.twitterclient.models;

import com.codepath.apps.twitterclient.util.TwitterUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet implements JSONSerializable {
  public String text;
  public long id;
  public String createdAt;
  public String relativeTimestamp;
  public User user;

  // empty constructor needed by the Parceler library
  public Tweet() { }

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    text = jsonObject.getString("text");
    id = jsonObject.getLong("id");
    createdAt = jsonObject.getString("created_at");
    relativeTimestamp = TwitterUtil.getRelativeTimeAgo(createdAt);
    user = new User();
    user.configureFromJSON(jsonObject.getJSONObject("user"));
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
