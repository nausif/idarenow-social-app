package com.alpharelevant.idarenow;


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

import com.alpharelevant.idarenow.data.Adapters.NotificationViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.notificationResponse;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alpharelevant.idarenow.data.utils.Functions.session;

public class MainNavNotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    public MainNavNotificationFragment() {
        // Required empty public constructor
    }
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
    private Endless_Scroller scrollListener;
    RecyclerView rvItems;
    LinearLayoutManager linearLayoutManager;
    NotificationViewAdapter notificationViewAdapter;
    List<notificationResponse> notificationList;
    int user_id;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int curSize;

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
            View v = inflater.inflate(R.layout.fragment_main_nav_notification, container, false);
            session = new Session(getContext());
            user_id = session.getUserID();

            rvItems = v.findViewById(R.id.rvNotification);
            linearLayoutManager = new LinearLayoutManager(getContext());
            rvItems.setLayoutManager(linearLayoutManager);
            notificationList = new ArrayList<>();
            RetrofitClient.apiServices().getNotificationList(user_id, 0).enqueue(new Callback<notificationResponse[]>() {
                notificationResponse[] posts;

                @Override
                public void onResponse(Call<notificationResponse[]> call, Response<notificationResponse[]> response) {
                    if (response.isSuccessful()) {
                        this.posts = response.body();

                        if (posts.length > 0) {
                            int curSize = notificationViewAdapter.getItemCount();
                            Log.d("MainNavNotification", String.valueOf(posts.length));
//                            feedList.add(new NewsfeedModel(posts[0].challenge_to_img,posts[0].challenge_To_Name,posts[0].video,"",0,posts[0].challenge_From_Name,posts[0].c_title,posts[0].challenge_To_User_ID,posts[0].challenge_from_id));
                            for (notificationResponse item : posts) {
                                notificationResponse post = item;
                                notificationList.add(new notificationResponse(post.notification_id, post.from_id, post.to_id, post.title, post.description, post.profile_image, post.challege_id, "assigned challenge", post.from_name, post.approval_status));
                            }
                            notificationViewAdapter.notifyItemRangeInserted(curSize, notificationList.size());
                        }
                    }

                }

                @Override
                public void onFailure(Call<notificationResponse[]> call, Throwable t) {

                }
            });
//        notificationList.add(new notificationResponse(1,2,2,"asdasd","asdasd","asd",2,"asd"));
            notificationViewAdapter = new NotificationViewAdapter(getContext(), notificationList);
            rvItems.setAdapter(notificationViewAdapter);
            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new Endless_Scroller(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page * 3);
                }
            };
            rvItems.addOnScrollListener(scrollListener);
            return v;
        }
        catch (Exception e)
        {
           return inflater.inflate(R.layout.fragment_main_nav_notification, container, false);
        }
    }

    public void loadNextDataFromApi(int offset) {

        Log.d("onloadmore", "show me");
        RetrofitClient.apiServices().getNotificationList(user_id,offset).enqueue(new Callback<notificationResponse[]>() {
            notificationResponse[] posts;
            @Override
            public void onResponse(Call<notificationResponse[]> call, Response<notificationResponse[]> response) {
                if(response.isSuccessful()){
                    this.posts = response.body();
                    if(posts.length>0){
                        int curSize = notificationViewAdapter.getItemCount();
                        for(notificationResponse item : posts){
                            notificationResponse post = item;
                            notificationList.add(new notificationResponse(post.notification_id,post.from_id,post.to_id,post.title,post.description,post.profile_image,post.challege_id,"6hr",post.from_name,post.approval_status));
                        }
                        notificationViewAdapter.notifyItemRangeInserted(curSize, notificationList.size() - 1);
                    }
                }

            }

            @Override
            public void onFailure(Call<notificationResponse[]> call, Throwable t) {

            }
        });
    }
    @Override
    public void onRefresh() {
        notificationList.clear();
        scrollListener.resetState();
        notificationViewAdapter.notifyDataSetChanged();
//        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
