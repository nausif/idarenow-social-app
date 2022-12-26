package com.alpharelevant.idarenow.data.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.alpharelevant.idarenow.SearchProfileFragment;
import com.alpharelevant.idarenow.data.ViewHolders.SearchViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.UserResult;
import com.alpharelevant.idarenow.data.utils.Constants;
import com.alpharelevant.idarenow.data.utils.Functions;

import java.util.List;


/**
 * Created by NAUSIF on 07-Jan-18.
 */

public class SearchViewAdapter extends BaseAdapter {

    Context c;
    List<UserResult> listofuseraccounts;

    public SearchViewAdapter(Context ctx,List<UserResult> listofua)
    {
        this.c = ctx;
        this.listofuseraccounts = listofua;
    }
    @Override
    public int getCount() {
        return listofuseraccounts.size();
    }

    @Override
    public Object getItem(int i) {
        return listofuseraccounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listofuseraccounts.indexOf(i);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view == null)
        {
            view = inflater.inflate(R.layout.search_model,null);
        }

        SearchViewHolder holder = new SearchViewHolder(view);
        holder.tv.setText(listofuseraccounts.get(i).fullName);
        Bitmap bitmap = Functions.getBitmapFromURL("http://" +listofuseraccounts.get(i).profilePic);
        if(bitmap != null)
        holder.iv.setImageBitmap(bitmap);
        holder.email.setText(listofuseraccounts.get(i).email);

        holder.setItemClickListener(new ItemClickListner() {
            @Override
            public void onItemCLick(View v) {
                Bundle args = new Bundle();
                args.putInt(Constants.search_id,listofuseraccounts.get(i).id);
                Fragment fr = new SearchProfileFragment();
                fr.setArguments(args);
                android.support.v4.app.FragmentManager fragmentManager = ((MainNavigationActivity) c).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_nav_frame_layout,fr);
                ft.commit();

            }
        });

        return view;
    }

}
