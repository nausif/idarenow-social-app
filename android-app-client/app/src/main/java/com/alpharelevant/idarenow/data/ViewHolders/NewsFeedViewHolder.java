package com.alpharelevant.idarenow.data.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import tcking.github.com.giraffeplayer2.*;
import com.alpharelevant.idarenow.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

/**
 * Created by NAUSIF on 28-Feb-18.
 */


public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

    public TextView newsFeed_userName;
    public CircularImageView newsFeed_profile_image;
    public TextView newsFeed_time_location;
    public TextView newsFeed_challenge_tittle;
    public TextView newsFeed_challenge_by;
    public TextView newsFeed_challenge_commenttext;
    public tcking.github.com.giraffeplayer2.VideoView newsFeed_challenge_video;
    public ImageView newsFeed_challenge_approve;
    public ImageView newsFeed_Challenge_reject;
    public ImageView newsFeed_challenge_comment;
    public TextView newFeed_approve_ratio;
    public TextView newFeed_reject_ratio;

    public NewsFeedViewHolder(View view) {
        super(view);
        newsFeed_profile_image = view.findViewById(R.id.newsFeed_profile_image);
        newsFeed_challenge_approve = view.findViewById(R.id.newsFeed_cardView_approve);
        newsFeed_challenge_by =  view.findViewById(R.id.newsFeed_cardView_Challenge_by);
        newsFeed_challenge_tittle = view.findViewById(R.id.newsFeed_cardView_Challenge_Tittle);
        newsFeed_challenge_commenttext = view.findViewById(R.id.newsFeed_cardView_message);
        newsFeed_userName =  view.findViewById(R.id.newsFeed_cardView_name);
        newsFeed_Challenge_reject = view.findViewById(R.id.newsFeed_cardView_reject);
        newsFeed_time_location =  view.findViewById(R.id.newsFeed_cardView_time_location);
        newsFeed_challenge_video =  view.findViewById(R.id.newsFeed_cardView_video);
        newsFeed_challenge_comment = view.findViewById(R.id.newsFeed_cardView_comment);
        newFeed_approve_ratio = view.findViewById(R.id.txt_approve_ratio);
        newFeed_reject_ratio = view.findViewById(R.id.txt_reject_ratio);


    }

}







