package com.alpharelevant.idarenow;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.data.Adapters.NewsFeedViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedModel;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedPost;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Session;
import com.github.tcking.viewquery.ViewQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tcking.github.com.giraffeplayer2.VideoView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class MainNavHomeFragment extends Fragment {

    protected ViewQuery $;
    int playPosition;
    private SwipeRefreshLayout mSwipeRefreshLayout;
//    @Override
//    public void onRefresh() {
//        feedList.clear();
//        loadNextDataFromApi(0);
//        newsAdapter = new NewsFeedViewAdapter(getContext(),feedList);
//        rvItems.setAdapter(newsAdapter);
//
////        mSwipeRefreshLayout.setRefreshing(true);
//    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }



    // Store a member variable for the listener
    private Endless_Scroller scrollListener;
    List<NewsfeedModel> feedList;
    RecyclerView rvItems;
    NewsFeedViewAdapter newsAdapter;
    Session session;
    int user_id;
    private ProgressDialog progress = null;
    boolean readytoStop = false;
    LinearLayoutManager layoutManager;

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStartnews", "Start: ");
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            Log.d("onStopnews", "onStop: ");
//            newsAdapter.feedList.clear();
//            newsAdapter.notifyDataSetChanged();
//        mSwipeRefreshLayout.setRefreshing(true);
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeFragment321", "onCreate: ");
        feedList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity());



    }

    public void onScrollingVideo(RecyclerView recyclerView, int newState)
    {
        if (newState == SCROLL_STATE_IDLE) {
            LinearLayoutManager lm = (LinearLayoutManager)recyclerView.getLayoutManager();
            playPosition = lm.findFirstVisibleItemPosition();

            Log.d("initial", "onScrollStateChanged: "+playPosition);
            if (playPosition == RecyclerView.NO_POSITION) {//no visible item
                return;
            }

            int firstCompletelyVisibleItemPosition = lm.findFirstCompletelyVisibleItemPosition();
            Log.d("first", "onScrollStateChanged: "+firstCompletelyVisibleItemPosition);

            int lastCompletelyVisibleItemPosition = lm.findLastCompletelyVisibleItemPosition();
            Log.d("last", "onScrollStateChanged: "+lastCompletelyVisibleItemPosition);

            for (int i = firstCompletelyVisibleItemPosition; i <=lastCompletelyVisibleItemPosition; i++) {
                View viewByPosition = lm.findViewByPosition(i);
                if (viewByPosition != null) {
                    VideoView videoView =  viewByPosition.findViewById(R.id.newsFeed_cardView_video);
                    if (videoView!=null && videoView.isCurrentActivePlayer()) {
                        return;//current active player is visible,do nothing
                    }
                }
            }


            //try find first visible item (visible part > 50%)
            if (firstCompletelyVisibleItemPosition >= 0 && playPosition != firstCompletelyVisibleItemPosition) {
                int[] recyclerView_xy = new int[2];
                int[] f_xy = new int[2];

                VideoView videoView = (VideoView) lm.findViewByPosition(playPosition).findViewById(R.id.newsFeed_cardView_video);
                videoView.getLocationInWindow(f_xy);
                recyclerView.getLocationInWindow(recyclerView_xy);
                int unVisibleY = f_xy[1] - recyclerView_xy[1];

                if (unVisibleY < 0 && Math.abs(unVisibleY) * 1.0 / videoView.getHeight() > 0.5) {//No visible part > 50%,play next
                    playPosition = firstCompletelyVisibleItemPosition;
                }
            }
            VideoView videoView =  lm.findViewByPosition(playPosition).findViewById(R.id.newsFeed_cardView_video);
            if (videoView != null) {
                videoView.getPlayer().start();
                readytoStop = true;
            }

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgressBar();
        readytoStop = false;
        /** ------------------------------ Main NewsFeed Activity --------------------------------------  **/
        // Configure the RecyclerView
//        rvItems = (RecyclerView) view.findViewById(R.id.rvNewsFeed);
        rvItems = $.id(R.id.rvNewsFeed).view();
        layoutManager = new LinearLayoutManager(getActivity());
        rvItems.setLayoutManager(layoutManager);
        scrollListener = new Endless_Scroller(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page*3);
            }
        };
        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                onScrollingVideo(recyclerView,newState);
            }

        });
        RetrofitClient.apiServices().getNewsFeedList(user_id,0).enqueue(new Callback<NewsfeedPost[]>() {
            @Override
            public void onResponse(Call<NewsfeedPost[]> call, Response<NewsfeedPost[]> response) {
                if(response.isSuccessful()){
                    NewsfeedPost[] posts = response.body();
                    if(posts.length>0){
                        for(NewsfeedPost post : posts){
                            feedList.add(new NewsfeedModel(post.challenge_id,post.challenge_to_img,post.challenge_To_Name,post.video,post.dateTime,post.video_id,post.challenge_From_Name,post.c_title,post.challenge_To_User_ID,post.challenge_from_id,post.post_approve_reject,post.challenge_Expiry_Date,post.winner_ID,post.approve_ratio,post.reject_ratio));
                        }
                        newsAdapter = new NewsFeedViewAdapter(getContext(),feedList);
                        rvItems.setAdapter(newsAdapter);
                        int curSize = newsAdapter.getItemCount();
                        newsAdapter.notifyItemRangeInserted(curSize, feedList.size() -1);
                        progress.dismiss();
                        onScrollingVideo(rvItems,SCROLL_STATE_IDLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsfeedPost[]> call, Throwable t) {
                progress.dismiss();
            }
        });
        // Retain an instance so that you can call `resetState()` for fresh searches

        rvItems.addOnScrollListener(scrollListener);

        // Adds the scroll listener to RecyclerView
        /** ------------------------------ Main NewsFeed Activity --------------------------------------  **/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ViewCompat.setNestedScrollingEnabled(rvItems, false);
        // Inflate the layout for this fragment
        try {


            Log.d("HomeFragment66", "onCreateView: ");
            View v = inflater.inflate(R.layout.fragment_main_nav_home, container, false);
            session = new Session(getContext());
            user_id = session.getUserID();
            $ = new ViewQuery(getActivity());

            // Pull to Refresh...

            // SwipeRefreshLayout
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code here
                    feedList.clear();
                    loadNextDataFromApi(0);
                    newsAdapter = new NewsFeedViewAdapter(getContext(), feedList);
                    rvItems.setAdapter(newsAdapter);
                    int curSize = newsAdapter.getItemCount();
                    newsAdapter.notifyItemRangeInserted(curSize, feedList.size() - 1);
                    layoutManager = new LinearLayoutManager(getActivity());
                    rvItems.setLayoutManager(layoutManager);
                    scrollListener = new Endless_Scroller(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            Log.d("HomeFr321", "onLoadMore: " + page);
                            loadNextDataFromApi(page * 3);
                        }
                    };
                    rvItems.addOnScrollListener(scrollListener);

                    mSwipeRefreshLayout.setRefreshing(false);
                    onScrollingVideo(rvItems, SCROLL_STATE_IDLE);

                }
            });
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);

            /**
             * Showing Swipe Refresh animation on activity create
             * As animation won't start on onCreate, post runnable is used
             */
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {

                }
            });

            return v;
        }
        catch (Exception e)
        {
            return  inflater.inflate(R.layout.fragment_main_nav_home, container, false);
        }

    }


    @Override
    public void onPause() {
//        newsAdapter.feedList.size()
        super.onPause();
         try {
             if (readytoStop) {
                 VideoView videoView = rvItems.getLayoutManager().findViewByPosition(playPosition).findViewById(R.id.newsFeed_cardView_video);
                 if (videoView != null) {
                     videoView.getPlayer().stop();
                 }
             }
//             newsAdapter.feedList.clear();
//             newsAdapter.notifyDataSetChanged();
//             newsAdapter.notifyItemRangeRemoved(0, newsAdapter.feedList.size());
         }
         catch (Exception e)
         {}
        Log.d("homeFrag", "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("homeFrag", "onresume: ");

    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        RetrofitClient.apiServices().getNewsFeedList(user_id,offset).enqueue(new Callback<NewsfeedPost[]>() {
            NewsfeedPost[] posts;
            @Override
            public void onResponse(Call<NewsfeedPost[]> call, Response<NewsfeedPost[]> response) {

                if(response.isSuccessful()){
                    this.posts = response.body();
                    if(posts.length>0){
                        int curSize = newsAdapter.getItemCount();
//                            feedList.add(new NewsfeedModel(posts[6].challenge_to_img,posts[6].challenge_To_Name,posts[6].video,"",0,posts[6].challenge_From_Name,posts[6].c_title));
//                            feedList.add(new NewsfeedModel(posts[0].challenge_to_img,posts[0].challenge_To_Name,posts[0].video,"",0,posts[0].challenge_From_Name,posts[0].c_title,posts[0].challenge_To_User_ID,posts[0].challenge_from_id));
                        for(NewsfeedPost post : posts){
                            feedList.add(new NewsfeedModel(post.challenge_id,post.challenge_to_img,post.challenge_To_Name,post.video,post.dateTime,post.video_id,post.challenge_From_Name,post.c_title,post.challenge_To_User_ID,post.challenge_from_id,post.post_approve_reject,post.challenge_Expiry_Date,post.winner_ID,post.approve_ratio,post.reject_ratio));
                        }
                        newsAdapter.notifyItemRangeInserted(curSize, feedList.size() - 1);
                    }
                }

            }

            @Override
            public void onFailure(Call<NewsfeedPost[]> call, Throwable t) {

            }
        });

    }

        public void showProgressBar()
        {
            progress = ProgressDialog.show(getActivity(), "",
                    "Please Wait", true);
            progress.show();
        }




}
