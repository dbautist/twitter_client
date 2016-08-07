package com.codepath.apps.twitterclient.network;

import com.codepath.apps.twitterclient.models.JSONSerializable;
import com.codepath.apps.twitterclient.util.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONDeserializer <T extends JSONSerializable> {
  private static final String TAG = JSONDeserializer.class.getName();
  private Class<T> mClazz;

  public JSONDeserializer(Class<T> clazz) {
    this.mClazz = clazz;
  }

  public List<T> fromJSONArrayToList(JSONArray jsonArray) throws JSONException {
    if (jsonArray == null)
      return null;

    List<T> list = new ArrayList<T>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObj = jsonArray.getJSONObject(i);
      T obj = configureJSONObject(jsonObj);
      if (obj != null)
        list.add(obj);
    }

    return list;
  }

  public T configureJSONObject(JSONObject json) {
    try {
      if(json == null)
        return null;

      T obj = mClazz.newInstance();
      obj.configureFromJSON(json);
      return obj;
    } catch (InstantiationException e) {
      ErrorHandler.handleAppException(e, TAG+" InstantiationException");
    } catch (IllegalAccessException e) {
      ErrorHandler.handleAppException(e, TAG+" IllegalAccessException");
    } catch (JSONException e) {
      ErrorHandler.handleAppException(e, TAG+" JSONException");
    }

    return null;
  }
}