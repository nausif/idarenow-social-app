package com.alpharelevant.idarenow.data.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.MessageActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.ViewHolders.BaseMessageViewHolder;
import com.alpharelevant.idarenow.data.models.baseFeedModel;
import com.alpharelevant.idarenow.data.utils.Functions;

import java.util.List;

/**
 * Created by Tabraiz on 6/29/2018.
 */

public class BaseMessageAdapter extends RecyclerView.Adapter<BaseMessageViewHolder> {
    Context c;
    List<baseFeedModel> listofBaseMessages;

    public BaseMessageAdapter(Context context,List<baseFeedModel> listofBaseMessages) {
        Log.d("construct", "BaseMessageAdapter: ");
        c= context;
        this.listofBaseMessages = listofBaseMessages;
    }

    @Override
    public BaseMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View baseView= inflater.inflate(R.layout.users_messages_list, parent, false);

        // Return a new holder instance
        BaseMessageViewHolder viewHolder = new BaseMessageViewHolder(baseView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseMessageViewHolder holder, final int position) {

        final baseFeedModel baseFeedModel= listofBaseMessages.get(position);
        holder.title.setText(baseFeedModel.title);
        holder.msg.setText(baseFeedModel.msg);
        Bitmap bmp = Functions.getBitmapFromURL(baseFeedModel.profile_image);
        if(bmp!=null){
            holder.profile_image.setImageBitmap(bmp);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(c, MessageActivity.class);
                intent.putExtra("otherId",listofBaseMessages.get(position).otherId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
        });
    }

    @Override

    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return listofBaseMessages.size();
    }




//    @Override
//    public View getView(final int i, View view, ViewGroup viewGroup) {


//        if(listofuseraccounts.get(i).rating!="")
//            holder.rb.setRating(Float.valueOf(listofuseraccounts.get(i).rating));

//        holder.setItemClickListener(new ItemClickListner() {
//            @Override
//            public void onItemCLick(View v) {
//                Bundle args = new Bundle();
//                args.putInt(Constants.search_id,listofuseraccounts.get(i).id);
//                Fragment fr = new SearchProfileFragment();
//                fr.setArguments(args);
//                android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationActivity) c).getSupportFragmentManager();
//                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
//                ft.replace(R.id.main_nav_frame_layout,fr);
//                ft.commit();
//            }
//        });

//        return view;
//    }
}
