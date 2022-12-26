package com.alpharelevant.idarenow.data.Adapters;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.MarketerChallengeActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.AcceptRejectActivity;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.alpharelevant.idarenow.data.ViewHolders.NotificationViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.notificationResponse;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by NAUSIF on 16-Feb-18.
 */

public class NotificationViewAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    private Context mContext;
    private List<notificationResponse> notificationList;
    Session session;
    public NotificationViewAdapter(Context mContext,List<notificationResponse> notificationList) {
        this.mContext = mContext;
        this.notificationList = notificationList;
        session = new Session(getApplicationContext());
    }




    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View notificationView = inflater.inflate(R.layout.notification_model, parent, false);

        // Return a new holder instance
        NotificationViewHolder viewHolder = new NotificationViewHolder(notificationView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
       final notificationResponse notificationModel = notificationList.get(position);

        // Set item views based on your views and data model
        if(notificationModel.approval_status==ChallengeConstants.INITIAL){
            holder.txtNotificationText.setText(notificationModel.from_name + " challenged you to do " + notificationModel.title);
        }
        else if(notificationModel.approval_status==ChallengeConstants.ACCEPTED){
            holder.txtNotificationText.setText( "Complete the challenge \"" + notificationModel.title+"\" by " +notificationModel.from_name );
        }
        else{
            holder.txtNotificationText.setText(notificationModel.from_name + " Status UNknown " + notificationModel.title);
        }
        holder.txtNotificationDate.setText(notificationModel.date_time);
        Log.d("imageNotify","http://"+ notificationModel.profile_image);
        holder.imgProfileNotification.setImageBitmap(Functions.getBitmapFromURL("http://"+notificationModel.profile_image));

        holder.setItemClickListener(new ItemClickListner() {
            @Override
            public void onItemCLick(View v) {
                                Log.d("setItemClickListener", "onResponse: ");
//                                if(notificationModel.approval_status==ChallengeConstants.INITIAL){
//                                    Intent myIntent  = new Intent(getApplicationContext(),AcceptRejectActivity.class);
//                                    myIntent.putExtra("challengeID",String.valueOf(notificationModel.challege_id));
//                                    myIntent.putExtra("approval_status",String.valueOf(notificationModel.approval_status));
//                                    mContext.startActivity(myIntent);
//                                }else if(notificationModel.approval_status==ChallengeConstants.ACCEPTED){
//                                    Intent myIntent  = new Intent(getApplicationContext(),AcceptRejectActivity.class);
//                                    myIntent.putExtra("challengeID",String.valueOf(notificationModel.challege_id));
//                                    myIntent.putExtra("approval_status",String.valueOf(notificationModel.approval_status));
//                                    mContext.startActivity(myIntent);
//                                }
                Intent myIntent = new Intent(getApplicationContext(), MarketerChallengeActivity.class);
                myIntent.putExtra("challengeID", String.valueOf(notificationModel.challege_id));
                mContext.startActivity(myIntent);
            }
        });


    }
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(mContext.getPackageManager()) != null) {
//            startActivityForResult(takeVideoIntent, Constants.REQUEST_VIDEO_CAPTURE);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == Constants.REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
//
//            Uri videoUri = intent.getData();
//            Log.d("videoPath",videoUri.getPath());
//            if(Functions.checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
//                uploadVideoToServer(videoUri);
//            }
//        }
//    }

//    public void uploadVideoToServer(Uri pathToVideoFile){
//
//        File videoFile = new File(Functions.getRealPathFromURIPath(pathToVideoFile,(Activity) mContext));
//
//        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
//        MultipartBody.Part vFile = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);
//        RetrofitClient.apiServices().postChallengeVideo(vFile,"description","5").enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d("uploadedvid","responseRecieved");
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("uploadedvid","failed");
//
//            }
//        });
//    }
    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
