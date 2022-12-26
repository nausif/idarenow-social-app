package com.alpharelevant.idarenow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.models.User_Accounts;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SignupStep2Fragment extends Fragment {




    public SignupStep2Fragment() {
        // Required empty public constructor
    }

    @BindView(R.id.EdtxtSignupFullName)
    EditText Fullname;
    static EditText Birthdate;
    @BindView(R.id.btnSignupStep2Back)
    Button btnBack;
    @BindView(R.id.btnSignupStep2Next)
    Button btnNext;
    @BindView(R.id.radioSignupMale)
    RadioButton radiobtn;
    private FragmentActivity myContext;
    User_Accounts ua = new User_Accounts();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_step2, container, false);
        ButterKnife.bind(this,v);
        Birthdate = (EditText) v.findViewById(R.id.EdtxtSignupBirthdate);
        ua.setUserGender("MALE");
        Birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });


        Button btnBack = (Button) v.findViewById(R.id.btnSignupStep2Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new SignupStep1Fragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place, fr).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save User
                if(Fullname.getText().length() >= 5) {
                    ua.setUserEmail(getArguments().getString("Email"));
                    ua.setUserPassword(getArguments().getString("Pass"));
                    ua.setUserFullName(Fullname.getText().toString());
                    if (radiobtn.isChecked()) {
                        ua.setUserGender("MALE");
                    } else
                    {
                        ua.setUserGender("FEMALE");
                    }
                    register_User();
                }
                else
                    Toast.makeText(getActivity(), "Fullname should be 5 character long and not empty", Toast.LENGTH_LONG).show();

            }
        });

        return v;
    }


    public void register_User()
    {
        APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);
        apiServices.postRegisterUser(ua).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                Log.d("Success123 :", response.toString() + "/n" + call.toString());
                Boolean json = response.body().booleanValue();
                if(json)
                {
                    Toast.makeText(getActivity(),"Signup Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainNavigationActivity.class));
                }
                else
                    Toast.makeText(getActivity(), "Failed to submit", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure :/", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(myContext.getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Birthdate.setText((((day) < 10) ? ("0" + (day)) : (day)) + "/" + (((month + 1) < 10) ? ("0" + (month+1)) : (month+1)) + "/" + year);
        }
    }

}
