package com.alpharelevant.idarenow.data.models.CustomModel;

import android.util.Log;

import com.alpharelevant.idarenow.data.Adapters.NewsFeedViewAdapter;

/**
 * Created by NAUSIF on 28-Feb-18.
 */

public class NewsfeedModel {


    public Integer getPost_approve_reject() {
        return post_approve_reject;
    }
    public Integer getChallenge_id() {
        return challenge_id;
    }


    public String getUserImageURL() {
        return userImageURL;
    }

    public String getUserName() {
        return userName;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getVideoID() {
        return videoID;
    }

    public String getChallenged_by() {
        return challenged_by;
    }
    public int getChallengedToUserId() {
        return challengeToUserId;
    }
    public int getChallengedFromUserId() {
        return challengeFromUserId;
    }

    public String getChallenged_tittle() {
        return challenged_tittle;
    }

    private  int post_approve_reject;
    private Integer challenge_id;
    private String userImageURL;
    private String userName;
    private String videoURL;
    private String dateTime;
    private int videoID;
    private String challenged_by;
    private String challenged_tittle;
    private int challengeToUserId;
    private int challengeFromUserId;
    public Long challenge_Expiry_Date;
    public Integer winner_ID;
    public int approve_ratio;
    public int reject_ratio;
    public NewsfeedModel(Integer challenge_id, String userImageURL, String userName, String videoURL, String dateTime, int videoID, String challenged_by, String challenged_tittle,int challengeToUserId,int challengeFromUserId, int post_approve_reject,Long challenge_Expiry_Date, Integer winner_ID,Integer approve_ratio,Integer reject_ratio) {
        this.challengeToUserId = challengeToUserId;
        this.challengeFromUserId = challengeFromUserId;
        this.userImageURL = userImageURL;
        this.userName = userName;
        this.videoURL = videoURL;
        Log.d("video_url", "NewsfeedModel: "+videoURL);
        this.dateTime = dateTime;
        this.videoID = videoID;
        this.challenged_by = challenged_by;
        this.challenged_tittle = challenged_tittle;
        this.challenge_id = challenge_id;
        this.post_approve_reject = post_approve_reject;
        this.challenge_Expiry_Date = challenge_Expiry_Date;
        this.winner_ID = winner_ID;
        this.approve_ratio = approve_ratio;
        this.reject_ratio = reject_ratio;

    }

}
