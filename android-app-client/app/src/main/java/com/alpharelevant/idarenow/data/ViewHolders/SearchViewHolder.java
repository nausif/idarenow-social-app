package com.alpharelevant.idarenow.data.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;

/**
 * Created by NAUSIF on 13-Jan-18.
 */



public class SearchViewHolder implements View.OnClickListener  {

    public ImageView iv ;
    public TextView tv ;
    public TextView email;
    ItemClickListner itemClickListner;
    public  SearchViewHolder(View view)
    {
         iv = (ImageView) view.findViewById(R.id.userImage);
         tv = (TextView) view.findViewById(R.id.user_nameTxt);
         email = (TextView) view.findViewById(R.id.searchuser_Email);
         view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListner.onItemCLick(view);
    }

    public void setItemClickListener(ItemClickListner ic)
    {
        this.itemClickListner=ic;
    }
}
