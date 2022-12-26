package com.alpharelevant.idarenow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Adapters.MessageListAdapter;
import com.alpharelevant.idarenow.data.models.CustomModel.Message;
import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.StaticObjects;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tabraiz on 6/4/2018.
 */

public class MessageActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    List<MessageFormat> feedList;
    int otherId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {




            super.onCreate(savedInstanceState);
            setContentView(R.layout.messageist_layout);
            feedList = new ArrayList<>();
            mMessageAdapter = new MessageListAdapter(this, feedList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

            mMessageRecycler = findViewById(R.id.reyclerview_message_list);
            mMessageRecycler.setLayoutManager(linearLayoutManager);
            mMessageRecycler.setAdapter(mMessageAdapter);




            otherId = Integer.valueOf(getIntent().getExtras().get("otherId").toString());
            Log.d("MessageActivity", "otherid: " + otherId);
            final Button sendBtn = findViewById(R.id.button_chatbox_send);
            sendBtn.setEnabled(false);
            final EditText editText = findViewById(R.id.edittext_chatbox);

            ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageActivity.super.onBackPressed();
                }
            });
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().trim() != "") {
                        sendBtn.setEnabled(true);
                    } else {
                        sendBtn.setEnabled(false);
                    }
                }
            });
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RetrofitClient.apiServices().postSendMsg(StaticObjects.session.getUserID(), otherId, editText.getText().toString()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.body().booleanValue()) {
                                Log.d("postSendMsg", "onResponse: ");
                                feedList.add(new MessageFormat(otherId, StaticObjects.session.getUserID(), "", "asd", editText.getText().toString(), "Now"));
                                mMessageAdapter.notifyDataSetChanged();
                                editText.setText("");

                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {

                        }
                    });
                }
            });
            RetrofitClient.apiServices().getMessagesFromId(StaticObjects.session.getUserID(), otherId).enqueue(new Callback<MessageFormat[]>() {
                @Override
                public void onResponse(Call<MessageFormat[]> call, Response<MessageFormat[]> response) {
                    MessageFormat[] temp = response.body();
                    if (temp.length > 0) {
                        for (MessageFormat item : temp) {
                            feedList.add(new MessageFormat(item.to_id, item.from_id, item.from_id_name, item.from_id_img, item.message_Description, item.message_Timestamp));
                        }
                        mMessageAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void onFailure(Call<MessageFormat[]> call, Throwable t) {

                }
            });

        }
        catch (Exception e)
        {

        }
    }

}
