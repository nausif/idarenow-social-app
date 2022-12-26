package com.alpharelevant.idarenow.data.utils;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.concurrent.TimeUnit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tabraiz on 11/9/2017.
 */

public class Session {

    SharedPreferences mSharedPreferences;
    public Session(Context cntx) {
        mSharedPreferences = getApplicationContext().getSharedPreferences(Constants.shared_pref, 0);
    }

    public void setCookie(String cookieName,String cookieValue) {
        mSharedPreferences.edit().putString(cookieName, cookieValue).apply();
    }

    public String getValuebyName(String cookieName) {
        SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences(Constants.shared_pref, 0);
        if(mSharedPreferences != null)
            if(mSharedPreferences.contains(cookieName))
                return mSharedPreferences.getString(cookieName, null);
        return "";
    }
    public void setUserId_ttl(int _userId,int ttl){

        mSharedPreferences.edit().putString(Constants.userId, String.valueOf(_userId)).apply();
        mSharedPreferences.edit().putString(Constants.timeToLive, String.valueOf(ttl)).apply();

    }



    public int getUserID(){

            String u_i = getValuebyName(Constants.userId);

            if (u_i != "" && u_i.length() > 0) {
                return Integer.parseInt(u_i);

            } else
                return -1;

    }
    public void clearallSessions(){
        mSharedPreferences .edit().clear().commit();
    }
    public void clearSessionslocal(){
            mSharedPreferences.edit().remove(Constants.userId).commit();
            mSharedPreferences.edit().remove(Constants.search_id).commit();
            mSharedPreferences.edit().remove(Constants.timeToLive).commit();

    }
    public int getCurrentTimestamp(){
        return ((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) ;
    }
    public  int get24hourahead(){
        return getCurrentTimestamp()+60*60*24;
    }
   public boolean checkUserSessionExists(){

       String user_id= getValuebyName(Constants.userId);
       String ttl= getValuebyName(Constants.timeToLive);
       if(user_id != null && ttl != null)
           if(Integer.parseInt(user_id)>-1 && Integer.parseInt(ttl)> getCurrentTimestamp())
                return true;
       clearSessionslocal();
       return false;
    }

}