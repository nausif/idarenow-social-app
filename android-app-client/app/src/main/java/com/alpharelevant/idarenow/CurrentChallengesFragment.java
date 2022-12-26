package com.alpharelevant.idarenow;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class CurrentChallengesFragment extends Fragment {


    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_current_challenges, container, false);
        if (savedInstanceState == null) {
            insertarTabs(container);

            viewPager = (ViewPager) view.findViewById(R.id.currentChallengePager);
            challengesHistoryViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#081f21"));
            tabLayout.setTabTextColors(Color.parseColor("#1c6e6e"), Color.parseColor("#081f21"));
        }
        return view;
    }

    private void insertarTabs(ViewGroup container) {
        View padre = (View) container.getParent();
        appBar = (AppBarLayout) padre.findViewById(R.id.appbar);
        tabLayout = new TabLayout(getActivity());
        appBar.addView(tabLayout);
    }

    private void challengesHistoryViewPager(ViewPager viewPager) {
        CurrentChallengesAdapter adapter = new CurrentChallengesAdapter(getFragmentManager());
        adapter.addFragment(new RespondChallengeFragment(), "Respond\nChallenges");
        adapter.addFragment(new InProgressChallengesFragment(), "In Progress\nChallenges");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBar.removeView(tabLayout);
    }

    public void onButtonPressed(Uri uri) {
    }



    public class CurrentChallengesAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> frags = new ArrayList<>();
        private final List<String> tittlefrags = new ArrayList<>();

        public CurrentChallengesAdapter(FragmentManager fm) {
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
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tittlefrags.get(position);
        }
    }

}
