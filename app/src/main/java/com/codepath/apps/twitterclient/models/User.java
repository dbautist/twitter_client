package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

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

  @Column(name = "Description")
  public String description;

  @Column(name = "FollowersCount")
  public int followersCount;

  @Column(name = "FollowingCount")
  public int followingCount;

  @Column(name = "ProfileBannerUrl")
  public String profileBannerUrl;

  @Column(name = "IsFollowing")
  public boolean isFollowing;

  public User() {
    super();
  }

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    name = jsonObject.getString("name");
    uid = jsonObject.getLong("id");
    screenName = "@" + jsonObject.getString("screen_name");
    description = jsonObject.getString("description");
    followersCount = jsonObject.getInt("followers_count");
    followingCount = jsonObject.getInt("friends_count");
    profileImageUrl = jsonObject.getString("profile_image_url");
    isFollowing = jsonObject.getBoolean("following");

    try {
      profileBannerUrl = jsonObject.getString("profile_banner_url") + "/mobile";
    } catch(JSONException e) {
      // don't throw an error
    }

  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("name=").append(name).append(";\n");
    str.append("screenName=").append(screenName).append(";\n");
    str.append("followersCount=").append(followersCount).append(";\n");
    str.append("followingCount=").append(followingCount).append(";\n");
    str.append("isFollowing=").append(isFollowing).append(";\n");
    str.append("profileImageUrl=").append(profileImageUrl).append(";\n");;
    str.append("profileBannerUrl=").append(profileBannerUrl).append(";\n");;

    return str.toString();
  }
}
