package com.alpharelevant.idarenow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alpharelevant.idarenow.data.Adapters.CommentsViewAdapter;
import com.alpharelevant.idarenow.data.Adapters.SearchViewAdapter;
import com.alpharelevant.idarenow.data.models.CustomModel.Comments;
import com.alpharelevant.idarenow.data.models.CustomModel.UserDetails;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Functions;
import com.alpharelevant.idarenow.data.utils.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCommentActivity extends AppCompatActivity {


    ListView lv;
    ProgressDialog pd;
    List<Comments> listofcomments = new ArrayList<>();
    CommentsViewAdapter cva;
    EditText comment_txt;
    ImageView img_checkmark;
    Session s;
    int post_id;
    int user_id;
    Comments c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post_comment);
            s = new Session(getApplicationContext());
            lv = (ListView) findViewById(R.id.comment_listView);
            comment_txt = (EditText) findViewById(R.id.comment_add_new);
            img_checkmark = (ImageView) findViewById(R.id.comment_PostComment);
            lv.setDivider(null);

            user_id = s.getUserID();
            Bundle b = getIntent().getExtras();
            post_id = -1; // or other values
            if (b != null)
                post_id = b.getInt("post_id");


            RetrofitClient.apiServices().getAllComments(post_id).enqueue(new Callback<List<Comments>>() {
                @Override
                public void onResponse(Call<List<Comments>> call, Response<List<Comments>> response) {
                    Log.i("onresponse", "hit");
                    if (response.isSuccessful()) {
                        listofcomments = response.body();
                        cva = new CommentsViewAdapter(PostCommentActivity.this, listofcomments);
                        lv.setAdapter(cva);
                    }
                }

                @Override
                public void onFailure(Call<List<Comments>> call, Throwable t) {
                    Log.i("failurecomment", "failed");
                }
            });

            ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostCommentActivity.super.onBackPressed();
                }
            });


            img_checkmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!comment_txt.getText().toString().matches("")) {
                        c = new Comments();

                        RetrofitClient.apiServices().getUserDetails(s.getUserID()).enqueue(new Callback<UserDetails>() {
                            @Override
                            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {

                                if (response.isSuccessful()) {
                                    UserDetails ud = response.body();
                                    if (ud != null) {

                                        if (ud.user_name != null)
                                            c.full_name = ud.user_name;
                                        if (ud.profile_pic != null)
                                            c.profile_image = ud.profile_pic;
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDetails> call, Throwable t) {

                            }
                        });

                        c.comment = comment_txt.getText().toString();
                        c.profile_id = user_id;
                        c.video_id = post_id;
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss'Z'", Locale.US);
                        c.datetime = new Date().toString();
                        RetrofitClient.apiServices().postComment(c).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(PostCommentActivity.this, "Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                                    listofcomments.add(c);
                                    cva.notifyDataSetChanged();
                                    comment_txt.setText("");
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(PostCommentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
        }


    }
}
