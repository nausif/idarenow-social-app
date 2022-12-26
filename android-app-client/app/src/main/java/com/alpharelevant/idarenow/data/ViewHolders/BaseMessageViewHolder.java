package com.alpharelevant.idarenow.data.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;

/**
 * Created by Tabraiz on 6/29/2018.
 */

public class BaseMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView msg;
    public TextView duration;
    public ImageView profile_image;
    public BaseMessageViewHolder(View itemView) {
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.title);
        msg = (TextView)itemView.findViewById(R.id.secondline);
        profile_image = (ImageView) itemView.findViewById(R.id.icon);
    }
}
