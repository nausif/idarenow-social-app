package com.alpharelevant.idarenow;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.data.Adapters.ChallengeSummaryViewAdapter;
import com.alpharelevant.idarenow.data.Adapters.TanscationsDetailsViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.ChallengeSummary;
import com.alpharelevant.idarenow.data.models.CustomModel.TranscationsDetails;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.ChallengeConstants;
import com.alpharelevant.idarenow.data.utils.Session;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alpharelevant.idarenow.data.utils.Functions.session;

public class TranscationDetailsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Endless_Scroller scrollListener;
    RecyclerView rvItems;
    LinearLayoutManager linearLayoutManager;
    TanscationsDetailsViewAdapter challengesViewAdapter;
    List<TranscationsDetails> challengesList = new ArrayList<>();
    int user_id;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView txt_user_total_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transcation_details);

            ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranscationDetailsActivity.super.onBackPressed();
                }
            });

            session = new Session(getApplicationContext());
            user_id = session.getUserID();

            rvItems = findViewById(R.id.transcationDetailsRecyclerView);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvItems.setLayoutManager(linearLayoutManager);
            txt_user_total_balance = (TextView) findViewById(R.id.user_total_amount);
            RetrofitClient.apiServices().getUserTotalBalanceAmount(user_id).enqueue(new Callback<Double>() {
                @Override
                public void onResponse(Call<Double> call, Response<Double> response) {
                    if (response.isSuccessful()) {
                        Double user_total_balance = response.body().doubleValue();
                        txt_user_total_balance.setText("$" + user_total_balance.toString());
                    }
                }

                @Override
                public void onFailure(Call<Double> call, Throwable t) {

                }
            });

            RetrofitClient.apiServices().getTranscationsList(user_id, 0).enqueue(new Callback<List<TranscationsDetails>>() {
                @Override
                public void onResponse(Call<List<TranscationsDetails>> call, Response<List<TranscationsDetails>> response) {
                    if (response.isSuccessful()) {
                        challengesList = response.body();

                        if (challengesList.size() > 0) {
                            challengesViewAdapter = new TanscationsDetailsViewAdapter(getApplicationContext(), challengesList);
                            rvItems.setAdapter(challengesViewAdapter);
                            int curSize = challengesViewAdapter.getItemCount();
                            challengesViewAdapter.notifyItemRangeInserted(curSize, challengesList.size() - 1);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<TranscationsDetails>> call, Throwable t) {

                }
            });

            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new Endless_Scroller(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page * 10);
                }
            };
            rvItems.addOnScrollListener(scrollListener);
        }
        catch (Exception e)
        {

        }
    }

    public void loadNextDataFromApi(int offset) {

        RetrofitClient.apiServices().getTranscationsList(user_id,offset).enqueue(new Callback<List<TranscationsDetails>>() {
            @Override
            public void onResponse(Call<List<TranscationsDetails>> call, Response<List<TranscationsDetails>> response) {
                if(response.isSuccessful()){
                    List<TranscationsDetails> challengeslist = response.body();

                    if(challengesList.size() > 0){
                        int curSize = challengesViewAdapter.getItemCount();
                        challengesList.addAll(challengeslist);
                        challengesViewAdapter.notifyItemRangeInserted(curSize, challengesList.size() -1);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TranscationsDetails>> call, Throwable t) {

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
