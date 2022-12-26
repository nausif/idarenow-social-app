package com.alpharelevant.idarenow.data.Adapters;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.MarketerChallengeActivity;
import com.alpharelevant.idarenow.PostCommentActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.SearchProfileFragment;
import com.alpharelevant.idarenow.data.ViewHolders.CustomVideoViewNewsfeed;
import com.alpharelevant.idarenow.data.ViewHolders.MarketerVideoViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.AcceptRejectPost;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedModel;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tcking.github.com.giraffeplayer2.Option;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by NAUSIF on 28-Feb-18.
 */

public class MarketerVideoViewAdapter extends RecyclerView.Adapter<MarketerVideoViewHolder> {

    private Context mContext;
    public List<NewsfeedModel> feedList;
    long startTime = 0;
    Session session ;
    private static Bitmap bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.question_bmp);
    public MarketerVideoViewAdapter(Context mContext, List<NewsfeedModel> feedList) {
        this.mContext = mContext;
        session = new Session(mContext);
        this.feedList = feedList;
    }

    @Override
    public MarketerVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View newsFeedView = inflater.inflate(R.layout.marketer_videos_model, parent, false);




        // Return a new holder instance
        MarketerVideoViewHolder viewHolder = new MarketerVideoViewHolder(newsFeedView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MarketerVideoViewHolder holder,final int position) {

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
        // yeh null ara
        //holder.marketervideo_challenge_video
        holder.marketervideo_challenge_video.videoInfo(videoInfo);
        holder.marketervideo_challenge_video.getVideoInfo().setPortraitWhenFullScreen(false);
        Log.d("video_url", "onBindViewHolder: "+"http://"+newsfeedModel.getVideoURL());
        holder.marketervideo_challenge_video.setVideoPath("http://"+newsfeedModel.getVideoURL()).setFingerprint(position);


        holder.marketervideo_txt_challenge_winner.setVisibility(View.INVISIBLE);

        if(newsfeedModel.winner_ID != null) {
            if (newsfeedModel.winner_ID > 0) {
                holder.marketervideo_challenge_approve.setVisibility(View.INVISIBLE);
                holder.marketervideo_Challenge_reject.setVisibility(View.INVISIBLE);
                holder.marketervideo_selectWinner.setVisibility(View.INVISIBLE);
                if (newsfeedModel.winner_ID == newsfeedModel.getChallengedToUserId()) {
                    holder.marketervideo_txt_challenge_winner.setVisibility(View.VISIBLE);
                }
            }
        }
        else {

            if (newsfeedModel.getPost_approve_reject() == 1) {
                holder.marketervideo_challenge_approve.setImageResource(R.drawable.approve_clicked);
                holder.marketervideo_challenge_approve.setEnabled(false);
                holder.marketervideo_Challenge_reject.setEnabled(false);
                holder.marketervideo_approve_ratio.setText(String.valueOf(newsfeedModel.approve_ratio));
                holder.marketervideo_reject_ratio.setText(String.valueOf(newsfeedModel.reject_ratio));
                holder.marketervideo_approve_ratio.setVisibility(View.VISIBLE);
                holder.marketervideo_reject_ratio.setVisibility(View.VISIBLE);
            }
            if (newsfeedModel.getPost_approve_reject() == 2) {
                holder.marketervideo_Challenge_reject.setImageResource(R.drawable.reject_clicked);
                holder.marketervideo_challenge_approve.setEnabled(false);
                holder.marketervideo_Challenge_reject.setEnabled(false);
                holder.marketervideo_approve_ratio.setText(String.valueOf(newsfeedModel.approve_ratio));
                holder.marketervideo_reject_ratio.setText(String.valueOf(newsfeedModel.reject_ratio));
                holder.marketervideo_approve_ratio.setVisibility(View.VISIBLE);
                holder.marketervideo_reject_ratio.setVisibility(View.VISIBLE);
            }


            if (newsfeedModel.challenge_Expiry_Date > 0) {
                holder.marketervideo_challenge_approve.setVisibility(View.VISIBLE);
                holder.marketervideo_Challenge_reject.setVisibility(View.VISIBLE);
                holder.marketervideo_selectWinner.setVisibility(View.INVISIBLE);

            } else {

                if (newsfeedModel.getChallengedFromUserId() == session.getUserID()) {
                    holder.marketervideo_selectWinner.setVisibility(View.VISIBLE);
                    holder.marketervideo_challenge_approve.setVisibility(View.INVISIBLE);
                    holder.marketervideo_Challenge_reject.setVisibility(View.INVISIBLE);
                    holder.marketervideo_approve_ratio.setText(String.valueOf(newsfeedModel.approve_ratio));
                    holder.marketervideo_reject_ratio.setText(String.valueOf(newsfeedModel.reject_ratio));
                    holder.marketervideo_approve_ratio.setVisibility(View.INVISIBLE);
                    holder.marketervideo_reject_ratio.setVisibility(View.INVISIBLE);
                } else {
                    holder.marketervideo_selectWinner.setVisibility(View.INVISIBLE);
                    holder.marketervideo_challenge_approve.setVisibility(View.VISIBLE);
                    holder.marketervideo_Challenge_reject.setVisibility(View.VISIBLE);
                    holder.marketervideo_approve_ratio.setText(String.valueOf(newsfeedModel.approve_ratio));
                    holder.marketervideo_reject_ratio.setText(String.valueOf(newsfeedModel.reject_ratio));
                    holder.marketervideo_approve_ratio.setVisibility(View.VISIBLE);
                    holder.marketervideo_reject_ratio.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.marketervideo_challenge_commenttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext.getApplicationContext(), PostCommentActivity.class);
                Bundle b = new Bundle();
                b.putInt("post_id", feedList.get(position).getVideoID()); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                mContext.startActivity(intent);
            }
        });

        holder.marketervideo_profile_image.setOnClickListener(new View.OnClickListener() {
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


        holder.marketervideo_selectWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RetrofitClient.apiServices().postMarketerSelectWinner(newsfeedModel.getChallengedToUserId(),newsfeedModel.getChallenge_id()).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.isSuccessful())
                        {
                            if(response.body().booleanValue())
                            {
                                Toast.makeText(mContext,"Winner Selected Successfully!",Toast.LENGTH_LONG).show();
                                ((MarketerChallengeActivity)mContext).finish();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
            }
        });

        // Set item views based on your views and data model
        holder.marketervideo_userName.setText(newsfeedModel.getUserName());
        holder.marketervideo_time_location.setText(newsfeedModel.getDateTime());
        Bitmap bmp = Functions.getBitmapFromURL("http://"+newsfeedModel.getUserImageURL());
        if(bmp != null)
        {
            holder.marketervideo_profile_image.setImageBitmap(bmp);
        }
//        holder.newsFeed_profile_image.setImageResource(R.drawable.sample_profile_image);




            holder.marketervideo_challenge_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newsfeedModel.getPost_approve_reject() == 0) {
                        RetrofitClient.apiServices().ApproveRejectPost(newsfeedModel.getVideoID(), session.getUserID(), 1).enqueue(new Callback<AcceptRejectPost>() {
                            @Override
                            public void onResponse(Call<AcceptRejectPost> call, Response<AcceptRejectPost> response) {
                                if (response.isSuccessful()) {
                                    AcceptRejectPost obj = response.body();
                                    if (obj.status == 1) {
                                        holder.marketervideo_challenge_approve.setImageResource(R.drawable.approve_clicked);
                                        holder.marketervideo_challenge_approve.setEnabled(false);
                                        holder.marketervideo_Challenge_reject.setEnabled(false);
                                        holder.marketervideo_approve_ratio.setText(String.valueOf(obj.approve_ratio));
                                        holder.marketervideo_reject_ratio.setText(String.valueOf(obj.reject_ratio));
                                        holder.marketervideo_approve_ratio.setVisibility(View.VISIBLE);
                                        holder.marketervideo_reject_ratio.setVisibility(View.VISIBLE);
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

        holder.marketervideo_challenge_comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext.getApplicationContext(), PostCommentActivity.class);
            Bundle b = new Bundle();
            b.putInt("post_id", feedList.get(position).getVideoID()); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            mContext.startActivity(intent);
        }
        });

        holder.marketervideo_Challenge_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsfeedModel.getPost_approve_reject() == 0) {
                    RetrofitClient.apiServices().ApproveRejectPost(newsfeedModel.getVideoID(), session.getUserID(), 2).enqueue(new Callback<AcceptRejectPost>() {
                        @Override
                        public void onResponse(Call<AcceptRejectPost> call, Response<AcceptRejectPost> response) {
                            if (response.isSuccessful()) {
                                AcceptRejectPost obj = response.body();
                                if (obj.status == 2) {
                                    holder.marketervideo_Challenge_reject.setImageResource(R.drawable.reject_clicked);
                                    holder.marketervideo_challenge_approve.setEnabled(false);
                                    holder.marketervideo_Challenge_reject.setEnabled(false);
                                    holder.marketervideo_approve_ratio.setText(String.valueOf(obj.approve_ratio));
                                    holder.marketervideo_reject_ratio.setText(String.valueOf(obj.reject_ratio));
                                    holder.marketervideo_approve_ratio.setVisibility(View.VISIBLE);
                                    holder.marketervideo_reject_ratio.setVisibility(View.VISIBLE);
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

        holder.marketervideo_profile_image.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(MarketerVideoViewAdapter.bmp,Float.valueOf(250),Float.valueOf(250),null);
    }
    @Override
    public int getItemCount() {
        return feedList.size();
    }
}

