package com.alpharelevant.idarenow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.alpharelevant.idarenow.data.background_services.NotificationService;
import com.alpharelevant.idarenow.data.models.CustomModel.UserDetails;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.alpharelevant.idarenow.data.utils.StaticObjects;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainNavigationActivity extends AppCompatActivity implements
        MainNavHomeFragment.OnFragmentInteractionListener
        , MainNavBrowseFragment.OnFragmentInteractionListener
        , MainNavSearchFragment.OnFragmentInteractionListener
        , MainNavProfileFragment.OnFragmentInteractionListener
        , MainNavNotificationFragment.OnFragmentInteractionListener
        , NavigationView.OnNavigationItemSelectedListener{

   public Fragment frag;
   TextView txt_nav_header_fullname;
   CircularImageView txt_nav_header_profile_pic;
    String fragName = "";
    Session s;
    BottomNavigationView bottom_navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            StaticObjects.fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = StaticObjects.fragmentManager.beginTransaction();
            ft.setReorderingAllowed(true);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragments(0);
                    return true;
                case R.id.navigation_search:
                    loadFragments(1);
                    return true;
                case R.id.navigation_profile:
                    loadFragments(2);
                    return true;
                case R.id.navigation_notifications:
                    loadFragments(3);
                    return true;
                case R.id.navigation_browse:
                    loadFragments(4);
                    return true;

            }
            return false;
        }
    };

    private DrawerLayout mDrawerLayout;
    ArrayList<Fragment> fragments;
    int index;
    void loadFragments(int index){
        this.index = index;
        getSupportFragmentManager().beginTransaction().replace(R.id.main_nav_frame_layout,fragments.get(index)).commit();
    }
    private NotificationService notification_Service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_navigation);
            s = new Session(this.getApplicationContext());
            startService(new Intent(this, NotificationService.class));
            fragments = new ArrayList<Fragment>();

            NavigationView side_drawer = (NavigationView) findViewById(R.id.side_drawer);
            View headerView = side_drawer.getHeaderView(0);
            txt_nav_header_fullname = headerView.findViewById(R.id.nav_header_profile_name);
            txt_nav_header_profile_pic = headerView.findViewById(R.id.nav_header_profile_image);

            if (s.getUserID() <= 0) {
                s.clearSessionslocal();
                startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                return;
            }
            GiraffePlayer.debug = true;//show java logs
            GiraffePlayer.nativeDebug = false;//not show native logs

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionbar.setDisplayShowTitleEnabled(true);
            actionbar.setTitle("iDareNow");


//        if( s.getUserID()<=0){
//            s.clearSessionslocal();
//            startActivity(new Intent(getApplicationContext(),LandingActivity.class));
//            return;
//        }
//        startService(new Intent(this, NotificationService.class));


            // Drawer
            mDrawerLayout = findViewById(R.id.drawer_container);

            RetrofitClient.apiServices().getUserDetails(s.getUserID()).enqueue(new Callback<UserDetails>() {
                @Override
                public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {

                    if (response.isSuccessful()) {
                        UserDetails ud = response.body();

                        if (ud != null) {
                            if (txt_nav_header_fullname != null) {
                                if (ud.user_name != null)
                                    txt_nav_header_fullname.setText(ud.user_name);
                            }

                            if (ud.profile_pic != null) {
                                Log.d("drawer_image", "onResponse: " + String.valueOf(ud.profile_pic));
                                txt_nav_header_profile_pic.setImageBitmap(Functions.getBitmapFromURL("http://" + ud.profile_pic));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDetails> call, Throwable t) {

                }
            });

            mDrawerLayout.addDrawerListener(
                    new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            // Respond when the drawer's position changes
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {
                            // Respond when the drawer is opened
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            // Respond when the drawer is closed
                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            // Respond when the drawer motion state changes
                        }
                    }
            );
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            side_drawer.setNavigationItemSelectedListener(this);

            bottom_navigation = findViewById(R.id.bottom_navigation);
            fragments.addAll(Arrays.asList(new MainNavHomeFragment(), new MainNavSearchFragment(), new MainNavProfileFragment(), new MainNavNotificationFragment(), new MainNavBrowseFragment()));
            disableShiftMode(bottom_navigation);

            this.loadFragments(0);
            bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        frag = new MainNavHomeFragment();
//            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.replace(R.id.main_nav_frame_layout,frag);
//            ft.commit();
            createNotificationChannel();
        }
        catch (Exception e)
        {

        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123123123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);

                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            //Timber.e(e, "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            //Timber.e(e, "Unable to change value of shift mode");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                Toast.makeText(this, "please grant read permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PlayerManager.getInstance().onConfigurationChanged(newConfig);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && getSupportFragmentManager().getFragments().size()>0){
            getSupportFragmentManager().popBackStack();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }

//            FragmentManager fragmentManager = getSupportFragmentManager();
            this.doubleBackToExitPressedOnce = true;
//            int clickedFragment = fragmentManager.getBackStackEntryCount();
//            Log.d("clickedFragment", "onBackPressed: "+clickedFragment);
//            if(clickedFragment>0)
//            {
//                fragmentManager.beginTransaction();
//                fragmentManager.popBackStackImmediate("com.alpharelevant.idarenow.MainNavHomeFragment",0);

//                .commit();
                //fragment title highlight
            if(index!=0){
                bottom_navigation.setSelectedItemId(bottom_navigation.getMenu().findItem(R.id.navigation_home).getItemId());
                this.doubleBackToExitPressedOnce = false;
            }else {
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 1200);
            }

        }
    }
    boolean doubleBackToExitPressedOnce = false;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.messege_send:
            {
                Intent myIntent = new Intent(getApplicationContext(), BaseMessageActivity.class);
                startActivity(myIntent);
                return true;
            }

        }


       return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        if (id == R.id.nav_current_challenges) {
            ft.replace(R.id.main_nav_frame_layout, new CurrentChallengesFragment())
                    .commit();
        } else if (id == R.id.nav_transcation_Details) {
            Intent myIntent = new Intent(getApplicationContext(), TranscationDetailsActivity.class);
            startActivity(myIntent);
        }
        else if (id == R.id.nav_challenges_by_you) {

            ft.replace(R.id.main_nav_frame_layout, new ChallengeByYouFragment())
                    .commit();
        }
        else if (id == R.id.nav_challenge_history) {
                    ft.replace(R.id.main_nav_frame_layout, new ChallengesHistoryFragment())
                    .commit();
        }
        else if (id == R.id.nav_ChangePassword) {
            Intent myIntent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(myIntent);
        }
        else if (id == R.id.nav_Edit_profile) {
            Intent myIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
            startActivity(myIntent);
        }
        else if (id == R.id.nav_logout) {
            Intent myIntent = new Intent(getApplicationContext(), LandingActivity.class);
            if(AccessToken.isCurrentAccessTokenActive()){
                LoginManager.getInstance().logOut();
            }
            startActivity(myIntent);
            s.clearallSessions();
            s.clearSessionslocal();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_container);
        item.setChecked(true);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


}
