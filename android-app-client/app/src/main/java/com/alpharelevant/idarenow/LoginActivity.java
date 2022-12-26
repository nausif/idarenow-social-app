package com.alpharelevant.idarenow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    static EditText edtxtEmail;

    @BindView(R.id.edtxtPass)
    EditText edtxtPass;

    @BindView(R.id.btnLogin)
    Button btnLogin;
    //-----------My Work --------------------------
    @BindView(R.id.login_button)
    LoginButton login_button;
    CallbackManager callbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    ProfileTracker mProfileTracker;
    @BindView(R.id.tvForgotPassword)
    TextView txtForgotPassword;

    private ProgressDialog progress;

    Integer user_id;
    private void assigning_fields(){
        callbackManager = CallbackManager.Factory.create();
        edtxtEmail = (EditText)findViewById(R.id.edtxtEmail);
        edtxtEmail.setText("abc@gmail.com");
        edtxtPass.setText("123");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void loginToMyFbApp() {
        Log.d("Log2ger","loginToMyFbApp");
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("Logger","has fb token");

//            t.setText(t.getText().toString()+"\n Has access token assigning tracker callback");
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if(currentAccessToken == null) {
                        Log.d("Logger","Has no access token");
                    }
                    else {
                        Log.d("Logger","Has access token");
                        fetchProfile();
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        }
        else {
            Log.d("Logger","else has fb token");

            login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    fetchProfile();
                }
                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(),"Facebook Login Cancelled",Toast.LENGTH_LONG);
                }
                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(),"Facebook Error ",Toast.LENGTH_LONG);
                }
            });
        }
    }

    public  void fetchProfile() {
        if(AccessToken.getCurrentAccessToken()!=null) {
            showProgressBar();
            Log.d("fetchProfile","asd");

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(final JSONObject object, GraphResponse response) {
                            Log.d("records",object.toString());
                            if(Functions.isValid(object.optString("email").toString()) && Functions.isValid(object.optString("name").toString())){

                                RetrofitClient.apiServices().fbCheckAndSave(object.optString("email").toString(),object.optString("name").toString()).enqueue(new Callback<Integer>() {
                                    @Override
                                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                                        Log.d("response fbChecknsave",response.message());
                                        if(response.isSuccessful()){
                                            user_id = response.body().intValue();
                                            Log.d("fb_login_id",String.valueOf(user_id));
                                            if(user_id>0) {
                                                Session s = new Session(getApplicationContext());
                                                s.clearSessionslocal();
                                                s.setUserId_ttl(user_id,s.get24hourahead());
                                                Intent i = new Intent(getApplicationContext(), MainNavigationActivity.class);
                                                startActivity(i);
                                            }else{
                                                finish();
                                                s.clearSessionslocal();
                                            }
                                        }else{
                                            Log.d("failedapi","party");
                                            Log.d("response",response.message().toString());
                                        }
                                        progress.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<Integer> call, Throwable t) {
                                        progress.dismiss();
                                    }
                                });


                            }else{
                                Toast.makeText(getApplicationContext(),"facebook cannot fetch email",Toast.LENGTH_LONG);
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,link"); //write the fields you need
            request.setParameters(parameters);
            request.executeAsync();
        }
    }



    //-----------My Work --------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        assigning_fields();
        loginToMyFbApp();


        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

                final EditText edittext = new EditText(LoginActivity.this);
                edittext.setSingleLine();
                edittext.setCompoundDrawablePadding(15);
                edittext.setTextSize(22);
                edittext.setHint("example@domain.com");
                float pad = getResources().getDisplayMetrics().density;
                int pix = (int)(pad * 25);
                edittext.setPadding(pix,5,pix,5);
                edittext.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                edittext.setBackgroundColor(Color.parseColor("#00000000"));
                edittext.setTextColor(Color.parseColor("#A9A9A9"));
                alert.setTitle("Enter your Email Address");
                alert.setView(edittext);
                showKeyboard(edittext);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        final String emailAddress = edittext.getText().toString();

                        if(emailAddress.equalsIgnoreCase("")){

                            Toast.makeText(LoginActivity.this,"Email Address Cannot be Empty", Toast.LENGTH_LONG).show();

                        }
                        else{

                            if(emailValidator(emailAddress)){

                                showProgressDialog("");

                                RetrofitClient.apiServices().getForgetPassword(emailAddress).enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                                        if(response.isSuccessful())
                                        {
                                            Boolean boolvalue = response.body().booleanValue();
                                            if(boolvalue)
                                            {
                                                Toast.makeText(LoginActivity.this, "An email has been sent to "+ emailAddress +" with your account password", Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(LoginActivity.this, "We cannot find this email address in our database.", Toast.LENGTH_LONG).show();
                                            }
                                            hideProgressDialog();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                        hideProgressDialog();
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,"Please provide a valid email address. ", Toast.LENGTH_LONG).show();
                            }

                        }


                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_a = edtxtEmail.getText().toString();
                String password_a = edtxtPass.getText().toString();
                if(email_a.equals("") || password_a.equals("")) {
                    Toast.makeText(getApplicationContext(),"Enter Details",Toast.LENGTH_SHORT).show();
                } else {
                    if(emailValidator(email_a)) {
                        showProgressBar();
                        checkAuthentication(email_a, password_a);
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Please provide a valid email address. ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
    public void showProgressBar()
    {
        progress = ProgressDialog.show(LoginActivity.this, "",
                "Please Wait", true);
        progress.show();
    }

    public void redirectToHome(int user_id){
        s =new Session(getApplicationContext());
        s.clearSessionslocal();
        s.setUserId_ttl(user_id,s.get24hourahead());
//        Toast.makeText(getApplicationContext(),"Login Successful user id : "+user_id,Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(LoginActivity.this, BaseMessageActivity.class));
        finish();
        startActivity(new Intent(LoginActivity.this, MainNavigationActivity.class));
    }
    Session s;
    public void checkAuthentication(String email,String password) {

        Log.d("checkAuthentication","invoked");

        RetrofitClient.apiServices().getAuthentication(email, password).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer user_id = response.body().intValue();
                Log.d("authentication",user_id.toString());
                if(user_id>0)
                {
                    redirectToHome(user_id);
                    progress.dismiss();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Check email and password.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("Failed Method", t.toString() + "\n" + call.toString());
                progress.hide();
            }
        });


    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void showKeyboard(EditText editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(EditText editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    private void showProgressDialog(final String message){



        new Thread(new Runnable() {
            @Override
            public void run()
            {
                // do the thing that takes a long time

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        progress = ProgressDialog.show(LoginActivity.this, "",
                                message, true);

                    }
                });
            }
        }).start();


    }

    private void hideProgressDialog(){

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                // do the thing that takes a long time

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        progress.dismiss();
                    }
                });
            }
        }).start();

    }

}
