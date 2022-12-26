package com.alpharelevant.idarenow;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeById;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.FileUtils;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static butterknife.internal.Utils.arrayOf;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.Challenge;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MarketerChallengeBriefFragment extends Fragment {


    @BindView(R.id.brief_name)
    TextView brief_profile_name;

    @BindView(R.id.brief_description)
    TextView brief_description;

    @BindView(R.id.brief_profile_image)
    CircularImageView brief_profile_image;

    @BindView(R.id.brief_title)
    TextView brief_title;

    @BindView(R.id.brief_price)
    TextView brief_price;

    @BindView(R.id.brief_timerText)
    CountdownView brief_timerText;

    @BindView(R.id.brief_btn_Record)
    Button videoRecord;

    @BindView(R.id.accept_btn_accept_reject)
            Button btn_accept;

    @BindView(R.id.reject_btn_accept_reject)
            Button btn_reject;

    @BindView(R.id.brief_challenge_status_description)
            TextView txt_challenge_Status_description;

    private ProgressDialog progress;
    int challengeID;
    ChallengeById Challenger;
    boolean issAccepted = false;
    Intent video_intent = null;
    int user_ID;
    Session s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        try {


            RetrofitClient.apiServices().getChallengeById(challengeID).enqueue(new Callback<ChallengeById>() {
                @Override
                public void onResponse(Call<ChallengeById> call, Response<ChallengeById> response) {

                    Challenger = response.body();
                    brief_profile_name.setText(Challenger.challenge_from_profile_name);
                    brief_title.setText(Challenger.challenge_Tittle);
                    brief_description.setText(Challenger.challenge_Description);
                    brief_profile_image.setImageBitmap(Functions.getBitmapFromURL("http://" + Challenger.challenge_from_profile_image));
                    // don't show accept reject button if it is marketer

                    // if balance is 0 than show N/A
                    if (Challenger.challenge_Prize != null && Challenger.challenge_Prize > 0)
                        brief_price.setText("$" + Challenger.challenge_Prize.toString());
                    else brief_price.setText("N/A");
                    // if expire than dont show button..

                    if (Challenger.challenge_From_ID != user_ID) {


                        RetrofitClient.apiServices().getCheckUserCompletedVideo(user_ID, challengeID).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                                Boolean isVideoSubmittedOnce = response.body().booleanValue();

                                if (isVideoSubmittedOnce == false) {
                                    btn_accept.setVisibility(View.INVISIBLE);
                                    btn_reject.setVisibility(View.INVISIBLE);
                                    videoRecord.setVisibility(View.INVISIBLE);
                                    txt_challenge_Status_description.setText("You have already recorded video :)");
                                    txt_challenge_Status_description.setVisibility(View.VISIBLE);
                                } else {


                                    if (Challenger.challenge_Type_ID == 2) {
                                        if (Challenger.challenge_Expiry_Date > 0) {
                                            brief_timerText.start(Challenger.challenge_Expiry_Date);

                                            videoRecord.setVisibility(View.VISIBLE);
                                            btn_accept.setVisibility(View.INVISIBLE);
                                            btn_reject.setVisibility(View.INVISIBLE);
                                        } else {
                                            btn_accept.setVisibility(View.INVISIBLE);
                                            btn_reject.setVisibility(View.INVISIBLE);
                                            videoRecord.setVisibility(View.INVISIBLE);
                                            txt_challenge_Status_description.setText("This challenge time is over, you cannot record video anymore.");
                                            txt_challenge_Status_description.setVisibility(View.VISIBLE);
                                        }
                                    } else if (Challenger.challenge_Type_ID == 1) {

                                        if (Challenger.challenge_Approval_Status == ChallengeConstants.REJECTED) {
                                            Log.d("showNone", "RipCase");
                                            txt_challenge_Status_description.setText("This challenge is rejected by you.");
                                            txt_challenge_Status_description.setVisibility(View.VISIBLE);
                                            videoRecord.setVisibility(View.INVISIBLE);
                                            btn_accept.setVisibility(View.INVISIBLE);
                                            btn_reject.setVisibility(View.INVISIBLE);
                                        } else if (Challenger.challenge_Expiry_Date > 0) {
                                            brief_timerText.start(Challenger.challenge_Expiry_Date);

//                                        videoRecord.setVisibility(View.VISIBLE);
//                                        btn_accept.setVisibility(View.INVISIBLE);
//                                        btn_reject.setVisibility(View.INVISIBLE);

                                            if (Challenger.challenge_Approval_Status == ChallengeConstants.INITIAL) {

                                                videoRecord.setVisibility(View.INVISIBLE);
                                                btn_accept.setVisibility(View.VISIBLE);
                                                btn_reject.setVisibility(View.VISIBLE);

                                                btn_accept.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View view) {
                                                        RetrofitClient.apiServices().ChallengeStatusUpdate(challengeID, ChallengeConstants.ACCEPTED).enqueue(new Callback<ResponseBody>() {
                                                            @Override
                                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                if (response.isSuccessful()) {
                                                                    btn_accept.setVisibility(View.INVISIBLE);
                                                                    btn_reject.setVisibility(View.INVISIBLE);
                                                                    videoRecord.setVisibility(View.VISIBLE);
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                Log.d("failed321", t.getMessage());
                                                                Log.d("failed4321", "asdasdasdqwe");
                                                            }
                                                        });
                                                    }
                                                });
                                                btn_reject.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        RetrofitClient.apiServices().ChallengeStatusUpdate(challengeID, ChallengeConstants.REJECTED).enqueue(new Callback<ResponseBody>() {
                                                            @Override
                                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                if (response.isSuccessful()) {
//                                Intent i = new Intent(getApplicationContext(), MainNavigationActivity.class);
//                                startActivity(i);
//                                finish();
                                                                    btn_accept.setVisibility(View.INVISIBLE);
                                                                    btn_reject.setVisibility(View.INVISIBLE);
                                                                    txt_challenge_Status_description.setVisibility(View.VISIBLE);
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                Log.d("failed4321", t.getMessage());

                                                            }
                                                        });
                                                    }
                                                });
                                            } else if (Challenger.challenge_Approval_Status == ChallengeConstants.ACCEPTED) {
                                                Log.d("videoRecord", "videoRecord auri");
                                                videoRecord.setVisibility(View.VISIBLE);
                                                btn_accept.setVisibility(View.INVISIBLE);
                                                btn_reject.setVisibility(View.INVISIBLE);

                                            }
                                        } else {
                                            btn_accept.setVisibility(View.INVISIBLE);
                                            btn_reject.setVisibility(View.INVISIBLE);
                                            videoRecord.setVisibility(View.INVISIBLE);
                                            txt_challenge_Status_description.setText("This challenge time is over, you cannot record video anymore.");
                                            txt_challenge_Status_description.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                Functions.progress(getContext()).hide();
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                Log.d("failed4321", t.getMessage());
                                Functions.progress(getContext()).hide();
                            }
                        });

                    } else if (Challenger.challenge_Expiry_Date > 0)
                        brief_timerText.start(Challenger.challenge_Expiry_Date);
                    Functions.progress(getContext()).hide();
                }

                @Override
                public void onFailure(Call<ChallengeById> call, Throwable t) {
                    Functions.progress(getContext()).hide();
                }

            });
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {


            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_marketer_challenge_brief, container, false);
            ButterKnife.bind(this, v);

            Bundle bundle = this.getArguments();
            s = new Session(getContext());
            user_ID = s.getUserID();
            challengeID = bundle.getInt("challengeID");
            txt_challenge_Status_description.setVisibility(View.INVISIBLE);

            btn_accept.setVisibility(View.INVISIBLE);
            btn_reject.setVisibility(View.INVISIBLE);
            videoRecord.setVisibility(View.INVISIBLE);
            videoRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakeVideoIntent();
                }
            });


            return v;
        }
        catch (Exception e)
        {
            return inflater.inflate(R.layout.fragment_marketer_challenge_brief, container, false);
        }
    }
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, Constants.REQUEST_VIDEO_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent intent) {
        if (requestCode == Constants.REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

            video_intent = intent;
            if(Functions.checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
                Log.d("permissionVideo", "onActivityResult: Granted");
                uploadVideoToServer(video_intent.getData());
            }else{
                ActivityCompat.requestPermissions(getActivity(),arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE );
                Log.d("permissionVideo", "onActivityResult: Denied");

            }
        }
    }


    public void showProgressBar()
    {
        progress = ProgressDialog.show(getActivity(), "",
                "Uploading Video", true);
        progress.show();
    }
    private static final int DOWNLOAD_NOTIFICATION_ID_DONE = 911;
    private static final String CHANNEL_ID = "3000";

    private void showNotification() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(createChannel());
            }
            notificationBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID);
        } else {
            notificationBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext());
        }

        notificationBuilder
                .setContentTitle("iDareNow")
                .setContentText("Video Uploaded")
                .setTicker("Video Successfully Submitted")
                .setSmallIcon(R.mipmap.ic_idare_launcher)
                .setOngoing(false)
                .setContentIntent(PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0))
                .setAutoCancel(true);
        if (notificationManager != null) {
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID_DONE, notificationBuilder.build());
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationChannel createChannel() {
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), importance);
        channel.setDescription(getString(R.string.channel_description));
        channel.enableLights(true);
        channel.setLightColor(Color.YELLOW);
        return channel;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        Log.d("AcceptRejectActivity", "onRequestPermissionsResult: ");
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(video_intent!=null){
                        Log.d("videoUri", "onRequestPermissionsResult: "+video_intent.getData().getPath());
                        uploadVideoToServer(video_intent.getData());
                    }
                } else {
                    Log.d("permisiondeny", "onRequestPermissionsResult: "+requestCode);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    public void uploadVideoToServer(Uri pathToVideoFile){
        String videoPath = FileUtils.getRealPathFromURI(getActivity(),pathToVideoFile);
//        String videoPath = pathToVideoFile.getPath();
        Log.d("VideogetPath",videoPath);

        Log.d("challenge_id",String.valueOf(challengeID));
        File sdDir = Environment.getExternalStorageDirectory();
        File videoFile = new File(videoPath);
//        RequestBody description = RequestBody.create(MultipartBody.FORM,des.getText().toString());
//        RequestBody id = RequestBody.create(MultipartBody.FORM,String.valueOf(ChallengeID));
        RequestBody videoBody = RequestBody.create(MediaType.parse(videoPath), videoFile);
        MultipartBody.Part vFile = MultipartBody.Part.createFormData("filePart", videoFile.getName(), videoBody);
        Log.d("VideogetName",videoFile.getName());
        if(videoFile.exists()) {
            Log.d("videoFile","exists");

            if (videoFile.canRead()) {
                Log.d("videoFile","canRead");
                showProgressBar();
                RetrofitClient.apiServices().postChallengeVideo(vFile, "descrop", challengeID,s.getUserID()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("uploadedvid", "responseRecieved");
                            Toast.makeText(getActivity().getApplicationContext(),"Video Uploaded Successfully",Toast.LENGTH_LONG).show();
                            progress.dismiss();
                            showNotification();
                            ActivityManager mngr = (ActivityManager) getActivity().getSystemService( ACTIVITY_SERVICE );

                            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                            if(taskList.get(0).numActivities == 1 ) {
                                Intent i = new Intent(getActivity().getApplicationContext(), MainNavigationActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                getActivity().finish();
                                Log.i("if LAst", "This is last activity in the stack");
                            }
                            else
                                getActivity().onBackPressed();



                        } else {
                            Log.d("uploadedvid", response.message());
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("uploadedvid", "failed");
                        progress.dismiss();
                    }
                });

            }else {
                Log.d("unreadable", "uploadVideoToServer: ");
            }
        }else {
            Log.d("notexists", "uploadVideoToServer: ");
        }
    }
}
