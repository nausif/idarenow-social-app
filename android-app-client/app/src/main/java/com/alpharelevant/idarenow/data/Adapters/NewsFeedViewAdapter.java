package com.alpharelevant.idarenow.data.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.PostCommentActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.SearchProfileFragment;
import com.alpharelevant.idarenow.data.ViewHolders.NewsFeedViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.AcceptRejectPost;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedModel;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tcking.github.com.giraffeplayer2.Option;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.alpharelevant.idarenow.data.utils.Functions.session;

/**
 * Created by NAUSIF on 28-Feb-18.
 */

public class NewsFeedViewAdapter extends RecyclerView.Adapter<NewsFeedViewHolder> {

    private Context mContext;
    public List<NewsfeedModel> feedList;
    long startTime =0;
    Session session ;
    private static Bitmap bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.question_bmp);;
    public NewsFeedViewAdapter(Context mContext, List<NewsfeedModel> feedList) {
        this.mContext = mContext;
        session = new Session(mContext);
        this.feedList = feedList;

    }



    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View newsFeedView = inflater.inflate(R.layout.newsfeed_model, parent, false);

        // Return a new holder instance
        NewsFeedViewHolder viewHolder = new NewsFeedViewHolder(newsFeedView);
        return viewHolder;
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final NewsFeedViewHolder holder,final int position) {

        // Get the data model based on position
        final NewsfeedModel newsfeedModel = feedList.get(position);
        VideoInfo videoInfo = new VideoInfo()
                .setTitle("")
                .setAspectRatio(VideoInfo.AR_ASPECT_FIT_PARENT)
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 30000000L))
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1L))
                .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1L))
                .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "multiple_requests", 1L))
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "headers", "Connection: keep-alive\r\n"))
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1L))
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect_at_eof", 1L))
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect_streamed", 1L))
//                            .addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect_delay_max", 1L))
//                            .setPlayerImpl(VideoInfo.PLAYER_IMPL_SYSTEM) //using android media player
                .setShowTopBar(true);
        holder.newsFeed_challenge_video.videoInfo(videoInfo);
        holder.newsFeed_challenge_video.getVideoInfo().setPortraitWhenFullScreen(false);
        Log.d("video_url", "onBindViewHolder: "+"http://"+newsfeedModel.getVideoURL());
        holder.newsFeed_challenge_video.setVideoPath("http://"+newsfeedModel.getVideoURL()).setFingerprint(position);


        holder.newsFeed_challenge_commenttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext.getApplicationContext(), PostCommentActivity.class);
                Bundle b = new Bundle();
                b.putInt("post_id", feedList.get(position).getVideoID()); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                mContext.startActivity(intent);
            }
        });

        holder.newsFeed_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt(Constants.search_id,newsfeedModel.getChallengedToUserId());
                Fragment fr = new SearchProfileFragment();
                fr.setArguments(args);
                android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationActivity) mContext).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_nav_frame_layout,fr);
                ft.commit();
            }
        });


        // Set item views based on your views and data model
        holder.newsFeed_userName.setText(newsfeedModel.getUserName());
        holder.newsFeed_time_location.setText(newsfeedModel.getDateTime());
        Bitmap bmp = Functions.getBitmapFromURL("http://"+newsfeedModel.getUserImageURL());
        if(bmp != null)
        {
            holder.newsFeed_profile_image.setImageBitmap(bmp);
        }
//        holder.newsFeed_profile_image.setImageResource(R.drawable.sample_profile_image);
        holder.newsFeed_challenge_by.setText("Challenge By: " + newsfeedModel.getChallenged_by());
        holder.newsFeed_challenge_tittle.setText(newsfeedModel.getChallenged_tittle());
        if(newsfeedModel.getPost_approve_reject()==1) {
            holder.newsFeed_challenge_approve.setImageResource(R.drawable.approve_clicked);
            holder.newsFeed_challenge_approve.setEnabled(false);
            holder.newsFeed_Challenge_reject.setEnabled(false);
            holder.newFeed_approve_ratio.setText(String.valueOf(newsfeedModel.approve_ratio));
            holder.newFeed_reject_ratio.setText(String.valueOf(newsfeedModel.reject_ratio));
            holder.newFeed_approve_ratio.setVisibility(View.VISIBLE);
            holder.newFeed_reject_ratio.setVisibility(View.VISIBLE);
        }
        if(newsfeedModel.getPost_approve_reject()==2) {
            holder.newsFeed_Challenge_reject.setImageResource(R.drawable.reject_clicked);
            holder.newsFeed_challenge_approve.setEnabled(false);
            holder.newsFeed_Challenge_reject.setEnabled(false);
            holder.newFeed_approve_ratio.setText(String.valueOf(newsfeedModel.approve_ratio));
            holder.newFeed_reject_ratio.setText(String.valueOf(newsfeedModel.reject_ratio));
            holder.newFeed_approve_ratio.setVisibility(View.VISIBLE);
            holder.newFeed_reject_ratio.setVisibility(View.VISIBLE);
        }


        holder.newsFeed_challenge_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsfeedModel.getPost_approve_reject() == 0) {
                    RetrofitClient.apiServices().ApproveRejectPost(newsfeedModel.getVideoID(), session.getUserID(), 1).enqueue(new Callback<AcceptRejectPost>() {
                        @Override
                        public void onResponse(Call<AcceptRejectPost> call, Response<AcceptRejectPost> response) {
                            if (response.isSuccessful()) {
                                AcceptRejectPost obj = response.body();
                                if (obj.status == 1) {
                                    holder.newsFeed_challenge_approve.setImageResource(R.drawable.approve_clicked);
                                    holder.newsFeed_challenge_approve.setEnabled(false);
                                    holder.newsFeed_Challenge_reject.setEnabled(false);
                                    holder.newFeed_approve_ratio.setText(String.valueOf(obj.approve_ratio));
                                    holder.newFeed_reject_ratio.setText(String.valueOf(obj.reject_ratio));
                                    holder.newFeed_approve_ratio.setVisibility(View.VISIBLE);
                                    holder.newFeed_reject_ratio.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AcceptRejectPost> call, Throwable t) {
                            Toast.makeText(mContext, "Could not approve the video", Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });

        holder.newsFeed_challenge_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext.getApplicationContext(), PostCommentActivity.class);
                Bundle b = new Bundle();
                b.putInt("post_id", feedList.get(position).getVideoID()); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                mContext.startActivity(intent);
            }
        });

        holder.newsFeed_Challenge_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsfeedModel.getPost_approve_reject() == 0) {
                    RetrofitClient.apiServices().ApproveRejectPost(newsfeedModel.getVideoID(), session.getUserID(), 2).enqueue(new Callback<AcceptRejectPost>() {
                        @Override
                        public void onResponse(Call<AcceptRejectPost> call, Response<AcceptRejectPost> response) {
                            if (response.isSuccessful()) {
                                AcceptRejectPost obj = response.body();
                                if (obj.status == 2) {
                                    holder.newsFeed_Challenge_reject.setImageResource(R.drawable.reject_clicked);
                                    holder.newsFeed_challenge_approve.setEnabled(false);
                                    holder.newsFeed_Challenge_reject.setEnabled(false);
                                    holder.newFeed_approve_ratio.setText(String.valueOf(obj.approve_ratio));
                                    holder.newFeed_reject_ratio.setText(String.valueOf(obj.reject_ratio));
                                    holder.newFeed_approve_ratio.setVisibility(View.VISIBLE);
                                    holder.newFeed_reject_ratio.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AcceptRejectPost> call, Throwable t) {
                            Toast.makeText(mContext, "Could not reject the video", Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });

        holder.newsFeed_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt(Constants.search_id,newsfeedModel.getChallengedToUserId());
                Fragment fr = new SearchProfileFragment();
                fr.setArguments(args);
                android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationActivity) mContext).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_nav_frame_layout,fr);
                ft.commit();
            }
        });


    }

    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(NewsFeedViewAdapter.bmp,Float.valueOf(250),Float.valueOf(250),null);
    }
    @Override
    public int getItemCount() {
        return feedList.size();
    }

}

