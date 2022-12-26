package com.alpharelevant.idarenow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.AddChallengeActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Adapters.NewsFeedViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedModel;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedPost;
import com.alpharelevant.idarenow.data.models.User_Accounts;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.tcking.viewquery.ViewQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchProfileFragment extends Fragment  {

    TextView user_name_txt;
    TextView user_location_txt;
    RatingBar user_rating;
    DonutProgress d1;
    DonutProgress d2;
    DonutProgress d3;
    ImageView profileImage;
    ScrollView sv;

    FloatingActionButton floatingActionButton;
    FloatingActionButton floatingActionButtonmsg;

    LinearLayoutManager linearLayoutManager;
    protected ViewQuery $;
    private RecyclerView mRecyclerView;
    Session s;
    View v;
    private ProgressDialog progress;
    LayoutInflater inflate;
    private Endless_Scroller scrollListener;
    List<NewsfeedModel> feedList;
    RecyclerView rvItems;
    NewsFeedViewAdapter newsAdapter;
    public Integer user_id;
    private OnFragmentInteractionListener mListener;

    public SearchProfileFragment() {
        // Required empty public constructor
    }


//    public static SearchProfileFragment newInstance(String param1, String param2) {
//        SearchProfileFragment fragment = new SearchProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        feedList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());

        scrollListener = new Endless_Scroller(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                try{
                    if(feedList.size()>0){
                        loadNextDataFromApi(page*3);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
    }

    public void showProgressBar()
    {
        progress = ProgressDialog.show(getActivity(), "",
                "Please Wait", true);
        progress.show();
    }


    public void loadNextDataFromApi(int offset) {
        RetrofitClient.apiServices().getProfileChallengedVideo(search_user_id,offset).enqueue(new Callback<NewsfeedPost[]>() {
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


    int search_user_id=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        search_user_id = getArguments().getInt(Constants.search_id);
        v = inflater.inflate(R.layout.fragment_search_profile, container, false);

        s = new Session(getContext());
        inflate = inflater;
        $ = new ViewQuery(getActivity());
        sv = (ScrollView) v.findViewById(R.id.scrollViewProfile);
        user_id = s.getUserID();
        if( user_id <=0){
            s.clearSessionslocal();
            startActivity(new Intent(getContext(),LandingActivity.class));
            floatingActionButton.setVisibility(View.INVISIBLE);
            System.exit(0);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Functions.hideKeyboardFrom(getContext(),getView());
        showProgressBar();
        feedList.clear();
        floatingActionButton = (FloatingActionButton)getView().findViewById(R.id.floatingActionButton);
        floatingActionButtonmsg = (FloatingActionButton)getView().findViewById(R.id.floatingActionButtonmsg);
        user_name_txt = (TextView) getView().findViewById(R.id.txtfullname);
        user_location_txt = (TextView) getView().findViewById(R.id.txtlocation);
        user_rating = (RatingBar) getView().findViewById(R.id.ratingbarUser);
        d1 = (DonutProgress)  getView().findViewById(R.id.donut_progress_completed_challenge);
        d2 = (DonutProgress)  getView().findViewById(R.id.donut_progress_Approval_rate);
        d3 = (DonutProgress)  getView().findViewById(R.id.donut_progress_total_earning);
        profileImage = (ImageView)getView().findViewById(R.id.profile_image);
        getuserDetails(search_user_id);
        getProfileImage(search_user_id);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), AddChallengeActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.search_id, String.valueOf(search_user_id));
//                intent.putExtras(bundle);
//                startActivity(intent);
                Intent myIntent = new Intent(getActivity(), AssignMarketerChallenge.class);
                myIntent.putExtra("challenge_to_ID", search_user_id);
                startActivity(myIntent);
            }
        });

        floatingActionButtonmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("buttonmsg", "onClick: ");
                Intent myIntent = new Intent(getActivity(), MessageActivity.class);
                myIntent.putExtra("otherId", search_user_id);
                startActivity(myIntent);
            }
        });


        rvItems = $.id(R.id.rvSearchProfileChallengedVideos).view();

        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        RetrofitClient.apiServices().getProfileChallengedVideo(search_user_id,0).enqueue(new Callback<NewsfeedPost[]>() {
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
//                        if(Functions.progress(getContext()).isShowing()){
//                            Functions.progress(getContext()).dismiss();
//                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsfeedPost[]> call, Throwable t) {
                try{
                    progress.dismiss();
                    Toast.makeText(getContext(),"Failed to pull data",Toast.LENGTH_LONG);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        rvItems.addOnScrollListener(scrollListener);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("SearchProfileFragment", "keyCode: " + keyCode);
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i("KEYCODE_BACK", "onKey Back listener is working!!!");
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
    }
    public void getuserDetails(int search_user_id) {


        RetrofitClient.apiServices().getUser_Accounts(search_user_id).enqueue(new Callback<User_Accounts>() {
            @Override
            public void onResponse(Call<User_Accounts> call, Response<User_Accounts> response) {
                user_name_txt.setText(response.body().getUserFullName());
                d1.setProgress(response.body().getUserChallengedCompleted());
                d2.setProgress(response.body().getUserApprovalRate());
                d3.setProgress(response.body().getUserTotalEarnings());
                user_rating.setRating(Float.valueOf(response.body().getUserRatings().toString()));
                d3.setText(response.body().getUserTotalEarnings().toString() + "%");
                d1.setText(response.body().getUserChallengedCompleted().toString()+ "%");
                d2.setText(response.body().getUserApprovalRate().toString()+ "%");
                user_rating.setRating(Float.valueOf(response.body().getUserRatings().toString()));
                progress.dismiss();
            }
            @Override
            public void onFailure(Call<User_Accounts> call, Throwable t) {
                Log.d("getusererror2", t.toString());
                progress.dismiss();
            }
        });
    }
    public void getProfileImage(int id){
        RetrofitClient.apiServices().getProfileImage(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    Bitmap bmp = null;
                    try {
                        byte[] asd = response.body().bytes();
                        bmp = BitmapFactory.decodeByteArray(asd, 0, asd.length);
                        profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, profileImage.getWidth(), profileImage.getHeight(), false));
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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
