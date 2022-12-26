package com.alpharelevant.idarenow.data.models.CustomModel;

/**
 * Created by Tabraiz on 2/10/2018.
 */
public class notificationResponse {
    public int notification_id=0;
    public int from_id=0;
    public int to_id = 0;
    public String title="";
    public String description ="";
    public String profile_image = "";
    public int challege_id=0;
    public String from_name = "";
    public String date_time="";
    public int approval_status =0;

    public notificationResponse(int notification_id, int from_id, int to_id, String title, String description, String profile_image, int challege_id, String date_time,String user_FullName,int approval_status) {
        this.notification_id = notification_id;
        this.from_id = from_id;
        this.to_id = to_id;
        this.title = title;
        this.description = description;
        this.profile_image = profile_image;
        this.challege_id = challege_id;
        this.date_time = date_time;
        this.from_name = user_FullName;
        this.approval_status = approval_status;
    }
}
