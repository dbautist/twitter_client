package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User implements JSONSerializable {
  public String name;
  public long uid;
  public String screenName;
  public String profileImageUrl;

  // empty constructor needed by the Parceler library
  public User() {
  }

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    name = jsonObject.getString("name");
    uid = jsonObject.getLong("id");
    screenName = jsonObject.getString("screen_name");
    profileImageUrl = jsonObject.getString("profile_image_url");
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("name=").append(name).append(";\n");
    str.append("screenName=").append(screenName).append(";\n");
    str.append("profileImageUrl=").append(profileImageUrl);

    return str.toString();
  }
}
