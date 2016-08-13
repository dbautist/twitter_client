package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.databinding.ItemUserBinding;
import com.codepath.apps.twitterclient.models.TweetManager;
import com.codepath.apps.twitterclient.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
  private List<User> mUserList;
  private Context mContext;
  private User mUser;

  public UsersAdapter(Context context, List<User> userList) {
    this.mContext = context;
    this.mUserList = userList;
  }

  @Override
  public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
    UsersAdapter.ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    mUser = mUserList.get(position);
    holder.bindTo(mUser);

    Glide.with(mContext).load(mUser.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
        .fitCenter().centerCrop()
        .bitmapTransform(new RoundedCornersTransformation(mContext, 5, 0))
        .into(holder.ivProfilePhoto);

    if(TweetManager.getInstance().getCurrentUser().uid == mUser.uid){
      // hide the follow layout
      holder.followingLayout.setVisibility(View.GONE);
    } else {
      if (mUser.isFollowing) {
        holder.followingLayout.setVisibility(View.GONE);
      } else {
        holder.followingLayout.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override
  public int getItemCount() {
    return mUserList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private ItemUserBinding mBinding;

    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.followingLayout)
    LinearLayout followingLayout;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      mBinding = DataBindingUtil.bind(itemView);
    }

    public void bindTo(User user) {
      mBinding.setUser(user);
      mBinding.executePendingBindings();
    }
  }
}
