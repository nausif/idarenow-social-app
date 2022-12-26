package com.alpharelevant.idarenow.data.ViewHolders;

import android.icu.text.DateTimePatternGenerator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.models.CustomModel.Message;
import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tabraiz on 6/4/2018.
 */

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText;


    public SentMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);

    }
   public void bind(MessageFormat message) {
        messageText.setText(message.message_Description);
        // Format the stored timestamp into a readable String using method.
       Log.d("timestamp", "bind: "+message.message_Timestamp);
       if(message.message_Timestamp!=null)
           timeText.setText(message.message_Timestamp);
       else
           timeText.setText("unknown");
    }
}
