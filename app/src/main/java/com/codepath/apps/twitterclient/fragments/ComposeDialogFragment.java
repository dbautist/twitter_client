package com.codepath.apps.twitterclient.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
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

public class ComposeDialogFragment extends DialogFragment {
  private static final String TAG = ComposeDialogFragment.class.getSimpleName();

  @BindView(R.id.btTweet)
  Button btTweet;
  @BindView(R.id.etMessage)
  EditText etMessage;

  private TwitterClient mClient;

  public interface ComposeDialogListener {
    void onUpdateStatusSuccess(Tweet statusTweet);
  }

  public ComposeDialogFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
    mClient = TwitterApplication.getRestClient();
  }

  public static ComposeDialogFragment newInstance() {
    ComposeDialogFragment frag = new ComposeDialogFragment();
//    Bundle args = new Bundle();
//    args.putParcelable(AppConstants.USER_EXTRA, Parcels.wrap(user));
//    frag.setArguments(args);
    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
    ButterKnife.bind(this, view);
    return view;
  }


  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onStart()
  {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null)
    {
      // DialogFragment is not taking up the whole screen
      // http://stackoverflow.com/a/26163346
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }

  @OnClick(R.id.btTweet)
  public void postTweet() {
    // TODO: Check if tweet is empty or exceeded 140chars

    mClient.postStatus(etMessage.getText().toString(), new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "----- tweet successful: " + response.toString());
        JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
        Tweet statusTweet = deserializer.configureJSONObject(response);
        if (statusTweet != null) {
          sendSuccess(statusTweet);
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

  private void sendSuccess(Tweet statusTweet) {
    ComposeDialogListener listener = (ComposeDialogListener) getActivity();
    if (listener != null) {
      listener.onUpdateStatusSuccess(statusTweet);
    }
    dismiss();
  }

  private void handleError(String errorMessage) {
    ErrorHandler.logAppError(errorMessage);
    ErrorHandler.displayError(getActivity(), AppConstants.DEFAULT_ERROR_MESSAGE);
    dismiss();
  }
}
