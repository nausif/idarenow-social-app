package com.alpharelevant.idarenow;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeFromUser;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChallengeActivity extends AppCompatActivity {

   // just checking
    Button btn_submit;
    EditText title;
    TextInputLayout des;
    int search_user_id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_challenge);
        btn_submit = (Button)findViewById(R.id.submit);
        title=(EditText)findViewById(R.id.title);
        des =(TextInputLayout )findViewById(R.id.description);
        search_user_id = Integer.valueOf(getIntent().getExtras().getString(Constants.search_id));
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitClient.apiServices().getChallengeIdByType("normal").enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()){
                           Integer a = response.body().intValue();
                           if(a.intValue()>0){
                               Session s=new Session(getApplicationContext());
                               Log.d("textFetch",des.getEditText().getText().toString());
                                ChallengeFromUser challengeFromUser =new ChallengeFromUser(a.intValue(),s.getUserID(),search_user_id,des.getEditText().getText().toString(),title.getText().toString(),s.getCurrentTimestamp(),s.get24hourahead(),1);
                                challenge_user(challengeFromUser);
                           }
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });
            }
        });

    }
    public void challenge_user(ChallengeFromUser challengeFromUser){
        RetrofitClient.apiServices().setChallengeFromUser(challengeFromUser).enqueue(new Callback<Boolean>() {
    @Override
    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

        if(response.isSuccessful()){
            Toast.makeText(getApplicationContext(),"Challenge Successful",Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    public void onFailure(Call<Boolean> call, Throwable t) {

    }
        });
    }
}
