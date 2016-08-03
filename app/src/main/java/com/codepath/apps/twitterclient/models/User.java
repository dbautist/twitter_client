package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements JSONSerializable{
  private String name;
  private long uid;
  private String screenName;
  private String profileImageUrl;

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    name = jsonObject.getString("name");
    uid = jsonObject.getLong("id");
    screenName = jsonObject.getString("screen_name");
    profileImageUrl = jsonObject.getString("profile_image_url");
  }
}
