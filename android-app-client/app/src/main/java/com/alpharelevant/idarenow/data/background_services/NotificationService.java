package com.alpharelevant.idarenow.data.background_services;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.MarketerChallengeActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.AcceptRejectActivity;
import com.alpharelevant.idarenow.data.models.CustomModel.notificationResponse;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by NAUSIF on 07-Feb-18.
 */

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return notificationServiceBinder;
    }
    private final IBinder notificationServiceBinder = new NotificationServiceBinder();
    Session session;
    Timer mTimer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            session = new Session(getApplicationContext());
            if(session.getUserID()>0) {
                Log.d("background service","hitter");
                RetrofitClient.apiServices().checkPendingAssignChallenges(session.getUserID(),3).enqueue(new Callback<notificationResponse[]>() {
                    @Override
                    public void onResponse(Call<notificationResponse[]> call, Response<notificationResponse[]> response) {
                        if(response.isSuccessful()) {
                            for (notificationResponse item:response.body()) {
                                checkPendingAssignChallenge(item);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<notificationResponse[]> call, Throwable t) {

                    }
                });
                RetrofitClient.apiServices().checkPendingAcceptChallenge(session.getUserID()).enqueue(new Callback<notificationResponse[]>() {
                    @Override
                    public void onResponse(Call<notificationResponse[]> call, Response<notificationResponse[]> response) {
                        if(response.isSuccessful()) {

                            notificationResponse[] acceptChallengesIds = response.body();
                            if(acceptChallengesIds!=null)
                                for (notificationResponse item:acceptChallengesIds) {
                                Log.d("notification_id",String.valueOf(item.notification_id));
                                    checkPendingAcceptChallenges(item);
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<notificationResponse[]> call, Throwable t) {

                    }
                });
            }
        }

    };

    private void checkPendingAssignChallenge(notificationResponse _notificationResponse) {
        Context c = getApplicationContext();
//        Intent myIntent  = new Intent(getApplicationContext(),AcceptRejectActivity.class);
//        myIntent.putExtra("challengeID",String.valueOf(_notificationResponse.challege_id));
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent myIntent = new Intent(getApplicationContext(), MarketerChallengeActivity.class);
        myIntent.putExtra("challengeID", String.valueOf(_notificationResponse.challege_id));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent pendingIntent = PendingIntent.getActivity(c,(int)System.currentTimeMillis(),myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationHandler(pendingIntent,_notificationResponse,c);
        Log.d("assignchallenge",String.valueOf(_notificationResponse.challege_id));

//        Log.d("descrition",_notificationResponse.description);

    }
    private void checkPendingAcceptChallenges(   notificationResponse _notificationResponse) {

        if (_notificationResponse != null) {
            Context c = getApplicationContext();
            Intent myIntent = new Intent(c, MainNavigationActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationHandler(pendingIntent, _notificationResponse, c);
        }
    }
    private void notificationHandler(PendingIntent pendingIntent, notificationResponse _notificationResponse, Context c){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "123123123")
                    .setSmallIcon(R.mipmap.ic_idare_launcher)
                    .setContentTitle(_notificationResponse.from_name + " challenged you.")
                    .setContentText(_notificationResponse.title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(_notificationResponse.notification_id, mBuilder.build());
        }else{
            Notification.Builder builder= new Notification.Builder(c)
                    .setContentTitle(_notificationResponse.from_name + " challenged you.")
                    .setContentText(_notificationResponse.title)
                    .setSmallIcon(R.mipmap.ic_idare_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(c.NOTIFICATION_SERVICE);
            notificationManager.notify(_notificationResponse.notification_id,notification);
        }


    }


    public  class NotificationServiceBinder extends Binder {
        public  NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
        mTimer = new Timer();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mTimer.schedule(timerTask,100,10 * 1000);
    }


    @Override
    public ComponentName startService(Intent service) {
        Log.d("startSerice","times");
        return super.startService(service);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onstartcomand","asd");
//        return super.onStartCommand(intent,flags,startId);
            return START_STICKY;
    }

}
