package com.alpharelevant.idarenow.data.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.Interfaces.ItemClickListner;
import com.alpharelevant.idarenow.data.background_services.NotificationService;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by NAUSIF on 16-Feb-18.
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      public TextView txtNotificationText,txtNotificationDate;
      public CircularImageView imgProfileNotification;
      ItemClickListner itemClickListner;
      public NotificationViewHolder(View view)
      {
          super(view);
          txtNotificationText= (TextView) view.findViewById(R.id.notification_txt);
          txtNotificationDate = (TextView) view.findViewById(R.id.notification_dateTime);
          imgProfileNotification = (CircularImageView) view.findViewById(R.id.notification_profile_image);
          view.setOnClickListener(this);
      }

    @Override
    public void onClick(View view) {
        this.itemClickListner.onItemCLick(view);
    }

    public void setItemClickListener(ItemClickListner ic)
    {
        Log.d("setItemClickListener","asdasd");

        this.itemClickListner=ic;
    }

}
