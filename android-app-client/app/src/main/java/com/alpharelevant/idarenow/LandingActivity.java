package com.alpharelevant.idarenow;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.background_services.NotificationService;
import com.alpharelevant.idarenow.data.utils.Session;
import com.alpharelevant.idarenow.data.utils.StaticObjects;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import org.json.JSONObject;

public class LandingActivity extends AppCompatActivity {
    public Session session;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
//        getSupportActionBar().hide();
        StaticObjects.session = new Session(getApplicationContext());
        session =StaticObjects.session;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            if (session.checkUserSessionExists()) {
                int id = session.getUserID();
                if (id > -1) {

                    Intent i = new Intent(this, MainNavigationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", String.valueOf(id));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        } catch (Exception e){}
        // Changing Activity
        final Button btnLogin = (Button) findViewById(R.id.btnSignin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
            }
        });

        final TextView tv = (TextView) findViewById(R.id.txtCreateAcc);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingActivity.this, SignupActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(session==null){
            StaticObjects.session = new Session(getApplicationContext());
            if (session.checkUserSessionExists()) {
                int id = session.getUserID();
                if (id > -1) {

                    Intent i = new Intent(this, MainNavigationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", String.valueOf(id));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        }

    }

    public  void fetchProfile() {
        if(AccessToken.getCurrentAccessToken()!=null) {
            progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setMessage("Check Facebook Login....");
            progressDialog.show();
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            session.clearSessionslocal();
                            progressDialog.dismiss();
//                            startActivity(new Intent(getApplicationContext(), MainNavigationActivity.class));
//                            finish();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,link"); //write the fields you need
            request.setParameters(parameters);
            request.executeAsync();
        }
    }


}
