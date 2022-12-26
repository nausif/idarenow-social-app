package com.alpharelevant.idarenow.data.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.AcceptRejectActivity;
import com.alpharelevant.idarenow.MarketerChallengeActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.alpharelevant.idarenow.data.ViewHolders.ChallengeSummaryViewHolder;
import com.alpharelevant.idarenow.data.ViewHolders.TranscationsDetailsViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeSummary;
import com.alpharelevant.idarenow.data.models.CustomModel.TranscationsDetails;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by NAUSIF on 16-Feb-18.
 */

public class TanscationsDetailsViewAdapter extends RecyclerView.Adapter<TranscationsDetailsViewHolder> {

    private Context mContext;
    private List<TranscationsDetails> challengesList = new ArrayList<>();
    Session session;
    public TanscationsDetailsViewAdapter(Context mContext, List<TranscationsDetails> challengesList) {
        this.mContext = mContext;
        this.challengesList = challengesList;
        session = new Session(getApplicationContext());
    }




    @Override
    public TranscationsDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View challengesView = inflater.inflate(R.layout.transcation_details_model, parent, false);

        // Return a new holder instance
        TranscationsDetailsViewHolder viewHolder = new  TranscationsDetailsViewHolder(challengesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TranscationsDetailsViewHolder holder, int position) {
       final TranscationsDetails challengesModel = challengesList.get(position);

        holder.transcations_challenge_tittle.setText(challengesModel.challenge_Tittle);
        holder.transcations_timestamp.setText(challengesModel.timeAgo);
        holder.transcations_challengerName.setText("By " + challengesModel.challenge_From_Name);
        holder.transcations_profile_image.setImageBitmap(Functions.getBitmapFromURL("http://"+challengesModel.challenge_From_Profile_img));
        holder.transcations_amount.setText("$"+challengesModel.challenge_amount.toString());
        if(challengesModel.credit_ID == session.getUserID())
        {
            holder.transcations_img_icon.setBackgroundResource(R.drawable.ic_arrow_downward_red_24dp);
        }
        else   holder.transcations_img_icon.setBackgroundResource(R.drawable.ic_arrow_upward_green_24dp);
        holder.setItemClickListener(new ItemClickListner() {
            @Override
            public void onItemCLick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MarketerChallengeActivity.class);
                myIntent.putExtra("challengeID", String.valueOf(challengesModel.challenge_ID));
                myIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(myIntent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return challengesList.size();
    }
}
