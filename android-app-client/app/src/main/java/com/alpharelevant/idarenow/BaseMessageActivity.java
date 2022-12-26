package com.alpharelevant.idarenow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alpharelevant.idarenow.data.Adapters.BaseMessageAdapter;
import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat2;
import com.alpharelevant.idarenow.data.models.baseFeedModel;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseMessageActivity extends AppCompatActivity {

    private RecyclerView rvBaseMessages;
    private BaseMessageAdapter mMessageAdapter;
    List<baseFeedModel> BasefeedList;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_base_message);
            session = new Session(getApplicationContext());
            BasefeedList = new ArrayList<>();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvBaseMessages = findViewById(R.id.rvBaseMessages);
            mMessageAdapter = new BaseMessageAdapter(getApplicationContext(), BasefeedList);
            rvBaseMessages.setLayoutManager(linearLayoutManager);
            rvBaseMessages.setAdapter(mMessageAdapter);

            ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseMessageActivity.super.onBackPressed();
                }
            });

            RetrofitClient.apiServices().getAllMessages(session.getUserID()).enqueue(new Callback<MessageFormat2[]>() {
                MessageFormat2[] messageFormat2;

                @Override
                public void onResponse(Call<MessageFormat2[]> call, Response<MessageFormat2[]> response) {
                    messageFormat2 = response.body();
                    if (messageFormat2 != null && messageFormat2.length > 0) {
                        Log.d("messageFormat2", "onResponse: " + messageFormat2.length);
//                    int curSize = mMessageAdapter.getItemCount();
                        for (MessageFormat2 post : messageFormat2) {
                            int otherId;
                            if (post.from_id == session.getUserID()) {
                                otherId = post.to_id;
                            } else {
                                otherId = post.from_id;
                            }
                            BasefeedList.add(new baseFeedModel(post.title_name, post.message_Description, post.from_id_img, otherId));
                        }
                        mMessageAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("messageFormat2", "onResponse: Null");
                    }
                }

                @Override
                public void onFailure(Call<MessageFormat2[]> call, Throwable t) {
                    Log.d("getAllMessages", "onFailure: ");
                }
            });
        }
        catch (Exception e)
        {

        }
        }



}
