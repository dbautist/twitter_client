package com.codepath.apps.twitterclient.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.twitterclient.models.Media;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.JSONDeserializer;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.codepath.apps.twitterclient.util.AppConstants;
import com.codepath.apps.twitterclient.util.DateUtil;
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
  @BindView(R.id.btReplyTweet)
  Button btReplyTweet;
  @BindView(R.id.tvTime)
  TextView tvTime;
  @BindView(R.id.tvDate)
  TextView tvDate;
  @BindView(R.id.tvCharsLeft)
  TextView tvCharsLeft;
  @BindView(R.id.buttonLayout)
  RelativeLayout buttonLayout;
  @BindView(R.id.ivMedia)
  ImageView ivMedia;
  @BindView(R.id.vReplyTweet)
  View vReplyTweet;

  private ActivityTweetDetailBinding mBinding;
  private TwitterClient mClient;
  private Tweet mTweet;
  private int maxCharLength;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(
        this, R.layout.activity_tweet_detail);
    ButterKnife.bind(this);

    mClient = TwitterApplication.getRestClient();
    maxCharLength = Integer.parseInt(getResources().getString(R.string.tweetLimit));

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.tweet));


    mTweet = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.TWEET_EXTRA));
    if (mTweet != null) {
      mBinding.setTweet(mTweet);
      initTweetDetails();
    } else {
      // TODO: handle error
    }
  }

  private void initTweetDetails() {
    Log.d(TAG, "---- initTweetDetails");

    vReplyTweet.setVisibility(View.GONE);
    tvDate.setText(DateUtil.getFormattedDate(this, mTweet.createdAt));
    tvTime.setText(DateUtil.getFormattedTime(this, mTweet.createdAt));

    Media tweetMedia = mTweet.media;
    if (tweetMedia != null) {
      ivMedia.setVisibility(View.VISIBLE);

      Log.d(TAG, "Media width: " + tweetMedia.width + "; height: " + tweetMedia.height);
      DrawableTypeRequest drawableTypeRequest = Glide.with(this).load(mTweet.media.mediaUrl);
      drawableTypeRequest.override(tweetMedia.width, tweetMedia.height);
      drawableTypeRequest.fitCenter().centerCrop();
      drawableTypeRequest.into(ivMedia);
    }

    etReplyText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          if (TextUtils.isEmpty(etReplyText.getText())) {
            etReplyText.setText(mTweet.user.screenName + " ");
            etReplyText.setSelection(etReplyText.getText().length());
          }

          if (buttonLayout.getVisibility() == View.GONE) {
            buttonLayout.setVisibility(View.VISIBLE);
          }
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
        int remainingChar = maxCharLength - s.length();
        tvCharsLeft.setText(Integer.toString(remainingChar));
        boolean isEnabled = true;
        int charTextColor = R.color.dark_gray;
        if (remainingChar < 0) {
          charTextColor = R.color.error;
          isEnabled = false;
        }

        tvCharsLeft.setTextColor(ContextCompat.getColor(TweetDetailActivity.this, charTextColor));
        btReplyTweet.setEnabled(isEnabled);
      }
    });

    Glide.with(this).load(mTweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
        .fitCenter().centerCrop()
        .into(ivProfilePhoto);
  }

  @OnClick(R.id.btReplyTweet)
  public void replyTweet() {
    Log.d(TAG, "---- replyTweet");

    mClient.postStatus(etReplyText.getText().toString(), mTweet.id, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "----- tweet reply successful: " + response.toString());
        JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
        Tweet replyTweet = deserializer.configureJSONObject(response);
        if (replyTweet != null) {
          Log.d(TAG, "------ reply success");
          handleSuccess(replyTweet);
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

  private void handleSuccess(Tweet replyTweet) {
    // hide keyboard
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(etReplyText.getWindowToken(), 0);
    etReplyText.setText("");
    etReplyText.setLines(1);
    etReplyText.setSelection(0);
    etReplyText.clearFocus();

    if (buttonLayout.getVisibility() == View.VISIBLE) {
      buttonLayout.setVisibility(View.GONE);
    }

    ReplyLayout replyLayout = new ReplyLayout();
    ButterKnife.bind(replyLayout, vReplyTweet);
    vReplyTweet.setVisibility(View.VISIBLE);
    mBinding.vReplyTweet.setTweet(replyTweet);
    if (replyTweet.user != null) {
      Glide.with(this).load(replyTweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
          .fitCenter().centerCrop()
          .into(replyLayout.ivProfilePhoto);
    }
  }

  private void handleError(String errorMessage) {
    ErrorHandler.logAppError(errorMessage);
    ErrorHandler.displayError(this, AppConstants.DEFAULT_ERROR_MESSAGE);
  }

  public static class ReplyLayout {
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
  }
}

