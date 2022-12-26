package com.alpharelevant.idarenow.data.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.AcceptRejectActivity;
import com.alpharelevant.idarenow.MarketerChallengeActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.alpharelevant.idarenow.data.ViewHolders.ChallengeSummaryViewHolder;
import com.alpharelevant.idarenow.data.ViewHolders.NotificationViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeSummary;
import com.alpharelevant.idarenow.data.models.CustomModel.notificationResponse;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by NAUSIF on 16-Feb-18.
 */

public class ChallengeSummaryViewAdapter extends RecyclerView.Adapter<ChallengeSummaryViewHolder> {

    private Context mContext;
    private List<ChallengeSummary> challengesList = new ArrayList<>();
    Session session;
    int user_id;
    public ChallengeSummaryViewAdapter(Context mContext, List<ChallengeSummary> challengesList) {
        this.mContext = mContext;
        this.challengesList = challengesList;
        session = new Session(getApplicationContext());
    }




    @Override
    public ChallengeSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        user_id = session.getUserID();
        // Inflate the custom layout
        View challengesView = inflater.inflate(R.layout.challenge_summary_model, parent, false);

        // Return a new holder instance
        ChallengeSummaryViewHolder viewHolder = new ChallengeSummaryViewHolder(challengesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChallengeSummaryViewHolder holder, int position) {
       final ChallengeSummary challengesModel = challengesList.get(position);

        holder.challengeSummary_challenge_tittle.setText(challengesModel.challenge_Tittle);
        holder.challengeSummary_timestamp.setText(challengesModel.timeAgo);
        if(challengesModel.challenge_Type_ID == 1) {
            if(challengesModel.challenge_From_ID == user_id) {
                holder.challengeSummary_challengerName.setText("To " + challengesModel.challenge_From_Name);
            }
            else {
                holder.challengeSummary_challengerName.setText("By " + challengesModel.challenge_From_Name);
            }
            holder.challengeSummary_profile_image.setImageBitmap(Functions.getBitmapFromURL("http://" + challengesModel.challenge_From_Profile_img));
        }
        else if(challengesModel.challenge_Type_ID == 2)
        {
            holder.challengeSummary_challengerName.setText("To All User");
        }

        holder.setItemClickListener(new ItemClickListner() {
            @Override
            public void onItemCLick(View v) {
//                if(challengesModel.challenge_Type_ID == 1) {
//                    Intent myIntent = new Intent(getApplicationContext(), AcceptRejectActivity.class);
//                    myIntent.putExtra("challengeID", String.valueOf(challengesModel.challenge_ID));
//                    myIntent.putExtra("approva_status", String.valueOf(challengesModel.challenge_Approval_Status));
//                    mContext.startActivity(myIntent);
//                }
//                else if(challengesModel.challenge_Type_ID == 2)
//                {
                    Intent myIntent = new Intent(getApplicationContext(), MarketerChallengeActivity.class);
                    myIntent.putExtra("challengeID", String.valueOf(challengesModel.challenge_ID));
                    mContext.startActivity(myIntent);
//                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return challengesList.size();
    }
}
