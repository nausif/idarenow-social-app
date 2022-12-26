package com.alpharelevant.idarenow.data.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by NAUSIF on 28-Feb-18.
 */


public class MarketerVideoViewHolder extends RecyclerView.ViewHolder {

    public TextView marketervideo_userName;
    public CircularImageView marketervideo_profile_image;
    public TextView marketervideo_time_location;
    public TextView marketervideo_challenge_commenttext;
    public tcking.github.com.giraffeplayer2.VideoView marketervideo_challenge_video;
    public ImageView marketervideo_challenge_approve;
    public ImageView marketervideo_Challenge_reject;
    public ImageView marketervideo_challenge_comment;
    public Button marketervideo_selectWinner;
    public TextView marketervideo_txt_challenge_winner;
    public TextView marketervideo_approve_ratio;
    public TextView marketervideo_reject_ratio;

    public MarketerVideoViewHolder(View view) {
        super(view);
        marketervideo_profile_image =  view.findViewById(R.id.marketervideo_profile_image);
        marketervideo_challenge_approve =  view.findViewById(R.id.marketervideo_cardView_approve);
        marketervideo_challenge_commenttext = view.findViewById(R.id.marketervideo_cardView_message);
        marketervideo_userName =  view.findViewById(R.id.marketervideo_cardView_name);
        marketervideo_Challenge_reject = view.findViewById(R.id.marketervideo_cardView_reject);
        marketervideo_time_location = view.findViewById(R.id.marketervideo_cardView_time_location);
        marketervideo_challenge_video =  view.findViewById(R.id.marketervideo_cardView_video);
        marketervideo_challenge_comment =  view.findViewById(R.id.marketervideo_cardView_comment);
        marketervideo_selectWinner =  view.findViewById(R.id.marketervideo_selectWinner);
        marketervideo_txt_challenge_winner = view.findViewById(R.id.marketervideo_txt_challenge_winner);
        marketervideo_approve_ratio = view.findViewById(R.id.txt_approve_ratio);
        marketervideo_reject_ratio = view.findViewById(R.id.txt_reject_ratio);

    }

}







