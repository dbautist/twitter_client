package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet implements JSONSerializable {
  private String text;
  private long id;
  private String createdAt;
  private User user;

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    text = jsonObject.getString("text");
    id = jsonObject.getLong("id");
    createdAt = jsonObject.getString("created_at");
    user = new User();
    user.configureFromJSON(jsonObject.getJSONObject("user"));

  }
}
