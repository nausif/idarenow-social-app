package com.alpharelevant.idarenow.data.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by NAUSIF on 13-Jan-18.
 */



public class CommentsViewHolder  {

    public CircularImageView comment_profile_image;
    public TextView comment_profile_name;
    public TextView comment_profile_description;
    public TextView comment_time_posted;

    public CommentsViewHolder(View view)
    {
         comment_profile_image = (CircularImageView) view.findViewById(R.id.comment_profile_image);
         comment_profile_name = (TextView) view.findViewById(R.id.comment_username);
         comment_profile_description = (TextView) view.findViewById(R.id.comment_description);
         comment_time_posted = (TextView) view.findViewById(R.id.comment_time_posted);
    }

}
