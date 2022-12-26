package com.alpharelevant.idarenow;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alpharelevant.idarenow.data.models.CustomModel.MarketerChallengeModel;
import com.alpharelevant.idarenow.data.utils.Functions;

import java.util.ArrayList;
import java.util.List;

public class MarketerChallengeActivity extends AppCompatActivity {


    public void showProgressBar()
    {
        progress = ProgressDialog.show(this, "",
                "Please Wait", true);
        progress.show();
    }

    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ProgressDialog progress;
    int challengeID;
    Bundle b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_marketer_challenge);
//        if(!Functions.progress(this).isShowing()){
//            Functions.progress(this).show();
//        }

            showProgressBar();
            challengeID = Integer.valueOf(getIntent().getExtras().get("challengeID").toString());
            if (savedInstanceState == null) {
                insertarTabs();
                viewPager = findViewById(R.id.marketerChallengePager);
                challengesHistoryViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);

                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#081f21"));
                tabLayout.setTabTextColors(Color.parseColor("#1c6e6e"), Color.parseColor("#081f21"));
            }
            ImageView backArrow = findViewById(R.id.backArrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

                    List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                    Log.d("tasklist1", taskList.get(0).topActivity.getClassName());
                    Log.d("tasklist2", this.getClass().getName());
                    if (taskList.get(0).numActivities == 1) {
                        Intent i = new Intent(getApplicationContext(), MainNavigationActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                        Log.i("if LAst", "This is last activity in the stack");
                    } else
                        MarketerChallengeActivity.super.onBackPressed();
                }
            });
            progress.dismiss();
        }
        catch (Exception e)
        {

        }
    }
    private void insertarTabs() {
        appBar =  findViewById(R.id.marketer_appbar);
        tabLayout = new TabLayout(this);
        appBar.addView(tabLayout);
    }

    private void challengesHistoryViewPager(ViewPager viewPager) {
        b = new Bundle();
        b.putInt("challengeID",challengeID);

        MarketerChallengeAdapter adapter = new MarketerChallengeAdapter(getSupportFragmentManager());
        MarketerChallengeBriefFragment mcf = new MarketerChallengeBriefFragment();
        MarketerChallengeVideosFragment mcv = new MarketerChallengeVideosFragment();
        mcf.setArguments(b);
        mcv.setArguments(b);
        adapter.addFragment(mcf, "BRIEF");
        adapter.addFragment(mcv, "VIDEOS");
        viewPager.setAdapter(adapter);

    }


    public class MarketerChallengeAdapter extends FragmentPagerAdapter {
        private final List<Fragment> frags = new ArrayList<>();
        private final List<String> tittlefrags = new ArrayList<>();

        public MarketerChallengeAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return frags.get(position);
        }

        @Override
        public int getCount() {
            return frags.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            frags.add(fragment);
            tittlefrags.add(title);
            notifyDataSetChanged();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tittlefrags.get(position);
        }
    }
}
