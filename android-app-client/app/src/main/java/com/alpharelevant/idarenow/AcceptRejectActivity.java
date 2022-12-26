package com.alpharelevant.idarenow;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeById;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.FileUtils;
import com.alpharelevant.idarenow.data.utils.Functions;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

import static butterknife.internal.Utils.arrayOf;

public class AcceptRejectActivity extends AppCompatActivity {

    TextView title;
    TextView des;
    ImageView profile_image;
    Button accept;
    Button reject;
    Button videoRecord;

    public ChallengeById Challenger;
    int ChallengeID;
    boolean issAccepted = false;
    Intent video_intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            Log.d("reachhere", "asdasd");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.challenge_accept_reject_view);
            videoRecord = (Button) findViewById(R.id.videoRecord);
            videoRecord.setVisibility(View.INVISIBLE);
            videoRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakeVideoIntent();
                }
            });
            String approval_status = getIntent().getExtras().getString("approval_status");
            Log.d("approval_Activity", "onCreate: " + approval_status);
            ChallengeID = Integer.valueOf(getIntent().getExtras().get("challengeID").toString());
            if (ChallengeID <= 0) {
                Log.d("returning", "asdasd");
                return;
            }

            title = findViewById(R.id.title_accept_reject);
            des = findViewById(R.id.descriptionText_accept_reject);
            profile_image = findViewById(R.id.challenger_image);
            accept = findViewById(R.id.accept_btn_accept_reject);
            reject = findViewById(R.id.reject_btn_accept_reject);

            RetrofitClient.apiServices().getChallengeById(ChallengeID).enqueue(new Callback<ChallengeById>() {
                @Override
                public void onResponse(Call<ChallengeById> call, Response<ChallengeById> response) {
                    Challenger = response.body();

                    getProfileImage(Challenger.challenge_From_ID);
                    title.setText(Challenger.challenge_Tittle);
                    des.setText(Challenger.challenge_Description);
                }

                @Override
                public void onFailure(Call<ChallengeById> call, Throwable t) {

                }
            });
            if (Integer.valueOf(approval_status) == ChallengeConstants.INITIAL) {
                accept.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        RetrofitClient.apiServices().ChallengeStatusUpdate(ChallengeID, ChallengeConstants.ACCEPTED).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    accept.setVisibility(View.INVISIBLE);
                                    reject.setVisibility(View.INVISIBLE);
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
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RetrofitClient.apiServices().ChallengeStatusUpdate(ChallengeID, ChallengeConstants.REJECTED).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Intent i = new Intent(getApplicationContext(), MainNavigationActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("failed4321", t.getMessage());

                            }
                        });
                    }
                });
            } else if (Integer.valueOf(approval_status) == ChallengeConstants.ACCEPTED) {
                Log.d("videoRecord", "videoRecord auri");
                videoRecord.setVisibility(View.VISIBLE);
                accept.setVisibility(View.INVISIBLE);
                reject.setVisibility(View.INVISIBLE);

            } else {
                Log.d("showNone", "RipCase");
                videoRecord.setVisibility(View.INVISIBLE);
                accept.setVisibility(View.INVISIBLE);
                reject.setVisibility(View.INVISIBLE);
            }


        } catch (Exception e) {
        }


    }

    public void getProfileImage(int id) {

        try {

            RetrofitClient.apiServices().getProfileImage(id).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Bitmap bmp = null;
                        try {
                            byte[] asd = response.body().bytes();
                            bmp = BitmapFactory.decodeByteArray(asd, 0, asd.length);
                            profile_image.setImageBitmap(Bitmap.createScaledBitmap(bmp, profile_image.getWidth(), profile_image.getHeight(), false));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("Imagesfetched", "failedprofile");
                }
            });
        } catch (Exception e) {

        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, Constants.REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {


            if (requestCode == Constants.REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

                video_intent = intent;
                if (Functions.checkPermissionREAD_EXTERNAL_STORAGE(getApplicationContext())) {
                    Log.d("permissionVideo", "onActivityResult: Granted");
                    uploadVideoToServer(video_intent.getData());
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    Log.d("permissionVideo", "onActivityResult: Denied");

                }
            }
        } catch (Exception ex) {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {


            Log.d("AcceptRejectActivity", "onRequestPermissionsResult: ");
            switch (requestCode) {
                case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (video_intent != null) {
                            uploadVideoToServer(video_intent.getData());
                        }
                    } else {
                        Log.d("permisiondeny", "onRequestPermissionsResult: " + requestCode);
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        } catch (Exception e) {

        }
    }

    public void uploadVideoToServer(Uri pathToVideoFile) {
        try {

            String videoPath = FileUtils.getRealPathFromURI(this, pathToVideoFile);
            Log.d("VideogetPath", videoPath);

            Log.d("challenge_id", String.valueOf(ChallengeID));
            File videoFile = new File(videoPath);
            RequestBody videoBody = RequestBody.create(MediaType.parse(videoPath), videoFile);
            MultipartBody.Part vFile = MultipartBody.Part.createFormData("filePart", videoFile.getName(), videoBody);
            Log.d("VideogetName", videoFile.getName());
            if (videoFile.exists()) {
                Log.d("videoFile", "exists");

                if (videoFile.canRead()) {
                    Log.d("videoFile", "canRead");

                    RetrofitClient.apiServices().postChallengeVideo(vFile, "descrop", ChallengeID, Challenger.challenge_To_User_ID).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("uploadedvid", "responseRecieved");
                                finish();
                            } else {
                                Log.d("uploadedvid", response.message());

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("uploadedvid", "failed");

                        }
                    });
                } else {
                    Log.d("unreadable", "uploadVideoToServer: ");
                }
            } else {
                Log.d("notexists", "uploadVideoToServer: ");
            }

        } catch (Exception e) {
        }
    }
}
