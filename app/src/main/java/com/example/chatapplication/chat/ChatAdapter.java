package com.example.chatapplication.chat;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;
import com.example.chatapplication.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder>{
    private static final int MSG_TYPE_LEFT = 0, MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> modelChatList;
    String imageURL;
    FirebaseUser user;

    public ChatAdapter(Context context, List<ModelChat> modelChatList, String imageURL) {
        this.context = context;
        this.modelChatList = modelChatList;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT){
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message = modelChatList.get(position).getMessage();
        String time = modelChatList.get(position).getTime();
//        Calendar cld = Calendar.getInstance(Locale.ENGLISH);
//        cld.setTimeInMillis(Long.parseLong(time));
//        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", cld).toString();
        holder.message.setText(message);
        holder.time.setText(time);
        try{
            Picasso.get().load(imageURL).into(holder.imgProfileChat);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return modelChatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (modelChatList.get(position).getSender().equals(user.getEmail())){
            return MSG_TYPE_RIGHT;
        }else return MSG_TYPE_LEFT;
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        TextView message, time;
        CircleImageView imgProfileChat;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.txtChatMessage);
            time = itemView.findViewById(R.id.txtTime);
            imgProfileChat = itemView.findViewById(R.id.profilePicChat);
        }
    }
}
