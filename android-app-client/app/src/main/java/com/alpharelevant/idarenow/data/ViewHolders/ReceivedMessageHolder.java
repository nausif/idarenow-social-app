package com.alpharelevant.idarenow.data.ViewHolders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.models.CustomModel.Message;
import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat;
import com.alpharelevant.idarenow.data.utils.Functions;

/**
 * Created by Tabraiz on 6/4/2018.
 */

public class ReceivedMessageHolder extends RecyclerView.ViewHolder  {
    TextView messageText, timeText, nameText;
    ImageView profileImage;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText =  itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameText = itemView.findViewById(R.id.text_message_name);
        profileImage = itemView.findViewById(R.id.image_message_profile);

    }

    public void bind(MessageFormat message) {
        messageText.setText(message.message_Description);

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.message_Timestamp);
        nameText.setText(message.from_id_name);

        // Insert the profile image from the URL into the ImageView.
        profileImage.setImageBitmap(Functions.getBitmapFromURL(message.from_id_img.toString()));
    }
}
