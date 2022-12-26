package com.alpharelevant.idarenow.data.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.data.models.User_Accounts;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tabraiz on 11/20/2017.
 */

public class LoginService {
    Context c;



    public LoginService(){
        this.c =c;
    }

    public void checkAccountExistViaEmail(String em){
    }
    public void registerUser(User_Accounts user_accounts){

        APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);
        apiServices.postRegisterUser(user_accounts).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                Log.d("Success1234 :", response.toString() + "/n" + call.toString());
                if(response.body().booleanValue()){
                    Toast.makeText(getApplicationContext(),"user registered",Toast.LENGTH_SHORT).show();
//                    getApplicationContext().startActivity(new Intent(getApplicationContext(), MainNavigationActivity.class));

                }
                else
                    Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_SHORT).show();
//                Boolean json = response.body().booleanValue();
//                if(json)
//                {
//                }

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure :/", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
