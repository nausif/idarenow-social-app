package com.alpharelevant.idarenow.data.models;
/**
 * Created by Tabraiz on 6/29/2018.
 */

public class baseFeedModel {
    public String title;
    public String msg;
    public String profile_image;
    public int otherId;
    public baseFeedModel( String title,String secondline,String img,int otherId) {
        this.title = title;
        this.msg = secondline;
        this.profile_image = img;
        this.otherId = otherId;
    }
}
