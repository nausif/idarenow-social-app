package com.alpharelevant.idarenow;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;

public class SignupActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


//        // Signup Next Button Step 1
//        Button btnNext = (Button) findViewById(R.id.btnSignupStep1Next);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nextstepfragment();
//            }
//        });
//
//        // Signup Step 2 Back Button
//        Button btnBack = (Button) findViewById(R.id.btnSignupStep2Back);
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }





}
