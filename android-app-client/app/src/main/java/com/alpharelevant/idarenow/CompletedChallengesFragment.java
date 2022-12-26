package com.alpharelevant.idarenow;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.data.Adapters.ChallengeSummaryViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeSummary;
import com.alpharelevant.idarenow.data.models.CustomModel.notificationResponse;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alpharelevant.idarenow.data.utils.Functions.session;


public class CompletedChallengesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private Endless_Scroller scrollListener;
    RecyclerView rvItems;
    LinearLayoutManager linearLayoutManager;
    ChallengeSummaryViewAdapter challengesViewAdapter;
    List<ChallengeSummary> challengesList = new ArrayList<>();
    int user_id;
    SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_completed_challenges, container, false);
        session = new Session(getContext());
        user_id = session.getUserID();

        rvItems = v.findViewById(R.id.completedChallengesRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvItems.setLayoutManager(linearLayoutManager);
        RetrofitClient.apiServices().getChallengesList(user_id, ChallengeConstants.COMPLETED,0).enqueue(new Callback<List<ChallengeSummary>>() {
            @Override
            public void onResponse(Call<List<ChallengeSummary>> call, Response<List<ChallengeSummary>> response) {
                if(response.isSuccessful()){
                    challengesList = response.body();

                    if(challengesList.size() > 0){
                        challengesViewAdapter = new ChallengeSummaryViewAdapter(getContext(),challengesList);
                        rvItems.setAdapter(challengesViewAdapter);
                        int curSize = challengesViewAdapter.getItemCount();
                        challengesViewAdapter.notifyItemRangeInserted(curSize, challengesList.size() -1);
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
                loadNextDataFromApi(page*10);
            }
        };
        rvItems.addOnScrollListener(scrollListener);
        return v;

    }
    public void loadNextDataFromApi(int offset) {

        RetrofitClient.apiServices().getChallengesList(user_id, ChallengeConstants.COMPLETED,offset).enqueue(new Callback<List<ChallengeSummary>>() {
            @Override
            public void onResponse(Call<List<ChallengeSummary>> call, Response<List<ChallengeSummary>> response) {
                if(response.isSuccessful()){
                    List<ChallengeSummary> challengeslist = response.body();

                    if(challengesList.size() > 0){
                        int curSize = challengesViewAdapter.getItemCount();
                        challengesList.addAll(challengeslist);
                        challengesViewAdapter.notifyItemRangeInserted(curSize, challengesList.size() -1);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ChallengeSummary>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onRefresh() {
        challengesList.clear();
        scrollListener.resetState();
        challengesViewAdapter.notifyDataSetChanged();
//        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
