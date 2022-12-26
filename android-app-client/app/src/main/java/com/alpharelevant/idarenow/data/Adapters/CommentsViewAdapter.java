package com.alpharelevant.idarenow.data.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alpharelevant.idarenow.MainNavigationActivity;
import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.SearchProfileFragment;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.alpharelevant.idarenow.data.ViewHolders.CommentsViewHolder;
import com.alpharelevant.idarenow.data.ViewHolders.SearchViewHolder;
import com.alpharelevant.idarenow.data.models.CustomModel.Comments;
import com.alpharelevant.idarenow.data.models.CustomModel.UserResult;
import com.alpharelevant.idarenow.data.utils.Functions;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by NAUSIF on 07-Jan-18.
 */

public class CommentsViewAdapter extends BaseAdapter {

    Context c;
    List<Comments> listofusercomments;

    public CommentsViewAdapter(Context ctx, List<Comments> listofcomments)
    {
        this.c = ctx;
        this.listofusercomments = listofcomments;
    }
    @Override
    public int getCount() {
        return listofusercomments.size();
    }

    @Override
    public Object getItem(int i) {
        return listofusercomments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listofusercomments.indexOf(i);
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view == null)
        {
            view = inflater.inflate(R.layout.comment_model,null);
        }

        CommentsViewHolder holder = new  CommentsViewHolder(view);
        holder.comment_profile_name.setText(listofusercomments.get(i).full_name);
        holder.comment_profile_description.setText(listofusercomments.get(i).comment);
        holder.comment_time_posted.setText(listofusercomments.get(i).datetime.toString());
        holder.comment_profile_image.setImageBitmap(Functions.getBitmapFromURL("http://"+listofusercomments.get(i).profile_image));

        return view;
    }



}
