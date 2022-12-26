package com.alpharelevant.idarenow.data.models.CustomModel;

/**
 * Created by Tabraiz on 12/1/2017.
 */

public  class  ChallengeFromUser{
    private String des;
    private String title;
    private  int currentTime;
    private  int durationTime;
    private  int challengeType;
    private  int challengeStatus;
    private  int challengeFrom;
    private  int challengeTo;


    public ChallengeFromUser(int challengeType,int challengeFrom,int challengeTo,String descr,String title,int currentTime,int durationTime,int challengeStatus){
        this.challengeType  = challengeType;
        this.challengeFrom = challengeFrom;
        this.challengeTo = challengeTo;
        this.des = descr;
        this.title = title;
        this.currentTime = currentTime;
        this.durationTime = durationTime;
        this.challengeStatus = challengeStatus;
    }
}