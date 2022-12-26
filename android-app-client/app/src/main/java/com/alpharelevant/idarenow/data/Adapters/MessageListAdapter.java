package com.alpharelevant.idarenow.data.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alpharelevant.idarenow.R;
import com.alpharelevant.idarenow.data.ViewHolders.ReceivedMessageHolder;
import com.alpharelevant.idarenow.data.ViewHolders.SentMessageHolder;

import com.alpharelevant.idarenow.data.models.CustomModel.MessageFormat;
import com.alpharelevant.idarenow.data.utils.Session;
import com.alpharelevant.idarenow.data.utils.StaticObjects;

import java.util.List;

/**
 * Created by Tabraiz on 6/4/2018.
 */

public class MessageListAdapter  extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context mContext;
    private List<MessageFormat> mMessageList;
    Session session;
    public MessageListAdapter(Context context, List<MessageFormat> messageList) {
        mContext = context;
        mMessageList = messageList;

    }

    @Override
    public int getItemViewType(int position) {

        MessageFormat message = mMessageList.get(position);
        if ((message.from_id==StaticObjects.session.getUserID())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageFormat message =  mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
