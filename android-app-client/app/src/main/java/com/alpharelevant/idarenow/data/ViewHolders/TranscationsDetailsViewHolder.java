package com.alpharelevant.idarenow.data.ViewHolders;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by NAUSIF on 28-Feb-18.
 */


public class TranscationsDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView transcations_challengerName;
    public CircularImageView transcations_profile_image;
    public TextView transcations_timestamp;
    public TextView transcations_challenge_tittle;
    public ImageView transcations_img_icon;
    public TextView transcations_amount;
    ItemClickListner itemClickListner;


    public TranscationsDetailsViewHolder(View view) {
        super(view);
        transcations_profile_image = (CircularImageView) view.findViewById(R.id.transcations_profile_image);
        transcations_timestamp = (TextView) view.findViewById(R.id.transcations_dateTime);
        transcations_challengerName = (TextView) view.findViewById(R.id.transcations_challengerName);
        transcations_challenge_tittle = (TextView) view.findViewById(R.id.transcations_challengeTittle);
        transcations_img_icon = (ImageView) view.findViewById(R.id.transcations_icon_right);
        transcations_amount = (TextView) view.findViewById(R.id.transcations_amount);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListner.onItemCLick(view);
    }

    public void setItemClickListener(ItemClickListner ic)
    {
        this.itemClickListner=ic;
    }
}







