package com.alpharelevant.idarenow.data.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tabraiz on 12/6/2017.
 */

public class Functions {
    public static  Session session ;
    //String
    public static  Boolean isValid(String a){
        if(a!="" && a!= null){
            return true;
        }
        return false;
    }
    static ProgressDialog progressDialog;
    public static ProgressDialog progress(Context context){
        if(progressDialog==null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        return  progressDialog;
    }




    public static boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("permission1", "checkPermissionREAD_EXTERNAL_STORAGE: "+String.valueOf(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)));
                return false;
            } else {
                return true;
            }
        } else {
            //low version apps get direct access
            return true;
        }
    }
    public static String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    public static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) {
            Log.d("errorImage","fetching IMage" + e.getMessage());
            // Log exception
            return null;
        }
    }
    public boolean profileType(){
        return  false;
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void getProfileImage(int id,final ImageView profileImage){

        RetrofitClient.apiServices().getProfileImage(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200 ) {
                    Bitmap bmp = null;
                    try {
                        byte[] asd = response.body().bytes();
                        bmp = BitmapFactory.decodeByteArray(asd, 0, asd.length);
                        profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, profileImage.getWidth(), profileImage.getHeight(), false));
                    } catch (IOException e) {
                        profileImage.setImageResource(R.drawable.sample_profile_image);
                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Imagesfetched","failedprofile");
            }
        });
    }
}
