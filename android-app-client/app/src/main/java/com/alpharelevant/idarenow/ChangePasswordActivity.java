package com.alpharelevant.idarenow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Session;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.input_current_password)
    EditText input_current_password;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.input_confirmpassword)
    EditText input_confirmpassword;
    @BindView(R.id.btnChangePass)
    Button btnChangePass;
    @BindView(R.id.showPass)
    CheckBox chckShowPass;
    @BindView(R.id.show)
    TextView txtpassshow;
    Session s;
    int user_ID;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        s = new Session(ChangePasswordActivity.this);
        user_ID = s.getUserID();
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input_password.getText().toString().equals("") && !input_confirmpassword.getText().toString().equals("") && !input_current_password.getText().toString().equals("")) {
                    if (input_password.getText().toString().equals(input_confirmpassword.getText().toString())) {
                        RetrofitClient.apiServices().postChangePassword(user_ID, input_current_password.getText().toString(), input_confirmpassword.getText().toString()).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().booleanValue()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else
                                        Toast.makeText(ChangePasswordActivity.this, "Current Password is Wrong...", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {

                            }
                        });

                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "New password is not matching...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter Passwords", Toast.LENGTH_LONG).show();
                }
            }
        });

        txtpassshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chckShowPass.setChecked(!(chckShowPass).isChecked());
            }
        });
        chckShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    input_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    input_current_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    input_confirmpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else
                {
                    input_password.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input_confirmpassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input_current_password.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

    }
}
