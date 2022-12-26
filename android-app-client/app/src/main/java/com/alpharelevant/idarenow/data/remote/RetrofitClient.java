package com.alpharelevant.idarenow.data.remote;

import com.alpharelevant.idarenow.data.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by NAUSIF on 27-Oct-17.
 */

public class RetrofitClient {


    public static final String BASE_URL = "http://"+ Constants.ip_port_conn + "/api/";

    private static Retrofit retrofit = null;

    // Implementing Singleton Pattern to create only once instance of RetroClient
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static APIServices apiServices(){
        return  RetrofitClient.getClient().create(APIServices.class);
    }

}
