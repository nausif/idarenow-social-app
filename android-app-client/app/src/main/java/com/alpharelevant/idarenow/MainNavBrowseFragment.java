package com.alpharelevant.idarenow;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alpharelevant.idarenow.data.Adapters.ChallengeSummaryViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.background_services.NotificationService;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeSummary;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainNavBrowseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private Endless_Scroller scrollListener;
    RecyclerView rvItems;
    LinearLayoutManager linearLayoutManager;
    ChallengeSummaryViewAdapter challengesViewAdapter;
    List<ChallengeSummary> challengesList = new ArrayList<>();
    int user_id;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onRefresh() {

    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public MainNavBrowseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {


            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_main_nav_browse, container, false);
            FloatingActionButton fab = v.findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(getActivity(), AssignMarketerChallenge.class);
                    myIntent.putExtra("challenge_to_ID", 0);
                    startActivity(myIntent);
                }
            });

            rvItems = v.findViewById(R.id.browseChallengesRecyclerView);
            linearLayoutManager = new LinearLayoutManager(getContext());
            rvItems.setLayoutManager(linearLayoutManager);
            RetrofitClient.apiServices().getMarketerChallengesList(0).enqueue(new Callback<List<ChallengeSummary>>() {
                @Override
                public void onResponse(Call<List<ChallengeSummary>> call, Response<List<ChallengeSummary>> response) {
                    if (response.isSuccessful()) {
                        challengesList = response.body();

                        if (challengesList.size() > 0) {
                            challengesViewAdapter = new ChallengeSummaryViewAdapter(getContext(), challengesList);
                            rvItems.setAdapter(challengesViewAdapter);
                            int curSize = challengesViewAdapter.getItemCount();
                            challengesViewAdapter.notifyItemRangeInserted(curSize, challengesList.size());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ChallengeSummary>> call, Throwable t) {

                }
            });

            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new Endless_Scroller(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page + 10);
                }
            };
            rvItems.addOnScrollListener(scrollListener);
            return v;
        }
        catch (Exception e)
        {
            return inflater.inflate(R.layout.fragment_main_nav_browse, container, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("browseFrag", "onResume: "+challengesList.size());
    }

    public void loadNextDataFromApi(int offset) {

        RetrofitClient.apiServices().getMarketerChallengesList(offset).enqueue(new Callback<List<ChallengeSummary>>() {
            @Override
            public void onResponse(Call<List<ChallengeSummary>> call, Response<List<ChallengeSummary>> response) {
                if(response.isSuccessful()){
                    challengesList = response.body();

                    if(challengesList.size() > 0){
                        int curSize = challengesViewAdapter.getItemCount();
                        challengesViewAdapter.notifyItemRangeInserted(curSize, challengesList.size() );
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ChallengeSummary>> call, Throwable t) {

            }
        });
    }


}
