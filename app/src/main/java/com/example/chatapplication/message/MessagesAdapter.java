package com.example.chatapplication.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends BaseAdapter {
    ArrayList<MessagesList> messagesListArrayList = new ArrayList<>();
    Context context;
    public MessagesAdapter(ArrayList<MessagesList> messagesListArrayList, Context context) {
        this.messagesListArrayList = messagesListArrayList;
        this.context = context;
    }

    public void setMessagesListArrayList(ArrayList<MessagesList> messagesListArrayList) {
        this.messagesListArrayList = messagesListArrayList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return messagesListArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesListArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = View.inflate(parent.getContext(), R.layout.message_adapter, null);
        CircleImageView circleImageView = v.findViewById(R.id.proFilePic);
        TextView txtUserName = v.findViewById(R.id.txtChatRoommate);
        TextView txtLastMessage = v.findViewById(R.id.txtLastMessage);
        MessagesList item = (MessagesList) getItem(position);
        txtUserName.setText(item.getName());
        txtLastMessage.setText("Chưa nhắn gì nha bạn");
        Picasso.get().load(item.getProfilePic()).into(circleImageView);
        return v;
    }
}
