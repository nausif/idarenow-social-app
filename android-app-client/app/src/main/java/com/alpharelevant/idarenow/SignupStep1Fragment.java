package com.alpharelevant.idarenow;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupStep1Fragment extends Fragment  {

    public SignupStep1Fragment() {
        // Required empty public constructor
    }

    @BindView(R.id.EdtxtSignupEmail)
    EditText Email;
    @BindView(R.id.EdtxtSignupPass)
    EditText Pass;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_signup_step1, container, false);
        ButterKnife.bind(this,v);
        Button btnNext = (Button) v.findViewById(R.id.btnSignupStep1Next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(emailValidator(Email.getText().toString()) &&  !Email.getText().toString().equals(""))
                    if(Pass.getText().length() >= 6) {
                        check_email_exist();
                    }
                    else
                    {
                        Toast.makeText(getActivity().getApplicationContext(),"Password Must Be Atleast 6 Characters",Toast.LENGTH_LONG).show();
                    }
                else
                    Toast.makeText(getActivity().getApplicationContext(),"Please provide a valid email address. ", Toast.LENGTH_LONG).show();



            }
        });

        return  v;

    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void check_email_exist()
    {
        APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);

        apiServices.getEmailExist(Email.getText().toString()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                Log.d("Success onResponse :", response.toString());
                Boolean json = response.body().booleanValue();
                if(!json)
                {
                    Bundle args = new Bundle();
                    args.putString("Email",Email.getText().toString());
                    args.putString("Pass",Pass.getText().toString());
                    Fragment fr = new SignupStep2Fragment();
                    fr.setArguments(args);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_place, fr).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else
                    Toast.makeText(getActivity(), "Email Already Exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d("Failed Method", t.toString() + "\n" + call.toString());
            }
        });
    }
}
