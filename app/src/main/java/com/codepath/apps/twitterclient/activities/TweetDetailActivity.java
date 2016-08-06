package com.codepath.apps.twitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {
  private static final String TAG = TweetDetailActivity.class.getSimpleName();

  @BindView(R.id.ivProfilePhoto)
  ImageView ivProfilePhoto;
  @BindView(R.id.etReplyText)
  EditText etReplyText;
  @BindView(R.id.btReply)
  Button btReply;

  private TwitterClient mClient;
  private Tweet mTweet;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityTweetDetailBinding binding = DataBindingUtil.setContentView(
        this, R.layout.activity_tweet_detail);
    ButterKnife.bind(this);

    mClient = TwitterApplication.getRestClient();

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.tweet));


    mTweet = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.TWEET_EXTRA));
    if (mTweet != null) {
      binding.setTweet(mTweet);
      initTweetDetails();
    } else {

    }

  }

  private void initTweetDetails() {
    etReplyText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && TextUtils.isEmpty(etReplyText.getText())) {
          etReplyText.setText(mTweet.user.screenName + " ");
          etReplyText.setSelection(etReplyText.getText().length());
        }
      }
    });

    etReplyText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
          btReply.setEnabled(true);
        } else {
          btReply.setEnabled(false);
        }
      }
    });

    Glide.with(this).load(mTweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
        .fitCenter().centerCrop()
        .into(ivProfilePhoto);
  }

  @OnClick(R.id.btReply)
  public void replyTweet() {
    mClient.postStatus(etReplyText.getText().toString(), mTweet.id, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "----- tweet reply successful: " + response.toString());
        JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
        Tweet statusTweet = deserializer.configureJSONObject(response);
        if (statusTweet != null) {
          Log.d(TAG, "------ reply success");
          handleSuccess();
        } else {
          handleError("Error from deserializing JSON response");
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        handleError("onFailure1: " + responseString);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        handleError("onFailure2: " + errorResponse.toString());
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        handleError("onFailure3");
      }
    });
  }

  private void handleSuccess() {
    // 1. Clear the edittext
    etReplyText.setText("");
  }

  private void handleError(String errorMessage) {
    ErrorHandler.logAppError(errorMessage);
    ErrorHandler.displayError(this, AppConstants.DEFAULT_ERROR_MESSAGE);
  }
}