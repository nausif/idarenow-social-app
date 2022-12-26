package com.alpharelevant.idarenow.data.utils;

/**
 * Created by Tabraiz on 11/20/2017.
 */

public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String userId = "user_id";
    public static final String timeToLive = "ttl";
    public static final String search_id = "search_id";
    public static final String shared_pref = "SHARED_PREFERENCES";
//    public static final String ip_port_conn = "192.168.20.244:50017";
    public static final String ip_port_conn = "192.168.0.5:50017";
//    public static final String ip_port_conn = "http://localhost:50017";
    public static final String images_path = "/Images/";
    public static final String icons_path = "/Images/icons";
    public static final String video_path = "/Video/";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int REQUEST_VIDEO_CAPTURE = 1;


}