package com.alpharelevant.idarenow.data.models.CustomModel;

import android.text.format.DateUtils;

/**
 * Created by Tabraiz on 6/21/2018.
 */

public class MessageFormat {
    public MessageFormat(int to_id ,int from_id,String from_id_name,String from_id_img,String Message_Description,String message_Timestamp){
        this.to_id =to_id;
        this.from_id = from_id;
        this.from_id_name = from_id_name;
        this.from_id_img = from_id_img;
        this.message_Description= Message_Description;
        this.message_Timestamp = message_Timestamp;
    }
    public int msg_id ;
    public int from_id ;
    public String from_id_name ;
    public String from_id_img ;
    public int to_id ;
    public String message_Timestamp ;
    public String message_Description ;
//    public Nullable<bool> Message_IsRead { get; set; }
}
