package com.alpharelevant.idarenow.data.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by NAUSIF on 28-Feb-18.
 */


public class ChallengeSummaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView challengeSummary_challengerName;
    public CircularImageView challengeSummary_profile_image;
    public TextView challengeSummary_timestamp;
    public TextView challengeSummary_challenge_tittle;
    ItemClickListner itemClickListner;


    public ChallengeSummaryViewHolder(View view) {
        super(view);
        challengeSummary_profile_image = (CircularImageView) view.findViewById(R.id.challengesummary_profile_image);
        challengeSummary_timestamp = (TextView) view.findViewById(R.id.challengesummary_dateTime);
        challengeSummary_challengerName = (TextView) view.findViewById(R.id.challengesummary_challengerName);
        challengeSummary_challenge_tittle = (TextView) view.findViewById(R.id.challengesummary_challengeTittle);
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







