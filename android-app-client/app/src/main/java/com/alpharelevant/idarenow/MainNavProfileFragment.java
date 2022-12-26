package com.alpharelevant.idarenow;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.Adapters.NewsFeedViewAdapter;
import com.alpharelevant.idarenow.data.EndlessScroller.Endless_Scroller;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedModel;
import com.alpharelevant.idarenow.data.models.CustomModel.NewsfeedPost;
import com.alpharelevant.idarenow.data.models.User_Accounts;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.FileUtils;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.tcking.viewquery.ViewQuery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.alpharelevant.idarenow.data.utils.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainNavProfileFragment extends Fragment {

    public Integer user_id;
    User_Accounts user_accounts;

    TextView user_name_txt;
    TextView user_location_txt;
    RatingBar user_rating;
    DonutProgress d1;
    DonutProgress d2;
    DonutProgress d3;
    ImageView profileImage;
    private ProgressDialog progress = null;
    FloatingActionButton floatingActionButton;
    ScrollView sv;
    FloatingActionButton floatingActionButtonmsg;
    LinearLayoutManager linearLayoutManager;
    public MainNavProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {


//        Functions.progress(this.getContext()).show();
            showProgressBar();
            floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.floatingActionButton);
            floatingActionButtonmsg = (FloatingActionButton) getView().findViewById(R.id.floatingActionButtonmsg);
            if (s.getUserID() > 0 && s.getValuebyName("search_id") != String.valueOf(s.getUserID())) {
                floatingActionButton.setVisibility(View.INVISIBLE);
                floatingActionButtonmsg.setVisibility(View.INVISIBLE);
            }
            user_name_txt = (TextView) getView().findViewById(R.id.txtfullname);
            user_location_txt = (TextView) getView().findViewById(R.id.txtlocation);
            user_rating = (RatingBar) getView().findViewById(R.id.ratingbarUser);
            d1 = (DonutProgress) getView().findViewById(R.id.donut_progress_completed_challenge);
            d2 = (DonutProgress) getView().findViewById(R.id.donut_progress_Approval_rate);
            d3 = (DonutProgress) getView().findViewById(R.id.donut_progress_total_earning);
            profileImage = (ImageView) getView().findViewById(R.id.profile_image);
            Functions.getProfileImage(s.getUserID(), profileImage);
//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imageupload(view);
//            }
//        });
            getUserForProfile();
//        if(Functions.progress(getContext()).isShowing()){
//            Functions.progress(getContext()).dismiss();
//        }
            feedList.clear();
            rvItems = $.id(R.id.rvProfileChallengedVideos).view();
            linearLayoutManager = new LinearLayoutManager(getActivity());
            rvItems.setLayoutManager(linearLayoutManager);
            scrollListener = new Endless_Scroller(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    try {
                        if (feedList.size() > 0) {
                            loadNextDataFromApi(page * 3);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            RetrofitClient.apiServices().getProfileChallengedVideo(user_id, 0).enqueue(new Callback<NewsfeedPost[]>() {
                @Override
                public void onResponse(Call<NewsfeedPost[]> call, Response<NewsfeedPost[]> response) {
                    if (response.isSuccessful()) {
                        NewsfeedPost[] posts = response.body();
                        if (posts.length > 0) {
                            for (NewsfeedPost post : posts) {
                                feedList.add(new NewsfeedModel(post.challenge_id, post.challenge_to_img, post.challenge_To_Name, post.video, post.dateTime, post.video_id, post.challenge_From_Name, post.c_title, post.challenge_To_User_ID, post.challenge_from_id, post.post_approve_reject, post.challenge_Expiry_Date, post.winner_ID, post.approve_ratio, post.reject_ratio));
                            }
                            newsAdapter = new NewsFeedViewAdapter(getContext(), feedList);
                            rvItems.setAdapter(newsAdapter);
                            int curSize = newsAdapter.getItemCount();
                            newsAdapter.notifyItemRangeInserted(curSize, feedList.size() - 1);
//                        if(Functions.progress(getContext()).isShowing()){
//                            Functions.progress(getContext()).dismiss();
//                        }
                        }
                    }
                }

                @Override
                public void onFailure(Call<NewsfeedPost[]> call, Throwable t) {
                    try {
                        Functions.progress(getContext()).dismiss();
                        Toast.makeText(getContext(), "Failed to pull data", Toast.LENGTH_LONG);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            // Retain an instance so that you can call `resetState()` for fresh searches

            rvItems.addOnScrollListener(scrollListener);

        }
        catch (Exception e)
        {
            
        }

    }
    protected ViewQuery $;
    private RecyclerView mRecyclerView;
    Session  s;
    View v;
    LayoutInflater inflate;
    ViewGroup viewGroup;
    private Endless_Scroller scrollListener;
    List<NewsfeedModel> feedList;
    RecyclerView rvItems;
    NewsFeedViewAdapter newsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("profileTag", "onCreate: ");
        super.onCreate(savedInstanceState);
        feedList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());



    }

    public void showProgressBar()
    {
        progress = ProgressDialog.show(getActivity(), "",
                "Please Wait", true);
        progress.show();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("profileTag", "onCreateView: ");
//        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        v = inflater.inflate(R.layout.fragment_main_nav_profile, container, false);
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

        /** ------------------------------ Main NewsFeed Activity --------------------------------------  **/
        // Configure the RecyclerView





        // END


        return v;
    }


    public void loadNextDataFromApi(int offset) {
        RetrofitClient.apiServices().getProfileChallengedVideo(user_id,offset).enqueue(new Callback<NewsfeedPost[]>() {
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





    public void getUserForProfile() {
        RetrofitClient.apiServices().getUser_Accounts(user_id).enqueue(new Callback<User_Accounts>() {
            @Override
            public void onResponse(Call<User_Accounts> call, Response<User_Accounts> response) {

                user_name_txt.setText(response.body().getUserFullName());
                d1.setProgress(response.body().getUserChallengedCompleted());
                d2.setProgress(response.body().getUserApprovalRate());
                d3.setProgress(response.body().getUserTotalEarnings());
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
//        public class PagerAdapter extends FragmentStatePagerAdapter {
//            int mNumOfTabs;
//
//            public PagerAdapter(FragmentManager fm, int NumOfTabs) {
//                super(fm);
//                this.mNumOfTabs = NumOfTabs;
//            }
//
//            private String[] tabTitles = new String[]{"IN PROGRESS\nCHALLENGES", "COMPLETED\nCHALLENGE", "UNCOMPLETE\nCHALLENGE"};
//
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return tabTitles[position];
//            }
//
//            @Override
//            public Fragment getItem(int position) {
//
//                switch (position) {
//                    case 0:
//                        return new NavProfileTabsFragment();
//                    case 1:
//                        return new NavProfileTabsFragment();
//                    case 2:
//                        return new NavProfileTabsFragment();
//
//                    default:
//                        return null;
//                }
//            }
//
//            @Override
//            public int getCount() {
//                return mNumOfTabs;
//            }
//        }

        public interface OnFragmentInteractionListener {
            public void onFragmentInteraction(Uri uri);
        }
//        private int PICK_IMAGE_REQUEST = 1;
//      public  void imageupload(View v){
//          Log.d("dailoguestuck", "imageupload: 1");
//          if (Functions.checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
//              Log.d("dailoguestuck", "imageupload: 2");
//              Intent intent = new Intent();
//              intent.setType("image/*");
//              intent.setAction(Intent.ACTION_GET_CONTENT);
//              startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//          }else {
//              this.requestPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//              Log.d("dailoguestuck", "imageupload: 3");
//          }
//        }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("dailoguestuck", "onActivityResult: 1");
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
////            String path  = getRealPathFromURI_API19(getContext(), uri);
//            Log.d("dailoguestuck", "onActivityResult: 2");
//
//            Uri path = data.getData();
//            Log.d("pather", FileUtils.getPath(getContext(),data.getData()));
//                File file  = new File( FileUtils.getPath(getContext(),data.getData()));
//                    if (file.canRead()){
//                        Log.d("file_exist","asdasd");
//                       MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
//                       final int user_id =s.getUserID();
//                       Log.d("updateImage", (String.valueOf( user_id)));
//                        if(user_id>0) {
//                            RetrofitClient.apiServices().postProfileImage(filePart,  String.valueOf(s.getUserID())).enqueue(new Callback<ResponseBody>() {
//                                @Override
//                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                        getProfileImage(user_id);
//                                }
//                                @Override
//                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                    Log.d("ImagePosted","responseFailed"+call.toString());
//                                }
//                            });
//                        }else{
//                            Toast.makeText(getContext(),"User ID less than zero",Toast.LENGTH_LONG);
//                        }
//                    }
//                    else{
//                        Toast.makeText(getContext(),"Cannot Read File",Toast.LENGTH_LONG);
//                        Log.d("cannot_read_file", data.getData().toString());
//
//                    }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//
//    }

    public static String getRealPathFromURIGeneral(Uri uri){
        String path = uri.getPath(); // "/mnt/sdcard/FileName.mp3"
        File file = new File(path);
        return file.getPath();
    }
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
//        String filePath = "";
//        if (uri.getHost().contains("com.android.providers.media")) {
//            // Image pick from recent
//            String wholeID = DocumentsContract.getDocumentId(uri);
//
//            // Split at colon, use second item in the array
//            String id = wholeID.split(":")[1];
//
//            String[] column = {MediaStore.Images.Media.DATA};
//
//            // where id is equal to
//            String sel = MediaStore.Images.Media._ID + "=?";
//
//            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    column, sel, new String[]{id}, null);
//
//            int columnIndex = cursor.getColumnIndex(column[0]);
//
//            if (cursor.moveToFirst()) {
//                filePath = cursor.getString(columnIndex);
//            }
//            cursor.close();
//            return filePath;
//        } else {
//            // image pick from gallery
//            return  "";
//        }
        return  "";

    }
    public void getProfileImage(int id){
        RetrofitClient.apiServices().getProfileImage(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200 ) {
                    Bitmap bmp = null;
                    try {
                        byte[] asd = response.body().bytes();
                        bmp = BitmapFactory.decodeByteArray(asd, 0, asd.length);
                        Log.d("imageWidth",  String.valueOf(profileImage.getWidth()));
                        Log.d("imageHeight",  String.valueOf( profileImage.getHeight()));
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
}
