package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

// https://dev.twitter.com/overview/api/entities-in-twitter-objects#media
@Parcel
public class Media implements JSONSerializable {
  public enum ResizeType {
    Crop,
    Fit
  }

  public long id;
  public String mediaUrl;
  public String url;
  public String type;
  public int width;
  public int height;
  public String resizeStr;
  public ResizeType resize;

  public Media() {}

  @Override
  public void configureFromJSON(JSONObject jsonObject) throws JSONException {
    id = jsonObject.getLong("id");
    mediaUrl = jsonObject.getString("media_url");
    url = jsonObject.getString("url");

    JSONObject sizesObj = jsonObject.getJSONObject("sizes");
    if (sizesObj != null) {
      JSONObject smallObj = sizesObj.getJSONObject("small");
      if (smallObj != null) {
        width = smallObj.getInt("w");
        height = smallObj.getInt("h");
        resizeStr = smallObj.getString("resize");
        if (resizeStr != null) {
          if (resizeStr.equalsIgnoreCase("fit")) {
            resize = ResizeType.Fit;
          } else if (resizeStr.equalsIgnoreCase("crop")) {
            resize = ResizeType.Crop;
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("id=").append(id).append("\n");
    str.append("url=").append(url).append("\n");
    str.append("mediaUrl=").append(mediaUrl).append("\n");
    str.append("width=").append(width).append("\n");
    str.append("height=").append(height).append("\n");
    str.append("resizeStr=").append(resizeStr);

    return str.toString();
  }
}
