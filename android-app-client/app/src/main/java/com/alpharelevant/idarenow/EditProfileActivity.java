package com.alpharelevant.idarenow;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.models.CustomModel.Cities;
import com.alpharelevant.idarenow.data.models.CustomModel.Countries;
import com.alpharelevant.idarenow.data.models.CustomModel.EditProfile;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.FileUtils;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alpharelevant.idarenow.data.utils.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

public class EditProfileActivity extends AppCompatActivity {




    EditText edit_Profile_full_name;
    static EditText edit_Profile_Birthday;
    EditText edit_Profile_email;
    EditText edit_Profile_country;
    EditText edit_Profile_city;
    TextView edit_txt_change_profile;
    CircularImageView edit_profile_image;
    EditProfile ed = new EditProfile();
    Session s;
    ProgressDialog progress;

    public void showProgressBar()
    {
        progress = ProgressDialog.show(this, "",
                "Please Wait", true);
        progress.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_profile);
            showProgressBar();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
//        if(!Functions.progress(getApplicationContext()).isShowing())
//            Functions.progress(this).show();
            s = new Session(getApplicationContext());
            int user_id = s.getUserID();
            edit_Profile_full_name = (EditText) findViewById(R.id.Edit_Name);
            edit_Profile_email = (EditText) findViewById(R.id.Edit_Email);
            edit_Profile_country = (EditText) findViewById(R.id.Edit_country_spinner);
            edit_Profile_city = (EditText) findViewById(R.id.Edit_city_spinner);
            edit_Profile_Birthday = (EditText) findViewById(R.id.Edit_ProfileBirthday);
            edit_profile_image = (CircularImageView) findViewById(R.id.Edit_profile_image);
            edit_txt_change_profile = (TextView) findViewById(R.id.Edit_txt_change_profile);
            putDataOnEditView(user_id);
            Functions.getProfileImage(user_id, edit_profile_image);


            edit_Profile_Birthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(v);
                }
            });

            edit_txt_change_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageupload(view);
                }
            });

            ImageView checkmark = (ImageView) findViewById(R.id.saveChanges);
            checkmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressBar();
                    if (saveProfileData()) ;
                    {
                        showProgressBar();
                        saveProfileImage();
                        Toast.makeText(getApplicationContext(), "Profile Edit Successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    progress.dismiss();
                }


            });

            ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditProfileActivity.super.onBackPressed();
                }
            });
        }
        catch (Exception e)
        {

        }
    }
    public void chooseimage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public  void imageupload(View v){

        if (Functions.checkPermissionREAD_EXTERNAL_STORAGE(this.getApplicationContext())) {
            Log.d("donner", "imageupload: "+"have_permission");
           chooseimage();
        }else {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("request_alowed", "MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: ");
                    chooseimage();
                } else {
                    Log.d("request_denied", "MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: ");
                    // permission denied, boo! Disable the
                }
                return;
            }

        }
    }

    public boolean saveProfileData() {
        ed.profile_id = responseEditProfile.profile_id;
        ed.full_name = edit_Profile_full_name.getText().toString();
        ed.birthday = edit_Profile_Birthday.getText().toString();
        ed.country_name = edit_Profile_country.getText().toString();
        ed.city_name = edit_Profile_city.getText().toString();
        ed.email = edit_Profile_email.getText().toString();

        int country_ID = 0;
        int city_ID = 0;
        for (int i = 0; i < responseEditProfile.countries.length; i++) {
            if (edit_Profile_country.getText().toString().equals(responseEditProfile.countries[i].country_Name)) {
                country_ID = responseEditProfile.countries[i].country_ID;
                break;
            }
        }
        for (int i = 0; i < listofCities.size(); i++) {
            if (edit_Profile_city.getText().toString().equals(listofCities.get(i).city_Name)) {
                city_ID = listofCities.get(i).city_ID;
                break;
            }
        }
        if (country_ID > 0) {
            ed.country_id = country_ID;
            if (city_ID > 0)
                ed.city_id = city_ID;
        }

        if (country_ID <= 0 || city_ID <= 0) {
            Toast.makeText(getApplicationContext(), "You entered InValid country or city",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else {

            APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);
            apiServices.postEditProfile(ed).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if(response.isSuccessful())
                    {
                        progress.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    progress.dismiss();
                }
            });
            return true;
        }
    }

    EditProfile responseEditProfile;
    public void putDataOnEditView(int user_id)
    {
        APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);
        apiServices.getProfileData(user_id).enqueue(new Callback<EditProfile>() {
            @Override
            public void onResponse(Call<EditProfile> call, Response<EditProfile> response) {

                if(response.isSuccessful()) {
                    Log.d("edit1", "entered");
                    responseEditProfile = response.body();
                    listofCities = Arrays.asList(responseEditProfile.cities);
                    Log.d("editreponse", responseEditProfile.full_name.toString());
                    edit_Profile_full_name.setText(responseEditProfile.full_name);
                    if (responseEditProfile.birthday != null) {
                        edit_Profile_Birthday.setText(responseEditProfile.birthday.toString());
                    }
                    edit_Profile_country.setText(responseEditProfile.country_name);
                    edit_Profile_city.setText(responseEditProfile.city_name);
                    edit_Profile_email.setText(responseEditProfile.email);
                    Log.d("edit2", "error");
                    addItemsOnSpinner2(responseEditProfile.countries,responseEditProfile.cities);
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<EditProfile> call, Throwable t) {
                Log.d("editfailed",t.toString());
                progress.dismiss();
            }
        });
    }

    public void getProfileImage(int id){
        RetrofitClient.apiServices().getProfileImage(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200 ) {
                    Bitmap bmp = null;
                    try {
                        byte[] asd = response.body().bytes();
                        bmp = BitmapFactory.decodeByteArray(asd, 0, asd.length);
                        edit_profile_image.setImageBitmap(Bitmap.createScaledBitmap(bmp, edit_profile_image.getWidth(), edit_profile_image.getHeight(), false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Imagesfetched","failedprofile");
            }
        });
    }

    AutoCompleteTextView tv;
    AutoCompleteTextView tv_city;
    String my_var_country; //keeps tracking!!
    String my_var_city;
    public void addItemsOnSpinner2(final Countries[] country, Cities[] cities) {

        List<String> list_country = new ArrayList<>();
        for (int i = 0; i < country.length; i++) {
            list_country.add(country[i].country_Name);
        }
        list_city.clear();
        for (int i = 0; i < listofCities.size(); i++) {
            list_city.add(cities[i].city_Name);
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list_country);

        final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list_city);

        tv_city = (AutoCompleteTextView) findViewById(R.id.Edit_city_spinner);
        tv_city.setAdapter(dataAdapter2);
        tv  = (AutoCompleteTextView) findViewById(R.id.Edit_country_spinner);
        tv.setAdapter(dataAdapter);


        tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                my_var_country = dataAdapter.getItem(position).toString();
                int country_ID = 0;
                for (int i = 0; i < responseEditProfile.countries.length; i++) {
                    if(edit_Profile_country.getText().toString().equals(responseEditProfile.countries[i].country_Name)) {
                        country_ID = responseEditProfile.countries[i].country_ID;
                        break;
                    }
                }
                if(country_ID > 0)
                {
                    getCities(country_ID);
                }


            }
        });
/**
 * Unset the var whenever the user types. Validation will
 * then fail. This is how we enforce selecting from the list.
 */
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                my_var_country = null;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        tv_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    my_var_city = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    List<Cities> listofCities = new ArrayList<>();
    List<String> list_city = new ArrayList<>();
    public void getCities(int country_id)
    {

        APIServices apiServices = RetrofitClient.getClient().create(APIServices.class);
        apiServices.getSelectedCitiesFromCountry(country_id).enqueue(new Callback<List<Cities>>() {
            @Override
            public void onResponse(Call<List<Cities>> call, Response<List<Cities>> response) {

                if(response.isSuccessful()) {
                    listofCities = response.body();
                    list_city.clear();
                    for (int i = 0; i < listofCities.size(); i++) {
                        list_city.add(listofCities.get(i).city_Name.toString());
                    }

                    if(list_city.size() > 0)
                        tv_city.setAdapter(new ArrayAdapter<String>(getBaseContext(),
                                android.R.layout.simple_dropdown_item_1line, list_city));
                }
            }

            @Override
            public void onFailure(Call<List<Cities>> call, Throwable t) {

            }
        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
            edit_Profile_Birthday.setText((((day) < 10) ? ("0" + (day)) : (day)) + "/" + (((month + 1) < 10) ? ("0" + (month+1)) : (month+1)) + "/" + year);
        }
    }
    private int PICK_IMAGE_REQUEST = 1;
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            try {
//                final Uri imageUri = data.getData();
//                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                edit_profile_image.setImageBitmap(selectedImage);
//                main_url_to_change_profile_image = imageUri;
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//            }
//
//
//        }
//    }

    Intent path;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            String path  = getRealPathFromURI_API19(getContext(), uri);
            path = data;
            try {
            final InputStream imageStream = getContentResolver().openInputStream(path.getData());
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            edit_profile_image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }
//        private int PICK_IMAGE_REQUEST = 1;
//      public  void imageupload(View v){
//          Log.d("dailoguestuck", "imageupload: 1");
//          if (Functions.checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
//              Log.d("dailoguestuck", "imageupload: 2");
//              Intent intent = new Intent();
//              intent.setType("image/*");
//              intent.setAction(Intent.ACTION_GET_CONTENT);
//              startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//          }else {
//              this.requestPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//              Log.d("dailoguestuck", "imageupload: 3");
//          }
//        }

    public void saveProfileImage()
    {
        if(path != null) {
            Log.d("pather", FileUtils.getPath(this.getApplicationContext(), path.getData()));
            File file = new File(FileUtils.getPath(getApplicationContext(), path.getData()));
            if (file.canRead()) {
                Log.d("file_exist", "asdasd");
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                final int user_id = s.getUserID();
                Log.d("updateImage", (String.valueOf(user_id)));
                if (user_id > 0) {
                    RetrofitClient.apiServices().postProfileImage(filePart, String.valueOf(s.getUserID())).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            getProfileImage(user_id);

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("ImagePosted", "responseFailed");

                        }
                    });
                } else {
                    Toast.makeText(this.getApplicationContext(), "User ID less than zero", Toast.LENGTH_LONG);
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Cannot Read File", Toast.LENGTH_LONG);
                Log.d("cannot_read_file", path.getData().toString());

            }
        }
    }


    public static String getRealPathFromURIGeneral(Uri uri){
        String path = uri.getPath(); // "/mnt/sdcard/FileName.mp3"
        File file = new File(path);
        return file.getPath();
    }



}


