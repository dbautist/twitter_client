package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@Table(name = "User")
@Parcel(analyze = {User.class})   // add Parceler annotation here
public class User extends Model implements JSONSerializable {
  @Column(name = "UserId")
  public long uid;

  @Column(name = "Name")
  public String name;

  @Column(name = "ScreenName")
  public String screenName;

  @Column(name = "ProfileImageUrl")
  public String profileImageUrl;

  public User() {
    super();
  }

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    name = jsonObject.getString("name");
    uid = jsonObject.getLong("id");
    screenName = "@" + jsonObject.getString("screen_name");
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
