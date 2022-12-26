package com.alpharelevant.idarenow;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.models.CustomModel.MarketerChallengeModel;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.quantityview.QuantityView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignMarketerChallenge extends Activity {

    @BindView(R.id.marketer_challenge_tittle)
    EditText txt_marketer_challenge_tittle;

    @BindView(R.id.marketer_challenge_description)
    EditText getTxt_marketer_challenge_description;

    @BindView(R.id.quantityView_duration_days)
    QuantityView quantityViewDays;

    @BindView(R.id.quantityView_price)
    QuantityView quantityViewPrice;

    @BindView(R.id.marketer_create_challenge_btn)
    Button btn_marketer_Create_Challenge;

    MarketerChallengeModel mcm = new MarketerChallengeModel();

    int challenge_to_userID;
    int user_Account_Balance;


    Session s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_marketer_challenge);
        ButterKnife.bind(this);
        s = new Session(getApplicationContext());
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignMarketerChallenge.super.onBackPressed();
            }
        });

        RetrofitClient.apiServices().getUserTotalBalance(s.getUserID()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                user_Account_Balance = response.body().intValue();
                if(user_Account_Balance > 500)
                    quantityViewPrice.setMaxQuantity(500);
                else
                quantityViewPrice.setMaxQuantity(user_Account_Balance);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        Bundle extras = getIntent().getExtras();
        challenge_to_userID = extras.getInt("challenge_to_ID");
        btn_marketer_Create_Challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcm.challenge_Tittle = txt_marketer_challenge_tittle.getText().toString();
                mcm.challenge_Description = getTxt_marketer_challenge_description.getText().toString();
                mcm.challenge_Amount = (double) quantityViewPrice.getQuantity();
                mcm.challenge_Duration = (long) quantityViewDays.getQuantity();
                mcm.challenge_From_ID = s.getUserID();
                mcm.challenge_To_ID = challenge_to_userID;
                if(mcm.challenge_Tittle.equals("") || mcm.challenge_Tittle.equals(null) || mcm.challenge_Description.length() < 20)
                {
                    Toast.makeText(getApplicationContext(),"Make sure that title is not left empty and not 50 character long. Description should 20 character long.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);
                    apiServices.postMarketerChallenge(mcm).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                            if (response.isSuccessful()) {
                                if(response.body().booleanValue() == true)
                                {
                                    Toast.makeText(getApplicationContext(),"Sucessfully Posted",Toast.LENGTH_SHORT).show();
                                    AssignMarketerChallenge.super.onBackPressed();
                                }
                                else  Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {

                        }
                    });
                }
            }
        });

    }



}
