package com.alpharelevant.idarenow;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.data.Adapters.MarketerVideoViewAdapter;
import com.alpharelevant.idarenow.data.Adapters.NewsFeedViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedModel;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedPost;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.github.tcking.viewquery.ViewQuery;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tcking.github.com.giraffeplayer2.VideoView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class MarketerChallengeVideosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    protected ViewQuery $;
    int playPosition;
    @Override
    public void onRefresh() {
        feedList.clear();
        scrollListener.resetState();
        newsAdapter.notifyDataSetChanged();
//        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    private Endless_Scroller scrollListener;
    List<NewsfeedModel> feedList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView rvItems;
    MarketerVideoViewAdapter newsAdapter;
    Session session;
    int user_id;
    int challengeID;

    boolean readytoStop = false;

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStartnews", "Start: ");
    }


    @Override
    public void onStop() {
        super.onStop();
        if(newsAdapter != null) {
            Log.d("onStopnews", "onStop: ");
            newsAdapter.feedList.clear();
            newsAdapter.notifyDataSetChanged();
//        mSwipeRefreshLayout.setRefreshing(true);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {


            readytoStop = false;
            /** ------------------------------ Main NewsFeed Activity --------------------------------------  **/
            // Configure the RecyclerView
//        rvItems = (RecyclerView) view.findViewById(R.id.rvNewsFeed);
            rvItems = $.id(R.id.marketerChallengeVideoRecyclerView).view();
            feedList = new ArrayList<>();
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rvItems.setLayoutManager(layoutManager);


            rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == SCROLL_STATE_IDLE) {
                        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                        playPosition = lm.findFirstVisibleItemPosition();
                        Log.d("initial", "onScrollStateChanged: " + playPosition);
                        if (playPosition == RecyclerView.NO_POSITION) {//no visible item
                            return;
                        }

                        int firstCompletelyVisibleItemPosition = lm.findFirstCompletelyVisibleItemPosition();
                        Log.d("first", "onScrollStateChanged: " + firstCompletelyVisibleItemPosition);

                        int lastCompletelyVisibleItemPosition = lm.findLastCompletelyVisibleItemPosition();
                        Log.d("last", "onScrollStateChanged: " + lastCompletelyVisibleItemPosition);

                        for (int i = firstCompletelyVisibleItemPosition; i <= lastCompletelyVisibleItemPosition; i++) {
                            View viewByPosition = lm.findViewByPosition(i);
                            if (viewByPosition != null) {
                                VideoView videoView = viewByPosition.findViewById(R.id.marketervideo_cardView_video);
                                if (videoView != null && videoView.isCurrentActivePlayer()) {
                                    return;//current active player is visible,do nothing
                                }
                            }
                        }


                        //try find first visible item (visible part > 50%)
                        if (firstCompletelyVisibleItemPosition >= 0 && playPosition != firstCompletelyVisibleItemPosition) {
                            int[] recyclerView_xy = new int[2];
                            int[] f_xy = new int[2];

                            VideoView videoView = (VideoView) lm.findViewByPosition(playPosition).findViewById(R.id.marketervideo_cardView_video);
                            videoView.getLocationInWindow(f_xy);
                            recyclerView.getLocationInWindow(recyclerView_xy);
                            int unVisibleY = f_xy[1] - recyclerView_xy[1];

                            if (unVisibleY < 0 && Math.abs(unVisibleY) * 1.0 / videoView.getHeight() > 0.5) {//No visible part > 50%,play next
                                playPosition = firstCompletelyVisibleItemPosition;
                            }
                        }
                        VideoView videoView = lm.findViewByPosition(playPosition).findViewById(R.id.marketervideo_cardView_video);
                        if (videoView != null) {
                            videoView.getPlayer().start();
                            readytoStop = true;
                        }

                    }
                }

            });

            RetrofitClient.apiServices().getChallengeAssociatedVideos(user_id, challengeID, 0).enqueue(new Callback<NewsfeedPost[]>() {
                @Override
                public void onResponse(Call<NewsfeedPost[]> call, Response<NewsfeedPost[]> response) {
                    if (response.isSuccessful()) {
                        NewsfeedPost[] posts = response.body();
                        if (posts.length > 0) {

//                            feedList.add(new NewsfeedModel(posts[6].challenge_to_img,posts[6].challenge_To_Name,posts[6].video,"",0,posts[6].challenge_From_Name,posts[6].c_title));
//                            feedList.add(new NewsfeedModel(posts[0].challenge_to_img,posts[0].challenge_To_Name,posts[0].video,"",0,posts[0].challenge_From_Name,posts[0].c_title,posts[0].challenge_To_User_ID,posts[0].challenge_from_id));
                            for (NewsfeedPost post : posts) {
                                Log.d("item_post", "onResponse: " + post.c_title);
                                feedList.add(new NewsfeedModel(post.challenge_id, post.challenge_to_img, post.challenge_To_Name, post.video, post.dateTime, post.video_id, post.challenge_From_Name, post.c_title, post.challenge_To_User_ID, post.challenge_from_id, post.post_approve_reject, post.challenge_Expiry_Date, post.winner_ID, post.approve_ratio, post.reject_ratio));
                            }
                            newsAdapter = new MarketerVideoViewAdapter(getContext(), feedList);
                            rvItems.setAdapter(newsAdapter);
                            int curSize = newsAdapter.getItemCount();
                            newsAdapter.notifyItemRangeInserted(curSize, feedList.size() - 1);
                            Functions.progress(getContext()).hide();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NewsfeedPost[]> call, Throwable t) {
                    Functions.progress(getContext()).hide();
                }
            });


            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new Endless_Scroller(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page * 3);
                }
            };
            rvItems.addOnScrollListener(scrollListener);
        }
        catch (Exception e)
        {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {


            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_marketer_challenge_videos, container, false);
            $ = new ViewQuery(getActivity());
            readytoStop = false;
            session = new Session(getContext());
            user_id = session.getUserID();
            Bundle bundle = this.getArguments();
            challengeID = bundle.getInt("challengeID");
            return v;
        }
        catch (Exception e)
        {
           return inflater.inflate(R.layout.fragment_marketer_challenge_brief, container, false);
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
            newsAdapter.feedList.clear();
            newsAdapter.notifyDataSetChanged();
            newsAdapter.notifyItemRangeRemoved(0, newsAdapter.feedList.size());
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
        RetrofitClient.apiServices().getChallengeAssociatedVideos(user_id,challengeID,offset).enqueue(new Callback<NewsfeedPost[]>() {
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

}
