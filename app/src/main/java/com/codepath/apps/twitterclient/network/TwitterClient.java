package com.codepath.apps.twitterclient.network;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
  private static final String TAG = TwitterClient.class.getSimpleName();

  public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
  public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
  public static final String REST_CONSUMER_KEY = "1WceQOumDb6Tb6hVAdx5s2uLB";       // Change this
  public static final String REST_CONSUMER_SECRET = "a03DpjiyHC4wICTWG0mQ1BfiEWsITGNDNrOLuv8qwlsmKbrjcW"; // Change this
  public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

  public TwitterClient(Context context) {
    super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
  }

  // METHOD == ENDPOINT

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
   * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

  // HomeTimeline - Gets us the home timeline
  // GET statuses/home_timeline.json
  public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
    Log.d(TAG, "------getHomeTimeline = max_id: " + maxId);

    String apiUrl = getApiUrl("statuses/home_timeline.json");
    RequestParams params = new RequestParams();
    params.put("count", AppConstants.TWEET_FETCH_COUNT);
    if (maxId != -1) {
      params.put("max_id", maxId);
    } else {
      params.put("since_id", 1);
    }
    getClient().get(apiUrl, params, handler);
  }

  // HomeTimeline - Gets us the home timeline
  // GET statuses/mentions_timeline.json
  public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
    Log.d(TAG, "------mentions_timeline = max_id: " + maxId);

    String apiUrl = getApiUrl("statuses/mentions_timeline.json");
    RequestParams params = new RequestParams();
    params.put("count", AppConstants.TWEET_FETCH_COUNT);
    if (maxId != -1) {
      params.put("max_id", maxId);
    } else {
      params.put("since_id", 1);
    }
    getClient().get(apiUrl, params, handler);
  }

  // POST statuses/update.json
  // post status or reply to tweet when `in_reply_to_status_id` is set
  public void postStatus(String status, long replyStatusId, AsyncHttpResponseHandler handler) {
    Log.d(TAG, "postStatus: replyStatusId=" + replyStatusId + " ;status=" + status);

    String apiUrl = getApiUrl("statuses/update.json");
    RequestParams params = new RequestParams();
    params.put("status", status);
    if (replyStatusId != -1) {
      params.put("in_reply_to_status_id", replyStatusId);
    }
    getClient().post(apiUrl, params, handler);
  }

  // GET account/verify_credentials.json
  // requesting user if authenticated
  public void getUser(AsyncHttpResponseHandler handler) {
    String apiUrl = getApiUrl("account/verify_credentials.json");
    getClient().get(apiUrl, handler);
  }

  // POST favorites/create.json
  public void markFavorites(long tweetId, AsyncHttpResponseHandler handler) {
    String apiUrl = getApiUrl("favorites/create.json");
    RequestParams params = new RequestParams();
    params.put("id", tweetId);

    getClient().post(apiUrl, params, handler);
  }
}